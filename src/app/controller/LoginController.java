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
    private Connectable utilisateurCourant = null;

    // Constantes pour les messages
    private static final String MSG_EMAIL_VIDE = "Veuillez saisir votre adresse email.";
    private static final String MSG_SECRET_VIDE = "Veuillez saisir votre secret.";
    private static final String MSG_EMAIL_INVALIDE = "Veuillez utiliser votre adresse email institutionnelle (@uam.*)";
    private static final String MSG_COMPTE_INTROUVABLE = "Aucun compte n'est enregistré avec cette adresse email.\nVeuillez contacter l'administrateur.";
    private static final String MSG_MDP_INCORRECT = "Mot de passe incorrect.";
    private static final String MSG_CODE_INCORRECT = "Le code permanent saisi est incorrect.";
    private static final String MSG_ERREUR_SYSTEME = "Une erreur est survenue lors de la communication avec la base de données.\nVeuillez réessayer ultérieurement.";
    private static final String MSG_MDP_ENREGISTRE = "✅ Votre mot de passe a été enregistré avec succès.\nVous pouvez maintenant vous connecter avec votre nouveau mot de passe.";
    private static final String MSG_ERREUR_ENREGISTREMENT = "❌ Impossible d'enregistrer le mot de passe dans la base de données.";

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
        
        // Action pour le lien "Mot de passe oublié"
        view.getLienMotDePasseOublie().setOnAction(e -> handleMotDePasseOublie());
    }

    // ==========================================
    // DÉTECTION AUTOMATIQUE DU TYPE DE COMPTE
    // ==========================================
    
    private void ajusterFormulaireSelonEmail() {
        String email = view.getTxtEmail().getText().trim();
        if (email.isEmpty()) {
            view.clearStatusMessage();
            return;
        }
    
        try {
            // 1️⃣ Vérifier le format de l'email
            if (!email.matches("^[A-Za-z0-9+_.-]+@uam\\..*$")) {
                view.setStatusMessage("⚠️ Email invalide. Utilisez @uam.*", true);
                view.updateSecretLabel("🔑 Secret", "Saisissez votre secret");
                utilisateurCourant = null;
                return;
            }
            
            // 2️⃣ Recherche dans la table users (étudiants et enseignants)
            User user = userDAO.searchUser(email);
            
            if (user != null) {
                utilisateurCourant = user;
                String currentPassword = user.getPassword();
                
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    // Première connexion
                    view.updateSecretLabel("🔑 Code Permanent", "Ex: 501699");
                    view.setStatusMessage("ℹ️ Première connexion - utilisez votre Code Permanent", false);
                } else {
                    // Connexion classique
                    view.updateSecretLabel("🔑 Mot de passe", "Saisissez votre mot de passe");
                    view.clearStatusMessage();
                }
                return;
            }
            
            // 3️⃣ Vérifier si c'est un admin
            Admin admin = adminDAO.searchAdmin(email);
            if (admin != null) {
                utilisateurCourant = admin;
                String currentPassword = admin.getPassword();
                
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    view.updateSecretLabel("🔑 Code Permanent", "Ex: 501699");
                    view.setStatusMessage("ℹ️ Première connexion - utilisez votre Code Permanent", false);
                } else {
                    view.updateSecretLabel("🔑 Mot de passe", "Saisissez votre mot de passe");
                    view.clearStatusMessage();
                }
                return;
            }
            
            // 4️⃣ EMAIL NON RECONNU
            utilisateurCourant = null;
            view.updateSecretLabel("🔑 Secret", "Saisissez votre secret");
            view.setStatusMessage("⚠️ Adresse email non reconnue", true);
            
        } catch (Exception e) {
            e.printStackTrace();
            view.setStatusMessage("❌ Erreur réseau", true);
            view.updateSecretLabel("🔑 Secret", "Saisissez votre secret");
        } 
    }

    // ==========================================
    // GESTION DE LA CONNEXION 
    // ==========================================

    private void handleLogin() {
        String email = view.getTxtEmail().getText().trim();
        String secretSaisi = view.getTxtSecret().getText().trim();

        // Validation des champs
        if (email.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champ vide", MSG_EMAIL_VIDE);
            view.getTxtEmail().requestFocus();
            return;
        }

        if (secretSaisi.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champ vide", MSG_SECRET_VIDE);
            view.getTxtSecret().requestFocus();
            return;
        }

        // Vérification du format de l'email institutionnel
        if (!email.matches("^[A-Za-z0-9+_.-]+@uam\\..*$")) {
            afficherAlerte(Alert.AlertType.WARNING, "Email invalide", MSG_EMAIL_INVALIDE);
            view.getTxtEmail().requestFocus();
            return;
        }

        try {
            // 1️⃣ Recherche de l'utilisateur dans la base de données
            User user = userDAO.searchUser(email);
            Admin admin = adminDAO.searchAdmin(email);
            
            // 2️⃣ Cas : Utilisateur non trouvé
            if (user == null && admin == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Compte introuvable", MSG_COMPTE_INTROUVABLE);
                return;
            }
            
            // 3️⃣ Cas : C'est un administrateur
            if (admin != null) {
                if (traiterConnexionAdmin(admin, secretSaisi)) {
                    return;
                }
            }
            
            // 4️⃣ Cas : C'est un étudiant ou enseignant
            if (user != null) {
                traiterConnexionUser(user, secretSaisi);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur système", MSG_ERREUR_SYSTEME);
        }
    }

    // ==========================================
    // TRAITEMENT DES CONNEXIONS
    // ==========================================

    /**
     * Traite la connexion d'un administrateur
     */
    private boolean traiterConnexionAdmin(Admin admin, String secretSaisi) {
        utilisateurCourant = admin;
        String passActuel = admin.getPassword();
        
        // CAS A : Première connexion admin
        if (passActuel == null || passActuel.trim().isEmpty()) {
            return traiterPremiereConnexionAdmin(admin, secretSaisi);
        }
        
        // CAS B : Connexion classique admin
        Admin adminValide = adminDAO.authentificate(admin.getEmail(), secretSaisi);
        if (adminValide != null) {
            ouvrirInterfaceAdmin();
            return true;
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", MSG_MDP_INCORRECT);
            return false;
        }
    }

    /**
     * Traite la connexion d'un étudiant ou enseignant
     */
    private boolean traiterConnexionUser(User user, String secretSaisi) {
        utilisateurCourant = user;
        String passActuel = user.getPassword();
        
        // CAS A : Première connexion user
        if (passActuel == null || passActuel.trim().isEmpty()) {
            return traiterPremiereConnexionUser(user, secretSaisi);
        }
        
        // CAS B : Connexion classique user
        User userValide = userDAO.authentificate(user.getEmail(), secretSaisi);
        if (userValide != null) {
            ouvrirInterfaceElecteur(userValide);
            return true;
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", MSG_MDP_INCORRECT);
            return false;
        }
    }

    // ==========================================
    // PREMIÈRE CONNEXION
    // ==========================================

    /**
     * Traite la première connexion d'un administrateur
     */
    private boolean traiterPremiereConnexionAdmin(Admin admin, String secretSaisi) {
        try {
            int codePermanentSaisi = Integer.parseInt(secretSaisi);
            int codePermanentAttendu = admin.getCodePermanent();
            
            if (codePermanentAttendu == codePermanentSaisi) {
                String nouveauMdp = afficherDialogCreationPassword("Administrateur");
                if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                    boolean succes = adminDAO.updatePassword(admin.getId(), nouveauMdp);
                    if (succes) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", MSG_MDP_ENREGISTRE);
                        view.getTxtSecret().clear(); 
                        ajusterFormulaireSelonEmail();
                        return true;
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Erreur", MSG_ERREUR_ENREGISTREMENT);
                    }
                }
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", MSG_CODE_INCORRECT);
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Veuillez saisir votre Code Permanent (chiffres uniquement).");
        }
        return false;
    }

    /**
     * Traite la première connexion d'un étudiant ou enseignant
     */
    private boolean traiterPremiereConnexionUser(User user, String secretSaisi) {
        try {
            int codePermanentSaisi = Integer.parseInt(secretSaisi);
            int codePermanentAttendu = user.getCodePermanent();
            
            if (codePermanentAttendu == codePermanentSaisi) {
                String nouveauMdp = afficherDialogCreationPassword("Électeur");
                if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                    boolean succes = userDAO.updatePassword(user.getId(), nouveauMdp);
                    if (succes) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", MSG_MDP_ENREGISTRE);
                        view.getTxtSecret().clear(); 
                        ajusterFormulaireSelonEmail();
                        return true;
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Erreur", MSG_ERREUR_ENREGISTREMENT);
                    }
                }
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", MSG_CODE_INCORRECT);
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Veuillez saisir votre Code Permanent (chiffres uniquement).");
        }
        return false;
    }

    // ==========================================
    // DIALOGUE DE CRÉATION DE MOT DE PASSE
    // ==========================================    

    private String afficherDialogCreationPassword(String typeUtilisateur) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔐 Création du Mot de passe");
        dialog.setHeaderText("Identité confirmée !\nVeuillez configurer votre mot de passe d'accès (" + typeUtilisateur + ").");

        ButtonType boutonValider = new ButtonType("Créer mon compte", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonValider, ButtonType.CANCEL);

        VBox container = new VBox(12);
        container.setPadding(new Insets(20));
        
        PasswordField pf1 = new PasswordField();
        pf1.setPromptText("Nouveau mot de passe (min 6 caractères)");
        pf1.setPrefHeight(40);
        
        PasswordField pf2 = new PasswordField();
        pf2.setPromptText("Confirmez le mot de passe");
        pf2.setPrefHeight(40);

        // Indicateur de force du mot de passe
        Label lblForce = new Label("Force: -");
        lblForce.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");
        
        // Mise à jour dynamique de la force
        pf1.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                PasswordHasher.PasswordStrength strength = PasswordHasher.checkPasswordStrength(newVal);
                String color;
                String emoji;
                switch (strength.getLevel()) {
                    case "Fort": 
                        color = "#4CAF50"; 
                        emoji = "🟢";
                        break;
                    case "Moyen": 
                        color = "#FF9800"; 
                        emoji = "🟠";
                        break;
                    default: 
                        color = "#F44336";
                        emoji = "🔴";
                }
                lblForce.setText(emoji + " Force: " + strength.getLevel() + " (" + strength.getScore() + "/10)");
                lblForce.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-padding: 5 0 0 0;");
            } else {
                lblForce.setText("Force: -");
                lblForce.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");
            }
        });

        // Label pour les critères
        Label lblCritere = new Label("📋 Critères: 6 caractères min, 1 majuscule, 1 minuscule, 1 chiffre");
        lblCritere.setStyle("-fx-font-size: 11px; -fx-text-fill: #999; -fx-padding: 5 0 0 0;");

        container.getChildren().addAll(
            new Label("Choisissez un mot de passe sécurisé :"), 
            pf1, 
            lblForce,
            new Label("Confirmez le mot de passe :"), 
            pf2,
            lblCritere
        );
        
        dialog.getDialogPane().setContent(container);

        // Personnalisation des boutons
        final Button btValider = (Button) dialog.getDialogPane().lookupButton(boutonValider);
        btValider.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold;");
        
        final Button btAnnuler = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btAnnuler.setStyle("-fx-text-fill: #666;");

        btValider.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String mdp1 = pf1.getText().trim();
            String mdp2 = pf2.getText().trim();

            if (mdp1.isEmpty() || mdp2.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs vides", 
                    "Veuillez remplir les deux champs de mot de passe.");
                event.consume();
            } else if (mdp1.length() < 6) {
                afficherAlerte(Alert.AlertType.WARNING, "Mot de passe trop court", 
                    "Le mot de passe doit contenir au moins 6 caractères.");
                event.consume();
            } else if (!PasswordHasher.isValidPassword(mdp1)) {
                PasswordHasher.PasswordStrength strength = PasswordHasher.checkPasswordStrength(mdp1);
                afficherAlerte(Alert.AlertType.WARNING, "Mot de passe trop faible", 
                    "Le mot de passe ne respecte pas les critères de sécurité.\n\n" +
                    strength.getFeedback() +
                    "\nVeuillez choisir un mot de passe plus sécurisé.");
                event.consume();
            } else if (!mdp1.equals(mdp2)) {
                afficherAlerte(Alert.AlertType.ERROR, "Mots de passe différents", 
                    "Les deux mots de passe saisis ne correspondent pas.");
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
    // GESTION MOT DE PASSE OUBLIÉ
    // ==========================================

    private void handleMotDePasseOublie() {
        String email = view.getTxtEmail().getText().trim();
        
        if (email.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Email requis", 
                "Veuillez d'abord saisir votre adresse email.");
            view.getTxtEmail().requestFocus();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réinitialisation du mot de passe");
        alert.setHeaderText("🔐 Mot de passe oublié");
        alert.setContentText("Un email de réinitialisation va être envoyé à :\n\n" + email + 
                            "\n\nSi vous ne recevez pas d'email dans les 5 minutes, veuillez contacter l'administrateur.");
        alert.showAndWait();
    }

    // ==========================================
    // REDIRECTIONS
    // ==========================================

    private void ouvrirInterfaceAdmin() {
        try {
            Stage stage = (Stage) view.getScene().getWindow(); 
            AdminDashboardController adminDashboardController = new AdminDashboardController();
            Scene adminScene = new Scene(adminDashboardController, 1150, 720);  
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(adminScene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            System.out.println("✅ Redirection vers le Dashboard Admin");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible d'ouvrir le tableau de bord administrateur : " + e.getMessage());
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
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger l'espace électeur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Personnalisation du style des alertes
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        dialogPane.getButtonTypes().forEach(button -> {
            Button btn = (Button) dialogPane.lookupButton(button);
            btn.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        });
        
        alert.showAndWait();
    }

    // ==========================================
    // GETTERS POUR LES TESTS
    // ==========================================

    public Connectable getUtilisateurCourant() {
        return utilisateurCourant;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AdminDAO getAdminDAO() {
        return adminDAO;
    }
}