package edp.wat.edu.pl.projectkb.controller;

import edp.wat.edu.pl.projectkb.model.WeatherHistory;
import edp.wat.edu.pl.projectkb.persictence.DbPersistenceUnit;
import edp.wat.edu.pl.projectkb.session.SessionContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.List;

public class WeatherHistoryController {

    @FXML
    private ListView<WeatherHistory> historyListView;

    private Scene previousScene;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    public void initialize() {
        setupListViewCellFactory();
        loadHistory();
    }

    private void setupListViewCellFactory() {
        historyListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(WeatherHistory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // ustawienie czcionki
                    setFont(Font.font("System", 14));

                    // formatowanie wpisu
                    setText(item.getDate() + " | " + item.getLocationName() + ", " + item.getCountry());
                }
            }
        });
    }

    private void loadHistory() {
        EntityManager em = DbPersistenceUnit.getEntityManager();

        historyListView.getItems().clear();

        try {
            Long userId = SessionContext.getLoggedInUserId();

            TypedQuery<WeatherHistory> query = em.createQuery(
                    "SELECT w FROM WeatherHistory w WHERE w.user.userId = :userId ORDER BY w.date DESC",
                    WeatherHistory.class
            );
            query.setParameter("userId", userId);

            List<WeatherHistory> historyList = query.getResultList();
            historyListView.getItems().addAll(historyList);
            historyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } finally {
            em.close();
        }
    }

    @FXML
    public void viewDetails() {
        WeatherHistory selected = historyListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Wybierz wpis z listy, aby zobaczyć szczegóły.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edp/wat/edu/pl/projectkb/history-details.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/edp/wat/edu/pl/projectkb/styles/history-details-style.css").toExternalForm()
            );

            WeatherHistoryDetailsController controller = loader.getController();
            controller.setWeatherHistory(selected);
            controller.setPreviousScene(historyListView.getScene());

            Stage stage = (Stage) historyListView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Błąd ładowania widoku szczegółów: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void deleteHistory() {
        WeatherHistory selected = historyListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Wybierz wpis z listy, aby go usunąć.", Alert.AlertType.INFORMATION);
            return;
        }

        // Potwierdzenie
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Potwierdzenie usunięcia");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Czy na pewno chcesz usunąć ten wpis z historii?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return; // użytkownik anulował
        }

        EntityManager em = DbPersistenceUnit.getEntityManager();
        em.getTransaction().begin();

        try {
            WeatherHistory toDelete = em.find(WeatherHistory.class, selected.getLocationId());
            if (toDelete != null) {
                em.remove(toDelete);
                em.getTransaction().commit();
                historyListView.getItems().remove(selected);
                showAlert("Usunięto wpis z historii.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Nie znaleziono wpisu do usunięcia.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            showAlert("Błąd podczas usuwania: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            em.close();
        }
    }

    @FXML
    public void goBack() {
        Stage stage = (Stage) historyListView.getScene().getWindow();
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
