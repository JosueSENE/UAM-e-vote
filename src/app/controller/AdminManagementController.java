package app.controller;

import app.view.AdminManagementView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminManagementController extends BorderPane {

    private final AdminManagementView view;

    public AdminManagementController() {
        this.view = new AdminManagementView();
        this.setCenter(this.view);
        this.view.getBtnRetour().setOnAction(e -> retourAuTableauBord());
    }

    private void retourAuTableauBord() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
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
