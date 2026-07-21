package app.controller;

import app.dao.UserDAO;
import app.dao.VoteDAO;
import app.dao.AdminDAO;
import app.view.AdminDashboardView;
import app.view.LoginView;            
import javafx.scene.Parent;
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
    
    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

    public AdminDashboardController() {
        this.view = new AdminDashboardView();
        this.userDAO = new UserDAO();
        this.voteDAO = new VoteDAO();
        this.adminDAO = new AdminDAO();  
        this.setCenter(this.view);

        // Liaison des actions sur les boutons de la vue
        configurerEvenements();
        
        // Chargement des statistiques au démarrage
        chargerStatistiques();
    }

    // ==========================================
    // CONFIGURATION DES ÉVÉNEMENTS
    // ==========================================

    private void configurerEvenements() {
        this.view.getBtnGestionUtilisateurs().setOnAction(e -> ouvrirGestionElecteurs());
        this.view.getBtnGestionEnseignantFiliere().setOnAction(e -> ouvrirGestionEnseignant());
        this.view.getBtnGestionAdministrateurs().setOnAction(e -> ouvrirGestionAdministrateurs());
        this.view.getBtnGestionCandidats().setOnAction(e -> ouvrirGestionCandidats()); 
        this.view.getBtnGestionElections().setOnAction(e -> ouvrirGestionElections());    
        this.view.getBtnGestionUfrs().setOnAction(e -> ouvrirGestionUfrs());
        this.view.getBtnGestionDepartements().setOnAction(e -> ouvrirGestionDepartements()); 
        this.view.getBtnGestionFilieres().setOnAction(e -> ouvrirGestionFilieres());                
        this.view.getBtnStatistiques().setOnAction(e -> ouvrirStatistiques());
        this.view.getBtnRetourConnexion().setOnAction(e -> retourConnexion());
        
        // Bouton de rafraîchissement (si présent dans la vue)
        if (this.view.getBtnRafraichir() != null) {
            this.view.getBtnRafraichir().setOnAction(e -> chargerStatistiques());
        }
    }

    // ==========================================
    // CHARGEMENT DES STATISTIQUES
    // ==========================================

    private void chargerStatistiques() {
        try {
            // Récupération des données
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();
            int totalAdmins = adminDAO.getTotalAdmins();
            int totalVotants = voteDAO.getTotalVotants();
            int totalNonVotants = voteDAO.getNonVotants();
            
            // Mise à jour de la vue
            view.updateStats(
                totalEtudiants,
                totalEnseignants,
                totalAdmins,
                totalVotants,
                totalNonVotants
            );
            
            System.out.println("✅ Statistiques chargées avec succès !");
            System.out.println("   - Étudiants: " + totalEtudiants);
            System.out.println("   - Enseignants: " + totalEnseignants);
            System.out.println("   - Admins: " + totalAdmins);
            System.out.println("   - Votants: " + totalVotants);
            System.out.println("   - Non-votants: " + totalNonVotants);
            
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
     * Rafraîchit les statistiques (appelable depuis l'extérieur)
     */
    public void rafraichirStatistiques() {
        chargerStatistiques();
    }

    // ==========================================
    // NAVIGATION - GESTION DES UTILISATEURS
    // ==========================================

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
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des électeurs : " + e.getMessage());
        }
    }

    private void ouvrirGestionEnseignant() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminEnseignantController adminEnseignantController = new AdminEnseignantController();
            Scene scene = new Scene(adminEnseignantController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Enseignants");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des enseignants : " + e.getMessage());
        }
    }

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
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des administrateurs : " + e.getMessage());
        }
    }

    // ==========================================
    // NAVIGATION - GESTION DES CANDIDATS
    // ==========================================

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
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Candidats : " + e.getMessage());
        }
    }

    // ==========================================
    // NAVIGATION - GESTION DES ÉLECTIONS
    // ==========================================

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
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des élections : " + e.getMessage());
        }
    }

    // ==========================================
    // NAVIGATION - GESTION ACADÉMIQUE
    // ==========================================

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
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des UFRs : " + e.getMessage());
        }
    }

    private void ouvrirGestionDepartements() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDepartementController adminDepartementController = new AdminDepartementController();
            Scene scene = new Scene(adminDepartementController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Départements");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Départements : " + e.getMessage());
        }
    }

    private void ouvrirGestionFilieres() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminFiliereController adminFiliereController = new AdminFiliereController();
            Scene scene = new Scene(adminFiliereController, 1400, 700); 
            stage.setTitle("UAM e-Vote - Gestion des Filières");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace(); 
            afficherMessage("Erreur", "Impossible d'ouvrir la gestion des Filières : " + e.getMessage());
        }
    }

    // ==========================================
    // NAVIGATION - STATISTIQUES
    // ==========================================

    private void ouvrirStatistiques() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            // Créer un contrôleur de statistiques (à implémenter)
            // Pour l'instant, on utilise le contrôleur actuel
            // Mais vous devriez avoir un StatistiqueController dédié
            StatistiqueController statistiqueController = new StatistiqueController();
            // Si vous avez une vue de statistiques, utilisez-la ici
            // Sinon, on reste sur le dashboard
            afficherMessage("Information", "Module de statistiques en cours de développement.");
            
            // Exemple de code si vous avez une vue de statistiques :
            // StatisticsView statisticsView = new StatisticsView();
            // StatistiqueController statsCtrl = new StatistiqueController(statisticsView);
            // Scene scene = new Scene(statisticsView, 1400, 700);
            // stage.setTitle("UAM e-Vote - Statistiques");
            // stage.setScene(scene);
            // stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible d'ouvrir l'interface des statistiques : " + e.getMessage());
        }
    }

    // ==========================================
    // NAVIGATION - RETOUR À LA CONNEXION
    // ==========================================

    private void retourConnexion() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);
            Scene scene = new Scene(loginController, 1400, 700);
            stage.setTitle("UAM e-Vote - Connexion");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Impossible de revenir à la page de connexion : " + e.getMessage());
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
        
        // Personnalisation du style
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.getButtonTypes().forEach(button -> {
            javafx.scene.control.Button btn = (javafx.scene.control.Button) dialogPane.lookupButton(button);
            btn.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold;");
        });
        
        alert.showAndWait();
    }

    // ==========================================
    // GETTERS (optionnels)
    // ==========================================

    public AdminDashboardView getView() {
        return view;
    }

    public VoteDAO getVoteDAO() {
        return voteDAO;
    }

    public AdminDAO getAdminDAO() {
        return adminDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}