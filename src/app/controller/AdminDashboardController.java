package app.controller;

import app.dao.UserDAO;
import app.dao.VoteDAO;
import app.dao.AdminDAO;
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
    private final VoteDAO voteDAO;
    private final AdminDAO adminDAO;
    private final UserDAO userDAO;
    

    public AdminDashboardController() {
        this.view = new AdminDashboardView();
        
        // ✅ CORRECT : Initialisation de TOUS les DAO pour éviter les NullPointerException
        this.userDAO = new UserDAO();
        this.voteDAO = new VoteDAO();
        this.adminDAO = new AdminDAO();  
        this.setCenter(this.view);

        // Liaison des actions sur les boutons de la vue
        this.view.getBtnGestionUtilisateurs().setOnAction(e -> ouvrirGestionElecteurs());
        this.view.getBtnGestionEnseignantFiliere().setOnAction(e -> ouvrirGestionEnseignant());
        this.view.getBtnGestionAdministrateurs().setOnAction(e -> ouvrirGestionAdministrateurs());
        this.view.getBtnGestionCandidads().setOnAction(e -> ouvrirGestionCandidats()); 
        this.view.getBtnGestionElections().setOnAction(e -> ouvrirGestionElections());    
        this.view.getBtnGestionUfrs().setOnAction(e -> ouvrirGestionUfrs());
        this.view.getBtnGestionDepartements().setOnAction(e -> ouvrirGestionDepartements()); 
        this.view.getBtnGestionFilieres().setOnAction(e -> ouvrirGestionFilieres());                
        this.view.getBtnStatistiques().setOnAction(e -> ouvrirStatistiques());
        this.view.getBtnRetourConnexion().setOnAction(e -> retourConnexion());
        
        // ✅ CHARGEMENT DES STATISTIQUES AU DÉMARRAGE
        chargerStatistiques();
    }

    // ==========================================
    // ✅ CHARGEMENT DES STATISTIQUES
    // ==========================================
    private void chargerStatistiques() {
        try {
            // Mettre à jour la vue avec les données réelles
            view.updateStats(
                userDAO.getTotalEtudiants(),
                userDAO.getTotalEnseignants(),
                adminDAO.getTotalAdmins(),
                voteDAO.getVotants(),
                voteDAO.getNonVotants()
            );
            
            System.out.println("✅ Statistiques chargées avec succès !");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des statistiques : " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur Base de données", 
                "Impossible de charger les statistiques.\n" +
                "Vérifiez que la base de données est accessible.\n\n" +
                "Erreur : " + e.getMessage());
        }
    }

    public void rafraichirStatistiques() {
        chargerStatistiques();
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    /**
     * Ouvre l'interface de gestion des Électeurs.
     */

    private void ouvrirGestionElecteurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminUsersController adminController = new AdminUsersController();
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
     * Ouvre l'interface de gestion des Enseignants.
     */

    private void ouvrirGestionEnseignant() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminEnseignantController adminEnseignantController = new AdminEnseignantController();
            Scene scene = new Scene(adminEnseignantController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Enseignant");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des enseignants.");
        }
    }

    /**
     * Ouvre l'interface de gestion des Administrateurs.
     */

    private void ouvrirGestionAdministrateurs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminManagementController adminManagementController = new AdminManagementController(stage);
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
     * Ouvre l'interface de gestion des Candidats.
     */

    private void ouvrirGestionCandidats() { 
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminCandidatController adminCandidatController = new AdminCandidatController();
            Scene scene = new Scene(adminCandidatController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Candidats");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Candidats.");
        }
    }

    /**
     * Ouvre l'interface de gestion des Élections.
     */
    
    private void ouvrirGestionElections() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminElectionController adminElectionController = new AdminElectionController();
            Scene scene = new Scene(adminElectionController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Élections");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des élections.");
        }
    }

    /**
    * Ouvre l'interface de gestion des UFRs.
    */

    private void ouvrirGestionUfrs() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminUfrController adminUfrController = new AdminUfrController();
            Scene scene = new Scene(adminUfrController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des UFRs");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des ufrs.");
        }
    }

    /**
    * Ouvre l'interface de gestion des Départements.
    */

    private void ouvrirGestionDepartements() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDepartementController adminDepartementController = new AdminDepartementController();
            Scene scene = new Scene(adminDepartementController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Départemetents");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Départements.");
        }
    }

    /**
    * Ouvre l'interface de gestion des Filières.
    */

    private void ouvrirGestionFilieres() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminFiliereController adminFiliereController = new AdminFiliereController();
            Scene scene = new Scene(adminFiliereController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Filière");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Filières.");
        }
    }

    /**
     * Ouvre l'interface de gestion des Statistiques.
     */

    private void ouvrirStatistiques() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            StatisticsView statisticsView = new StatisticsView();
            Scene scene = new Scene(statisticsView, 1400, 700);
            stage.setTitle("UAM e-Vote - Statistiques");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible d'ouvrir l'interface des statistiques.");
        }
    }

    private void retourConnexion() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
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