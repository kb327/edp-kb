package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherForecastResponse;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherList;
import edp.wat.edu.pl.projectkb.service.singleton.OpenWeatherApiServiceSingl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class WeatherViewController {
    @FXML
    private VBox vBoxId;
    @FXML
    private Button showButton;


    @FXML
    public void showWeather(MouseEvent mouseEvent) throws IOException, InterruptedException {
        new Thread(() -> {
            WeatherList weatherList = null;
            try {
                weatherList = OpenWeatherApiServiceSingl.getInstance().getWeatherList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < 7; i++) {
                showDayWeather(weatherList.getList().get(i));
            }
        }).start();
    }

    private void showDayWeather(WeatherForecastResponse weatherForecastResponse) {
        HBox hBox = new HBox();

        Long time = weatherForecastResponse.getDateTime();
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);

        Label date = new Label(dateTime.toString() + "  |  ");
        Label temp = new Label(weatherForecastResponse.getMain().getTemperature().toString() + "  |  ");
        Label feelsLike = new Label(weatherForecastResponse.getMain().getFeelsLike().toString() + "  |  ");
        Label desc = new Label(weatherForecastResponse.getList().getFirst().getDescription());

        hBox.getChildren().addAll(date, temp, feelsLike, desc);
        vBoxId.getChildren().add(hBox);
    }
}
