package app.controller;

import app.view.StatisticsView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class StatisticsController { // Plus de "extends BorderPane"

    private final StatisticsView view;

    // Le constructeur reçoit la vue depuis le Dashboard
    public StatisticsController(StatisticsView view) {
        this.view = view;
        this.view.getBtnRetour().setOnAction(e -> retourAuTableauBord());
    }

    private void retourAuTableauBord() {
        try {
            Stage stage = (Stage) this.view.getScene().getWindow(); // On récupère le Stage via la vue
            AdminDashboardController dashboardController = new AdminDashboardController();
            Scene scene = new Scene(dashboardController, 1400, 700);
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}