package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.model.FavoriteLocation;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.LocationResult;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherForecastResponse;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherList;
import edp.wat.edu.pl.projectkb.service.singleton.FavoriteLocationServiceSingl;
import edp.wat.edu.pl.projectkb.service.singleton.OpenWeatherApiServiceSingl;
import edp.wat.edu.pl.projectkb.session.SessionContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherViewController {
    @FXML private VBox vBoxId;
    @FXML private TextField cityField;
    @FXML private Button showButton;
    @FXML private Button logoutButton;
    @FXML private ComboBox<LocationResult> locationComboBox;
    @FXML private Label emptyInfoLabel;

    @FXML private VBox favoritesVBox;
    @FXML private TextField favoriteCityField;
    @FXML private ComboBox<LocationResult> favoriteLocationComboBox;

    private Map<LocalDate, List<WeatherForecastResponse>> fullGroupedData = new HashMap<>();
    private LocationResult currentLocation;

    @FXML
    public void initialize() {
        loadFavorites();
        emptyInfoLabel.setVisible(true);

        Platform.runLater(() -> {
            Scene scene = logoutButton.getScene();
            if (scene != null) {
                scene.getStylesheets().add(
                        getClass().getResource("/edp/wat/edu/pl/projectkb/styles/weather-style.css").toExternalForm()
                );
            }
        });
    }

    @FXML
    public void searchCities(MouseEvent event) {
        String city = cityField.getText();
        if (city == null || city.isBlank()) {
            showAlert("Wpisz nazwę miasta!", Alert.AlertType.WARNING);
            return;
        }

        new Thread(() -> {
            try {
                List<LocationResult> results = OpenWeatherApiServiceSingl.getInstance().searchLocations(city);
                Platform.runLater(() -> {
                    locationComboBox.getItems().clear();
                    locationComboBox.getItems().addAll(results);
                    locationComboBox.setConverter(new StringConverter<>() {
                        @Override
                        public String toString(LocationResult loc) {
                            return loc.getName() + ", " + loc.getState() + ", " + loc.getCountry();
                        }

                        @Override
                        public LocationResult fromString(String string) {
                            return null;
                        }
                    });
                    locationComboBox.getSelectionModel().selectFirst();
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Błąd podczas wyszukiwania: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    @FXML
    public void showWeather(MouseEvent mouseEvent) {
        LocationResult selectedLocation = locationComboBox.getValue();
        if (selectedLocation == null) {
            showAlert("Najpierw wybierz lokalizację!", Alert.AlertType.WARNING);
            return;
        }

        currentLocation = selectedLocation;

        // Ukryj etykietę „Wyszukaj lokalizację...” przy każdej próbie pobrania
        emptyInfoLabel.setVisible(false);

        fetchAndDisplayWeather(selectedLocation);

        // Jeśli po pobraniu VBox z pogodą jest pusty — pokaż etykietę informacyjną
        if (vBoxId.getChildren().isEmpty()) {
            emptyInfoLabel.setVisible(true);
        }
    }


    private void fetchAndDisplayWeather(LocationResult location) {
        new Thread(() -> {
            try {
                WeatherList weatherList = OpenWeatherApiServiceSingl.getInstance()
                        .getWeatherByCoordinates(location.getLat(), location.getLon());

                fullGroupedData = groupByDate(weatherList.getList());

                Platform.runLater(() -> {
                    vBoxId.getChildren().clear();

                    WeatherForecastResponse current = findClosestToNow(weatherList.getList());
                    if (current != null) {
                        String icon = current.getList().get(0).getIcon();
                        ImageView iconView = new ImageView(new Image("https://openweathermap.org/img/wn/" + icon + "@2x.png"));
                        iconView.setFitHeight(50);
                        iconView.setFitWidth(50);

                        HBox nowBox = new HBox(iconView,
                                new Label("Aktualna pogoda: "),
                                new Label(current.getMain().getTemperature() + "°C, "),
                                new Label(current.getList().get(0).getDescription() + ", "),
                                new Label("Wilgotność: " + current.getMain().getHumidity() + "%, "),
                                new Label("Wiatr: " + current.getWind().getSpeed() + " m/s"));
                        vBoxId.getChildren().add(nowBox);
                    }

                    fullGroupedData.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .limit(5)
                            .forEach(entry -> {
                                LocalDate date = entry.getKey();
                                List<WeatherForecastResponse> forecasts = entry.getValue();

                                double avgDay = forecasts.stream()
                                        .filter(f -> "d".equals(f.getSys().getPod()))
                                        .mapToDouble(f -> f.getMain().getTemperature())
                                        .average().orElse(0.0);

                                double avgNight = forecasts.stream()
                                        .filter(f -> "n".equals(f.getSys().getPod()))
                                        .mapToDouble(f -> f.getMain().getTemperature())
                                        .average().orElse(0.0);

                                String commonIcon = forecasts.stream()
                                        .map(f -> f.getList().get(0).getIcon())
                                        .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                                        .entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey)
                                        .orElse("01d");

                                ImageView iconView = new ImageView(new Image("https://openweathermap.org/img/wn/" + commonIcon + "@2x.png"));
                                iconView.setFitWidth(50);
                                iconView.setFitHeight(50);

                                String dayName = date.format(DateTimeFormatter.ofPattern("EEEE", new Locale("pl", "PL")));

                                Button moreButton = new Button("Zobacz więcej");
                                moreButton.getStyleClass().add("main-button");
                                moreButton.setOnAction(e -> openDailyView(date, forecasts));

                                HBox dayBox = new HBox(iconView,
                                        new Label(dayName + " (" + date.getDayOfMonth() + "." + date.getMonthValue() + "): "),
                                        new Label("Dzień: " + String.format("%.1f", avgDay) + "°C, "),
                                        new Label("Noc: " + String.format("%.1f", avgNight) + "°C, "),
                                        moreButton);
                                vBoxId.getChildren().add(dayBox);
                            });
                    if (vBoxId.getChildren().isEmpty()) {
                        emptyInfoLabel.setVisible(true);
                    } else {
                        emptyInfoLabel.setVisible(false);
                    }
                });

            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Błąd pobierania danych pogodowych: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void openDailyView(LocalDate date, List<WeatherForecastResponse> forecasts) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/daily-weather.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/edp/wat/edu/pl/projectkb/styles/daily-weather-style.css").toExternalForm()
            );

            DailyWeatherController controller = loader.getController();

            controller.setForecastsForDay(date, forecasts);
            controller.setLocationDetails(
                    currentLocation.getName(),
                    currentLocation.getState(),
                    currentLocation.getCountry(),
                    currentLocation.getLat(),
                    currentLocation.getLon()
            );
            controller.setPreviousScene(vBoxId.getScene());

            Stage stage = (Stage) vBoxId.getScene().getWindow();
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Nie udało się załadować widoku szczegółowego:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void logout(MouseEvent event) {
        SessionContext.clear();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/edp/wat/edu/pl/projectkb/styles/login-style.css").toExternalForm()
            );

            Stage stage = new Stage();

            stage.setTitle("Prognoza Pogody");

            Image icon = new Image(getClass().getResourceAsStream("/snowflake_7587443.png"));
            stage.getIcons().add(icon);

            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(550);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        currentStage.close();
    }

    private WeatherForecastResponse findClosestToNow(List<WeatherForecastResponse> list) {
        long now = System.currentTimeMillis() / 1000;
        return list.stream()
                .min(Comparator.comparingLong(f -> Math.abs(f.getDateTime() - now)))
                .orElse(null);
    }

    private Map<LocalDate, List<WeatherForecastResponse>> groupByDate(List<WeatherForecastResponse> list) {
        return list.stream().collect(Collectors.groupingBy(f ->
                LocalDateTime.ofEpochSecond(f.getDateTime(), 0, ZoneOffset.UTC).toLocalDate()
        ));
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void searchFavorites(MouseEvent event) {
        String city = favoriteCityField.getText();
        if (city == null || city.isBlank()) {
            showAlert("Wpisz nazwę miasta!", Alert.AlertType.WARNING);
            return;
        }

        new Thread(() -> {
            try {
                List<LocationResult> results = OpenWeatherApiServiceSingl.getInstance().searchLocations(city);
                Platform.runLater(() -> {
                    favoriteLocationComboBox.getItems().clear();
                    favoriteLocationComboBox.getItems().addAll(results);
                    favoriteLocationComboBox.setConverter(new StringConverter<>() {
                        @Override
                        public String toString(LocationResult loc) {
                            return loc.getName() + ", " + loc.getState() + ", " + loc.getCountry();
                        }

                        @Override
                        public LocationResult fromString(String string) {
                            return null;
                        }
                    });
                    favoriteLocationComboBox.getSelectionModel().selectFirst();
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Błąd: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    @FXML
    public void addFavorite(MouseEvent event) {
        LocationResult selected = favoriteLocationComboBox.getValue();
        if (selected == null) {
            showAlert("Wybierz lokalizację z listy!", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean added = FavoriteLocationServiceSingl.getInstance().addFavoriteLocation(
                    selected.getName(), selected.getState(), selected.getCountry(),
                    selected.getLat(), selected.getLon()
            );

            if (added) {
                loadFavorites();
                showAlert("Dodano do ulubionych.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Lokalizacja już istnieje w ulubionych.", Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            showAlert("Błąd: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void loadFavorites() {
        favoritesVBox.getChildren().clear();
        List<FavoriteLocation> favorites = FavoriteLocationServiceSingl.getInstance().getUserFavorites();

        if (favorites.isEmpty()) {
            favoritesVBox.getChildren().add(new Label("Nie masz jeszcze ulubionych lokalizacji."));
            return;
        }

        for (FavoriteLocation loc : favorites) {
            HBox box = new HBox();
            Label label = new Label(loc.getName() + ", " + loc.getState() + ", " + loc.getCountry());

            Button showBtn = new Button("Zobacz pogodę");
            showBtn.getStyleClass().add("main-button");
            showBtn.setOnAction(e -> {
                LocationResult pseudo = new LocationResult(loc.getName(), loc.getState(), loc.getCountry(), loc.getLat(), loc.getLon());
                currentLocation = pseudo;
                fetchAndDisplayWeather(pseudo);
            });

            Button deleteBtn = new Button("Usuń");
            deleteBtn.getStyleClass().add("logout-button");
            deleteBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdzenie usunięcia");
                alert.setHeaderText(null);
                alert.setContentText("Czy na pewno chcesz usunąć tę lokalizację z ulubionych?");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        FavoriteLocationServiceSingl.getInstance().deleteFavorite(loc.getId());
                        loadFavorites();
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText(null);
                        info.setContentText("Lokalizacja została usunięta.");
                        info.showAndWait();
                    }
                });
            });

            box.getChildren().addAll(label, showBtn, deleteBtn);
            favoritesVBox.getChildren().add(box);
        }
    }

    @FXML
    public void openHistory(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/history-view.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/edp/wat/edu/pl/projectkb/styles/history-view-style.css").toExternalForm()
            );

            WeatherHistoryController controller = loader.getController();
            controller.setPreviousScene(logoutButton.getScene());

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setWidth(800);
            stage.setHeight(650);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Nie udało się otworzyć historii: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
