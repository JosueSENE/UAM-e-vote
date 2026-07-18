package app.controller;

import app.dao.UserDAO;
import app.dao.AdminDAO;
import app.model.User;
import app.model.Admin;
import app.view.LoginView;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class LoginController extends BorderPane{

    private LoginView view; 
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    
    private User electeurCourant = null;
    private Admin adminCourant = null;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        
        initEventHandlers();
    }

    private void initEventHandlers() {
        // Détecter la perte de focus sur l'email
        view.getTxtEmail().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { 
                ajusterFormulaireSelonEmail();
            }
        });

        // Détecter le changement de sélection dans le ComboBox
        view.getComboEspace().valueProperty().addListener((obs, oldVal, newVal) -> {
            ajusterFormulaireSelonEmail();
        });

        // Actions de validation
        view.getBtnConnexion().setOnAction(e -> handleLogin());
        view.getTxtEmail().setOnAction(e -> handleLogin());
        view.getTxtSecret().setOnAction(e -> handleLogin());
    }

    private void ajusterFormulaireSelonEmail() {
        String email = view.getTxtEmail().getText().trim();
        String fonction = view.getComboEspace().getValue().trim();

        if (email.isEmpty()) return;

        String currentPassword = null;

        if ("ADMINISTRATEUR".equalsIgnoreCase(fonction)) {
            electeurCourant = null;
            adminCourant = adminDAO.searchAdmin(email);
            if (adminCourant != null) currentPassword = adminCourant.getPassword();
        } else {
            adminCourant = null;
            electeurCourant = userDAO.searchUser(email);
            if (electeurCourant != null) currentPassword = electeurCourant.getPassword();
        }

        // Ajustement dynamique des labels et prompts de l'interface graphique
        if (adminCourant != null || electeurCourant != null) {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                view.getLblSecret().setText("Code Permanent (Première connexion)");
                view.getTxtSecret().setPromptText("Ex: 501699");
            } else {
                view.getLblSecret().setText("Mot de passe");
                view.getTxtSecret().setPromptText("Saisissez votre mot de passe");
            }
        }
    }

    private void handleLogin() {
        String email = view.getTxtEmail().getText().trim();
        String secretSaisi = view.getTxtSecret().getText().trim();
        String fonction = view.getComboEspace().getValue().trim(); 

        if (email.isEmpty() || secretSaisi.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }
        
        boolean isAdmin = "ADMINISTRATEUR".equalsIgnoreCase(fonction);

        String passActuel = null;
        int codePermanentAttendu = -1;

        if (isAdmin) {
            adminCourant = adminDAO.searchAdmin(email);
            if (adminCourant != null) {
                passActuel = adminCourant.getPassword();
                codePermanentAttendu = adminCourant.getCode_permanent(); 
            }
        } else {
            electeurCourant = userDAO.searchUser(email);
            if (electeurCourant != null) {
                passActuel = electeurCourant.getPassword();
                codePermanentAttendu = electeurCourant.getCode_permanent();
            }
        }

        if ((isAdmin && adminCourant == null) || (!isAdmin && electeurCourant == null)) {
            afficherAlerte(Alert.AlertType.ERROR, "Compte introuvable", 
                "Aucun compte " + fonction.toLowerCase() + " n'est enregistré avec cette adresse email.");
            return;
        }
        
        // ================= CAS 1 : TOUTE PREMIÈRE AUTHENTIFICATION =================
        if (passActuel == null || passActuel.trim().isEmpty()) {
            try {
                int codePermanentSaisi = Integer.parseInt(secretSaisi);
                
                if (codePermanentAttendu == codePermanentSaisi) {
                    String nouveauMdp = afficherDialogCreationPassword();
                    
                    // Si l'utilisateur valide un mot de passe conforme
                    if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                        boolean succes = isAdmin 
                            ? adminDAO.updatePassword(adminCourant.getId(), nouveauMdp)
                            : userDAO.updatePassword(electeurCourant.getId(), nouveauMdp);

                        if (succes) {
                            afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", 
                                "Votre mot de passe a été enregistré avec succès.\nVous pouvez maintenant vous connecter.");
                            view.getTxtSecret().clear(); 
                            ajusterFormulaireSelonEmail(); 
                        } else {
                            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer le mot de passe dans la base de données.");
                        }
                    }
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", "Le code permanent saisi est incorrect.");
                }
            } catch (NumberFormatException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Veuillez saisir votre Code Permanent (chiffres uniquement).");
            }
            return; 
        }

        // ================= CAS 2 : AUTHENTIFICATION CLASSIQUE AVEC MOT DE PASSE =================
        if (isAdmin) {
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
    }

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

        container.getChildren().addAll(new Label("Choisissez un mot de passe :"), pf1, new Label("Confirmez le mot de passe :"), pf2);
        dialog.getDialogPane().setContent(container);

        // Bloquer la fermeture si la validation échoue lors du clic sur "Créer mon compte"
        final Button btValider = (Button) dialog.getDialogPane().lookupButton(boutonValider);
        btValider.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String mdp1 = pf1.getText().trim();
            String mdp2 = pf2.getText().trim();

            if (mdp1.isEmpty() || mdp2.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir les deux champs de mot de passe.");
                event.consume(); // Interrompt la fermeture de la boîte de dialogue
            } else if (!mdp1.equals(mdp2)) {
                afficherAlerte(Alert.AlertType.ERROR, "Mots de passe différents", "Les deux mots de passe saisis ne correspondent pas.");
                event.consume(); // Interrompt également la fermeture
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

    private void ouvrirInterfaceAdmin() {
        try {
            Stage stage = (Stage) view.getScene().getWindow(); 
            AdminDashboardController adminDashboardController = new AdminDashboardController();
            Scene adminScene = new Scene(adminDashboardController, 1150, 720);       
            stage.setTitle("UAM e-Vote - Espace Administration");
            stage.setScene(adminScene);
            stage.centerOnScreen();
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'espace administration : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ouvrirInterfaceElecteur(User user) {
        // Redirection vers l'espace de vote de l'étudiant/enseignant (Prochaine étape)
        System.out.println("Ouverture de l'interface de vote pour l'électeur : " + user.getPrenom() + " " + user.getNom());
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}