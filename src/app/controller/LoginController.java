package app.controller;

import app.dao.UserDAO;
import app.dao.AdminDAO;
import app.model.User;
import app.model.Admin;
import app.model.Connectable;
import app.view.LoginView;
import app.utils.PasswordHasher;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController extends BorderPane {

    private LoginView view; 
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private User electeurCourant = null;
    private Admin adminCourant = null;
    private Connectable utilisateurCourant = null;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO(); 
        initEventHandlers();
    }

    public LoginView getView() {
        return this.view;
    }
    // ==========================================
    // INITIALISATION DES ÉVÉNEMENTS
    // ==========================================

    private void initEventHandlers() {
        // Détecter la perte de focus sur l'email pour ajuster dynamiquement le formulaire
        view.getTxtEmail().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { 
                ajusterFormulaireSelonEmail();
            }
        });

        // Actions de validation
        view.getBtnConnexion().setOnAction(e -> handleLogin());
        view.getTxtEmail().setOnAction(e -> handleLogin());
        view.getTxtSecret().setOnAction(e -> handleLogin());
    }

    // ==========================================
    // DÉTECTION AUTOMATIQUE DU TYPE DE COMPTE
    // ==========================================
    
    private void ajusterFormulaireSelonEmail() {
        String email = view.getTxtEmail().getText().trim();
        if (email.isEmpty()) return;
    
        try{
            adminCourant = adminDAO.searchAdmin(email);
            electeurCourant = userDAO.searchUser(email);
            String currentPassword = null;
            // 1️⃣ On cherche d'abord si c'est un administrateur
            if (adminCourant != null) {
                electeurCourant = null;
                currentPassword = adminCourant.getPassword();
                utilisateurCourant = adminCourant;
            } else if (electeurCourant != null){
                // 2️⃣ Si ce n'est pas un admin, on cherche dans les électeurs
                adminCourant = null;
                currentPassword = electeurCourant.getPassword();
                utilisateurCourant = electeurCourant;
            }else { utilisateurCourant = null; }
            // 3️⃣ UTILISATEUR TROUVÉ DANS electeurs ou admin
            if (utilisateurCourant != null) {
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    // Première connexion
                    view.getLblSecret().setText("Code Permanent (Première connexion)");
                    view.getTxtSecret().setPromptText("Ex: 501699");
                } else {
                    // Connexion classique
                    view.getLblSecret().setText("Mot de passe");
                    view.getTxtSecret().setPromptText("Saisissez votre mot de passe");
                }
            } else {
                // 4️⃣ EMAIL NON RECONNU
                view.getLblSecret().setText("Secret");
                view.getTxtSecret().setPromptText("Saisissez votre secret");
            }
        }catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur Réseau", "Impossible de vérifier l'état de l'adresse email.");
        } 
    }

    // ==========================================
    // GESTION DE LA CONNEXION 
    // ==========================================

    private void handleLogin() {
        String email = view.getTxtEmail().getText().trim();
        String secretSaisi = view.getTxtSecret().getText().trim();

        if (email.isEmpty() || secretSaisi.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            // 1️⃣ Détermination automatique du type de compte par recherche en Base de Données
            adminCourant = adminDAO.searchAdmin(email);
            electeurCourant = userDAO.searchUser(email);
            if (adminCourant != null) {
                String hashSaisi = PasswordHasher.hashSHA256(secretSaisi);
                if (hashSaisi.equals(adminCourant.getPassword())) {
                    ouvrirInterfaceAdmin();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe admin incorrect.");
                }
                return;
            } 
            else if (electeurCourant != null){
                String hashSaisi = PasswordHasher.hashSHA256(secretSaisi);
                if (hashSaisi.equals(electeurCourant.getPassword())) {
                    ouvrirInterfaceElecteur(electeurCourant);
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe électeur incorrect.");
                }
                return;
            }
            // 3️⃣ UTILISATEUR NON TROUVÉ
            if (adminCourant == null && electeurCourant == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Compte introuvable", 
                    "Aucun compte n'est enregistré avec cette adresse email.");
                return;
            }
            // ==================================================
            // 4️⃣ TRAITEMENT DES ÉLECTEURS (étudiants/enseignants)
            // ==================================================

            // CAS 1 : TOUTE PREMIÈRE AUTHENTIFICATION

            String passActuel = utilisateurCourant.getPassword();
            int codePermanentAttendu = utilisateurCourant.getCode_permanent();
            if (passActuel == null || passActuel.trim().isEmpty()) {
                try {
                    int codePermanentSaisi = Integer.parseInt(secretSaisi);
                    if (codePermanentAttendu == codePermanentSaisi) {
                        String nouveauMdp = afficherDialogCreationPassword();
                        if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                            boolean succes = (utilisateurCourant instanceof Admin)
                                ? adminDAO.updatePassword(utilisateurCourant.getId(), nouveauMdp)  // Si c'est un Admin, on enregistre dans la table 'admin'
                                : userDAO.updatePassword(utilisateurCourant.getId(), nouveauMdp);  // Sinon (c'est un étudiant/enseignant), on enregistre dans la table 'users'
                            if (succes) {
                                afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", 
                                    "Votre mot de passe a été enregistré avec succès.\nVous pouvez maintenant vous connecter avec votre nouveau mot de passe.");
                                view.getTxtSecret().clear(); 
                                ajusterFormulaireSelonEmail(); 
                            } else {
                                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer le mot de passe dans la base de données.");
                            }
                        }
                    } 
                    else {
                        afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", "Le code permanent saisi est incorrect.");
                    }
                } catch (NumberFormatException e) {
                    afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Veuillez saisir votre Code Permanent (chiffres uniquement).");
                }
                return; 
            }

            //  CAS 2 : AUTHENTIFICATION CLASSIQUE AVEC MOT DE PASSE

            if (utilisateurCourant instanceof Admin) {
                Admin adminValide = adminDAO.authentificate(email, secretSaisi);
                if (adminValide != null) {
                    ouvrirInterfaceAdmin();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe administrateur incorrect.");
                }
            } else {
                User userValide = userDAO.authentificate(email, secretSaisi);
                if (userValide != null) {
                    ouvrirInterfaceElecteur(userValide);
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe incorrect.");
                }
            }
        }catch (Exception e) {
                e.printStackTrace();
                afficherAlerte(Alert.AlertType.ERROR, "Erreur système", 
                    "Une erreur est survenue lors de la communication avec la base de données.");
        }
    }

    // ==========================================
    // DIALOGUE DE CRÉATION DE MOT DE PASSE
    // ==========================================    

    private String afficherDialogCreationPassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Création du Mot de passe");
        dialog.setHeaderText("Identité confirmée !\nVeuillez configurer votre mot de passe d'accès.");

        ButtonType boutonValider = new ButtonType("Créer mon compte", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonValider, ButtonType.CANCEL);

        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        PasswordField pf1 = new PasswordField();
        pf1.setPromptText("Nouveau mot de passe");
        PasswordField pf2 = new PasswordField();
        pf2.setPromptText("Confirmez le mot de passe");

        container.getChildren().addAll(
            new Label("Choisissez un mot de passe :"), 
            pf1, 
            new Label("Confirmez le mot de passe :"), 
            pf2
        );
        dialog.getDialogPane().setContent(container);

        final Button btValider = (Button) dialog.getDialogPane().lookupButton(boutonValider);
        btValider.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String mdp1 = pf1.getText().trim();
            String mdp2 = pf2.getText().trim();

            if (mdp1.isEmpty() || mdp2.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir les deux champs de mot de passe.");
                event.consume(); 
            } else if (!mdp1.equals(mdp2)) {
                afficherAlerte(Alert.AlertType.ERROR, "Mots de passe différents", "Les deux mots de passe saisis ne correspondent pas.");
                event.consume(); 
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == boutonValider) {
                return pf1.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // ==========================================
    // REDIRECTIONS
    // ==========================================

    private void ouvrirInterfaceAdmin() {
        try {
            // 1️⃣ Récupérer la fenêtre (Stage) actuelle
            Stage stage = (Stage) view.getScene().getWindow(); 
             // 2️⃣ Créer le contrôleur du Dashboard
            AdminDashboardController adminDashboardController = new AdminDashboardController();
            // 3️⃣ Créer la scène avec le Dashboard
            Scene adminScene = new Scene(adminDashboardController, 1150, 720);  
            // 4️⃣ Configurer la fenêtre     
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(adminScene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            System.out.println("✅ Redirection vers le Dashboard Admin");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le tableau de bord administrateur. : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ouvrirInterfaceElecteur(User user) {
        try {
            Stage stage = (Stage) view.getScene().getWindow();
            UserDashboardController userDashboardController = new UserDashboardController();
            Scene userScene = new Scene(userDashboardController, 1150, 720);
            stage.setTitle("UAM e-Vote - Espace Électeurs");
            stage.setScene(userScene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            System.out.println("✅ Redirection vers le Dashboard Électeur");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'espace électeur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}