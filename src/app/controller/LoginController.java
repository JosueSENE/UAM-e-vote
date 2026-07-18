package app.controller;

import app.dao.UserDAO;
import app.dao.AdminDAO;
import app.model.User;
import app.model.Admin;
import app.view.LoginView;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class LoginController {

    private LoginView view; 
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    
    // Un seul objet courant suffit puisqu'on sait exactement ce que l'on cherche
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

        // Détecter aussi le changement de sélection dans le ComboBox pour réajuster à la volée
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
        String fonction = view.getComboEspace().getValue().trim(); // .trim() pour nettoyer l'espace de " ADMINISTRATEUR"

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

        // Ajustement du label d'accompagnement
        if (electeurCourant != null || adminCourant != null) {
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

        // Rechargement de sécurité pour avoir l'état frais de la BDD
        String passActuel = null;
        int codePermanentAttendu = -1;

        if (isAdmin) {
            adminCourant = adminDAO.searchAdmin(email);
            if (adminCourant != null) {
                passActuel = adminCourant.getPassword();
                codePermanentAttendu = adminCourant.getCode_permanent(); // ou getCodePermanent() selon ton modèle Admin
            }
        } else {
            electeurCourant = userDAO.searchUser(email);
            if (electeurCourant != null) {
                passActuel = electeurCourant.getPassword();
                codePermanentAttendu = electeurCourant.getCode_permanent();
            }
        }

        // Si aucun compte trouvé dans la section demandée
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
                    
                    if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                        boolean succes = isAdmin 
                            ? adminDAO.updatePassword(adminCourant.getId(), nouveauMdp)
                            : userDAO.updatePassword(electeurCourant.getId(), nouveauMdp);

                        if (succes) {
                            afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", 
                                "Votre mot de passe a été enregistré avec succès.\nConnectez-vous maintenant.");
                            view.getTxtSecret().clear(); 
                            ajusterFormulaireSelonEmail(); 
                        } else {
                            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer le mot de passe.");
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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == boutonValider) {
                if (pf1.getText().trim().isEmpty() || !pf1.getText().equals(pf2.getText())) {
                    return null;
                }
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
            app.view.AdminView adminView = new app.view.AdminView();
            new app.controller.AdminController(adminView);
            
            Scene adminScene = new Scene(adminView, 1150, 720);
            stage.setTitle("UAM e-Vote - Espace Administration");
            stage.setScene(adminScene);
            stage.centerOnScreen();
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'espace administration : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirInterfaceElecteur(User user) {
        // Redirection vers l'espace de vote de l'étudiant/enseignant...
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}