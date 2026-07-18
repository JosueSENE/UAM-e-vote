package app.controller;

import app.view.AdminManagementView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminManagementController extends BorderPane {

    private final AdminManagementView view;
    private final Stage primaryStage; // Conserve la référence directe de la fenêtre

    public AdminManagementController(Stage stage) {
        this.primaryStage = stage;
        this.view = new AdminManagementView();
        this.setCenter(this.view);
        
        this.view.getBtnRetour().setOnAction(e -> retourAuTableauBord());
    }

    private void retourAuTableauBord() {
        try {
            // Plus besoin de deviner ou chercher la Scene, on utilise la référence directe
            AdminDashboardController dashboardController = new AdminDashboardController(primaryStage);
            Scene scene = new Scene(dashboardController, 1400, 700);
            
            primaryStage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Erreur lors du retour au tableau de bord : " + e.getMessage());
            e.printStackTrace();
        }
    }
}