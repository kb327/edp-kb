package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.service.singleton.UserServiceSingl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Uzupełnij wszystkie pola", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            boolean success = UserServiceSingl.getInstance().login(username, password);
            if (success) {
                openWeatherView();
            } else {
                showAlert("Nieprawidłowe dane logowania", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd podczas logowania", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/edp/wat/edu/pl/projectkb/styles/register-style.css").toExternalForm()
            );

            Stage stage = new Stage();

            stage.setTitle("Prognoza Pogody");

            Image icon = new Image(getClass().getResourceAsStream("/snowflake_7587443.png"));
            stage.getIcons().add(icon);

            stage.setScene(scene);
            stage.setWidth(500);
            stage.setHeight(700);
            stage.show();

            // Zamykamy okno logowania
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // można wyswietlic alert z błędem
        }
    }

    private void openWeatherView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/weather-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();

        stage.setTitle("Prognoza Pogody");

        Image icon = new Image(getClass().getResourceAsStream("/snowflake_7587443.png"));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        // Zamknij okno logowania
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
