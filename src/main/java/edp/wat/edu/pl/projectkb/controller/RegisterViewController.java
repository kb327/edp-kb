package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.service.singleton.UserServiceSingl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class RegisterViewController {
    @FXML private TextField usernameText;
    @FXML private TextField emailText;
    @FXML private PasswordField passwdText;
    @FXML private PasswordField passwdSecText;
    @FXML private TextField nameText;
    @FXML private TextField surnameText;

    @FXML
    public void register(MouseEvent mouseEvent) {
        if(usernameText.getText().isEmpty() || emailText.getText().isEmpty() || passwdText.getText().isEmpty() || passwdSecText.getText().isEmpty()) {
            showAlert("Podaj wszytskie dane", Alert.AlertType.INFORMATION);
            return;
        }

        if(!passwdText.getText().equals(passwdSecText.getText())) {
            showAlert("Hasła się różnią", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            UserServiceSingl.getInstance().register(usernameText.getText(), passwdText.getText(), emailText.getText(), nameText.getText(), surnameText.getText());
        } catch(IllegalArgumentException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        showAlert("Pomyslnie zarejestrowano.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
