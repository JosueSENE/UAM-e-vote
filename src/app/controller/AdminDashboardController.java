package app.controller;

import app.dao.UserDAO;
import app.view.AdminDashboardView;
import app.view.LoginView;            
import app.view.StatisticsView; 

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AdminDashboardController extends BorderPane {

    private final AdminDashboardView view;
    private final UserDAO userDAO;

    public AdminDashboardController() {
        this.view = new AdminDashboardView();
        this.userDAO = new UserDAO();
        
        this.setCenter(this.view);

        // Liaison des actions sur les boutons de la vue
        this.view.getBtnGestionUtilisateurs().setOnAction(e -> ouvrirGestionUtilisateurs());
        this.view.getBtnGestionAdministrateurs().setOnAction(e -> ouvrirGestionAdministrateurs());
        this.view.getBtnStatistiques().setOnAction(e -> ouvrirStatistiques());
        this.view.getBtnRetourConnexion().setOnAction(e -> retourConnexion());
        
        // ✅ CHARGEMENT DES STATISTIQUES AU DÉMARRAGE
        chargerStatistiques();
    }

    // ==========================================
    // ✅ CHARGEMENT DES STATISTIQUES
    // ==========================================
    
    /**
     * Charge les statistiques depuis la base de données et met à jour la vue
     */
    private void chargerStatistiques() {
        try {
            // Récupérer les statistiques via le DAO
            UserDAO.DashboardStats stats = userDAO.getDashboardStats();
            
            // Mettre à jour la vue avec les données réelles
            view.updateStats(
                stats.totalEtudiants,
                stats.totalEnseignants,
                stats.totalAdmins,
                stats.votants,
                stats.nonVotants,
                stats.enLigne,
                stats.horsLigne
            );
            
            System.out.println("✅ Statistiques chargées avec succès !");
            System.out.println("   - Étudiants : " + stats.totalEtudiants);
            System.out.println("   - Enseignants : " + stats.totalEnseignants);
            System.out.println("   - Admins : " + stats.totalAdmins);
            System.out.println("   - Votants : " + stats.votants);
            System.out.println("   - Non votants : " + stats.nonVotants);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des statistiques : " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur Base de données", 
                "Impossible de charger les statistiques.\n" +
                "Vérifiez que la base de données est accessible.\n\n" +
                "Erreur : " + e.getMessage());
        }
    }

    /**
     * Rafraîchit les statistiques (peut être appelé après une modification)
     */
    public void rafraichirStatistiques() {
        chargerStatistiques();
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    /**
     * Ouvre l'interface de gestion des utilisateurs (Électeurs).
     */
    private void ouvrirGestionUtilisateurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            
            AdminController adminController = new AdminController();
            Scene scene = new Scene(adminController, 1400, 700); 
            
            stage.setTitle("UAM e-Vote - Gestion des Électeurs");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des électeurs.");
        }
    }

    /**
     * Ouvre l'interface de gestion des administrateurs.
     */
    private void ouvrirGestionAdministrateurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            
            AdminManagementController adminManagementController = new AdminManagementController();
            Scene scene = new Scene(adminManagementController, 1400, 700);

            stage.setTitle("UAM e-Vote - Gestion des administrateurs");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des administrateurs.");
        }
    }

    /**
     * Ouvre l'interface des statistiques.
     */
    private void ouvrirStatistiques() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            StatisticsView statisticsView = new StatisticsView();
            
            new StatisticsController(statisticsView);
            
            Scene scene = new Scene(statisticsView, 1400, 700);
            stage.setTitle("UAM e-Vote - Statistiques");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible d'ouvrir les statistiques.");
        }
    }

    /**
     * Déconnexion et retour à la mire de login.
     */
    private void retourConnexion() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);
            
            Scene scene = new Scene(loginView, 1400, 700);
            stage.setTitle("UAM e-Vote - Connexion");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de revenir à la page de connexion.");
        }
    }

    // ==========================================
    // UTILITAIRES
    // ==========================================
    
    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}