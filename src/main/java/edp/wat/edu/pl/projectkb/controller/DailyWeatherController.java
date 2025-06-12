package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.model.FavoriteLocation;
import edp.wat.edu.pl.projectkb.model.User;
import edp.wat.edu.pl.projectkb.model.WeatherHistory;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherForecastResponse;
import edp.wat.edu.pl.projectkb.persictence.DbPersistenceUnit;
import edp.wat.edu.pl.projectkb.service.singleton.WeatherHistoryServiceSingl;
import edp.wat.edu.pl.projectkb.session.SessionContext;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailyWeatherController {

    @FXML
    private VBox hourlyVBox;

    private Long locationId;  // tylko dla ulubionych
    private String locationName;
    private String state;
    private String country;
    private double lat;
    private double lon;

    private LocalDate currentDate;
    private List<WeatherForecastResponse> forecasts;
    private Scene previousScene;

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    public void setForecastsForDay(LocalDate date, List<WeatherForecastResponse> forecasts) {
        this.currentDate = date;
        this.forecasts = forecasts;
        displayHourlyForecasts();
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public void setLocationDetails(String name, String state, String country, double lat, double lon) {
        this.locationName = name;
        this.state = state;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    private void displayHourlyForecasts() {
        hourlyVBox.getChildren().clear();

        for (WeatherForecastResponse forecast : forecasts) {
            HBox hBox = new HBox(10);

            LocalDateTime time = LocalDateTime.ofEpochSecond(forecast.getDateTime(), 0, ZoneOffset.UTC);
            String temp = forecast.getMain().getTemperature() + "°C";
            String description = forecast.getList().get(0).getDescription();
            String humidity = "Wilgotność: " + forecast.getMain().getHumidity() + "%";
            String wind = "Wiatr: " + forecast.getWind().getSpeed() + " m/s";
            String rain = (forecast.getRain() != null && forecast.getRain().getAmount() != null)
                    ? "Opady: " + forecast.getRain().getAmount() + " mm"
                    : "Opady: 0 mm";

            String iconCode = forecast.getList().get(0).getIcon();
            ImageView icon = new ImageView(new Image("https://openweathermap.org/img/wn/" + iconCode + "@2x.png"));
            icon.setFitWidth(50);
            icon.setFitHeight(50);

            hBox.getChildren().addAll(
                    icon,
                    new Label(time.toString() + " | "),
                    new Label(temp + ", "),
                    new Label(description + ", "),
                    new Label(humidity + ", "),
                    new Label(wind + ", "),
                    new Label(rain)
            );

            hourlyVBox.getChildren().add(hBox);
        }
    }

    @FXML
    public void saveWeatherHistory() {
        if (forecasts == null || forecasts.isEmpty()) {
            showAlert("Brak danych do zapisania.", Alert.AlertType.WARNING);
            return;
        }

        EntityManager em = DbPersistenceUnit.getEntityManager();

        try {
            User user = em.find(User.class, SessionContext.getLoggedInUserId());
            if (user == null) {
                showAlert("Nie znaleziono użytkownika.", Alert.AlertType.ERROR);
                return;
            }

            if (locationId != null) {
                FavoriteLocation loc = em.find(FavoriteLocation.class, locationId);
                if (loc != null) {
                    locationName = loc.getName();
                    state = loc.getState();
                    country = loc.getCountry();
                    lat = loc.getLat();
                    lon = loc.getLon();
                } else {
                    showAlert("Nie znaleziono lokalizacji o ID: " + locationId, Alert.AlertType.ERROR);
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (WeatherForecastResponse forecast : forecasts) {
                LocalDateTime time = LocalDateTime.ofEpochSecond(forecast.getDateTime(), 0, ZoneOffset.UTC);
                String temp = forecast.getMain().getTemperature() + "°C";
                String description = forecast.getList().get(0).getDescription();
                String humidity = forecast.getMain().getHumidity() + "%";
                String wind = forecast.getWind().getSpeed() + " m/s";
                String rain = (forecast.getRain() != null && forecast.getRain().getAmount() != null)
                        ? forecast.getRain().getAmount() + " mm"
                        : "0 mm";

                sb.append(time.format(formatter))
                        .append(" | ")
                        .append(temp).append(", ")
                        .append(description).append(", ")
                        .append("Wilgotność: ").append(humidity).append(", ")
                        .append("Wiatr: ").append(wind).append(", ")
                        .append("Opady: ").append(rain)
                        .append("\n");
            }

            WeatherHistory history = WeatherHistory.builder()
                    .user(user)
                    .date(currentDate)
                    .locationName(locationName)
                    .state(state)
                    .country(country)
                    .lat(lat)
                    .lon(lon)
                    .weatherData(sb.toString())
                    .build();

            boolean saved = WeatherHistoryServiceSingl.getInstance().save(history);

            if (saved) {
                showAlert("Zapisano prognozę do historii.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Taka prognoza już istnieje w historii.", Alert.AlertType.WARNING);
            }

        } finally {
            em.close();
        }
    }

    @FXML
    public void goBack() {
        Stage stage = (Stage) hourlyVBox.getScene().getWindow();
        stage.setScene(previousScene);
        stage.setWidth(1200);
        stage.setHeight(800);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
