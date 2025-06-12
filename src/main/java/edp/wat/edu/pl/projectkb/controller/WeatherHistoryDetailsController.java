package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.model.WeatherHistory;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class WeatherHistoryDetailsController {

    @FXML
    private Label locationLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea weatherDataArea;

    private Scene previousScene;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setWeatherHistory(WeatherHistory history) {
        locationLabel.setText("Lokalizacja: " + history.getLocationName() + ", " + history.getState() + ", " + history.getCountry());
        dateLabel.setText("Data: " + history.getDate().toString());
        weatherDataArea.setText(history.getWeatherData());
    }

    @FXML
    public void goBack() {
        Stage stage = (Stage) locationLabel.getScene().getWindow();
        stage.setScene(previousScene);
    }
}
