package app.controller;

import app.view.AdminDashboardView;
import app.view.LoginView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminDashboardController extends BorderPane {

    private final AdminDashboardView view;

    public AdminDashboardController() {
        this.view = new AdminDashboardView();
        this.setCenter(this.view);

        // Liaison des événements de navigation
        this.view.getBtnGestionUtilisateurs().setOnAction(e -> ouvrirGestionUtilisateurs());
        this.view.getBtnGestionAdministrateurs().setOnAction(e -> ouvrirGestionAdministrateurs());
        this.view.getBtnStatistiques().setOnAction(e -> ouvrirStatistiques());
        this.view.getBtnRetourConnexion().setOnAction(e -> retourConnexion());
    }

    private void ouvrirGestionUtilisateurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminController adminController = new AdminController();
            Scene scene = new Scene(adminController, 1600, 900); 
            stage.setTitle("UAM e-Vote - Gestion des Électeurs");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherErreur("Erreur", "Impossible d'ouvrir la gestion des électeurs.");
        }
    }

    private void ouvrirGestionAdministrateurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminManagementController adminManagementController = new AdminManagementController();
            Scene scene = new Scene(adminManagementController, 1400, 700);
            stage.setTitle("UAM e-Vote - Gestion des Administrateurs");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir la gestion des administrateurs.");
        }
    }

    private void ouvrirStatistiques() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            StatisticsController statisticsController = new StatisticsController();
            Scene scene = new Scene(statisticsController, 1400, 700);
            stage.setTitle("UAM e-Vote - Statistiques");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir les statistiques.");
        }
    }

    private void retourConnexion() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            
            // 1. On instancie d'abord la vue attendue par le constructeur
            LoginView loginView = new LoginView();
            
            // 2. On passe la vue au contrôleur pour résoudre l'erreur Eclipse
            LoginController loginController = new LoginController(loginView);
            
            Scene scene = new Scene(loginController, 1400, 700);
            stage.setTitle("UAM e-Vote - Connexion");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de revenir à la page de connexion.");
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}