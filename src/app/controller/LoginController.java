package app.controller;

import app.dao.UserDAO;
import app.model.User;
import app.view.LoginView;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class LoginController {

    private LoginView view; // Référence vers la Vue
    private UserDAO userDAO;
    private User utilisateurCourant = null;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        
        // Liaison des écouteurs d'événements sur la vue
        initEventHandlers();
    }

    private void initEventHandlers() {
        // Détecter la perte de focus sur l'email pour adapter l'interface
        view.getTxtEmail().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { 
                ajusterFormulaireSelonEmail();
            }
        });

        // Actions au clic sur le bouton ou appui sur Entrée
        view.getBtnConnexion().setOnAction(e -> handleLogin());
        view.getTxtEmail().setOnAction(e -> handleLogin());
        view.getTxtSecret().setOnAction(e -> handleLogin());
    }

    private void ajusterFormulaireSelonEmail() {
        String email = view.getTxtEmail().getText().trim();
        if (email.isEmpty()) return;

        utilisateurCourant = userDAO.searchUser(email);

        if (utilisateurCourant != null) {
            if (utilisateurCourant.getPassword() == null || utilisateurCourant.getPassword().trim().isEmpty()) {
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

        if (email.isEmpty() || secretSaisi.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }
        // On recharge l'utilisateur depuis la base pour être sûr de son état actuel
        utilisateurCourant = userDAO.searchUser(email);
        if (utilisateurCourant == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Compte inconnu", "Cette adresse email n'existe pas.");
            return;
        }
        // ================= CAS 1 : TOUTE PREMIÈRE AUTHENTIFICATION =================
        if (utilisateurCourant.getPassword() == null || utilisateurCourant.getPassword().trim().isEmpty()) {
            try {
                int codePermanentSaisi = Integer.parseInt(secretSaisi);
                
                if (utilisateurCourant.getCode_permanent() == codePermanentSaisi) {
                    // Étape A : Le code permanent est bon -> on ouvre la boîte de dialogue
                    String nouveauMdp = afficherDialogCreationPassword();
                    
                    if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                        // Étape B : Enregistrement en BDD (Haché en SHA-256 !)
                        boolean succes = userDAO.updatePassword(utilisateurCourant.getId(), nouveauMdp);
                        if (succes) {
                            afficherAlerte(Alert.AlertType.INFORMATION, "Compte configuré !", 
                                "Votre mot de passe a été enregistré avec succès.\n" +
                                "Veuillez maintenant vous connecter avec votre nouveau mot de passe.");
                            // Étape C : On réinitialise l'interface immédiatement
                            view.getTxtSecret().clear(); 
                            // On force la mise à jour de l'affichage (le label va repasser à "Mot de passe")
                            ajusterFormulaireSelonEmail(); 
                        } else {
                            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer le mot de passe.");
                        }
                    }
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", "Le code permanent saisi est incorrect.");
                }
            } catch (NumberFormatException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Pour votre première connexion, saisissez votre Code Permanent (chiffres uniquement).");
            }
            return; // On arrête là pour laisser l'utilisateur taper son nouveau mot de passe
        }

        // ================= CAS 2 : AUTHENTIFICATION CLASSIQUE AVEC MOT DE PASSE =================
        // On appelle ta méthode de validation
        User userValide = userDAO.authentificate(email, secretSaisi);
        if (userValide != null) {
            if ("administrateur".equalsIgnoreCase(userValide.getProfession())) {
                ouvrirInterfaceAdmin();
            } else {
                ouvrirInterfaceElecteur(userValide);
            }
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", "Mot de passe incorrect.");
        }
    }

    private String afficherDialogCreationPassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Création du Mot de passe");
        dialog.setHeaderText("Identité confirmée !\nVeuillez choisir votre mot de passe.");

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
        // Redirection administration...
    }

    private void ouvrirInterfaceElecteur(User user) {
        // Redirection électeur...
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}