package edp.wat.edu.pl.projectkb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().add(
                getClass().getResource("/edp/wat/edu/pl/projectkb/styles/login-style.css").toExternalForm()
        );

        stage.setTitle("Prognoza Pogody");

        Image icon = new Image(getClass().getResourceAsStream("/snowflake_7587443.png"));
        stage.getIcons().add(icon);

        stage.setWidth(500);
        stage.setHeight(550);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}