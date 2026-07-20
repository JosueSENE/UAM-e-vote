package app.controller;

import app.dao.UserDAO;
import app.model.User;
import app.view.LoginView;
import app.utils.PasswordHasher;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.sql.SQLException;

public class LoginController {

    private LoginView view;
    private UserDAO userDAO;
    private User utilisateurCourant = null;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        initEventHandlers();
    }

    public LoginView getView() {
        return this.view;
    }

    // INITIALISATION DES ÉVÉNEMENTS
    private void initEventHandlers() {
        // Perte de focus sur l'email → adapter l'interface
        view.getTxtEmail().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { 
                ajusterFormulaireSelonEmail();
            }
        });

        // Actions de connexion
        view.getBtnConnexion().setOnAction(e -> handleLogin());
        view.getTxtEmail().setOnAction(e -> handleLogin());
        view.getTxtSecret().setOnAction(e -> handleLogin());
    }

    // ==========================================
    // ADAPTATION DYNAMIQUE DU FORMULAIRE (VERSION CORRIGÉE)
    // ==========================================
    private void ajusterFormulaireSelonEmail() {
        String email = view.getTxtEmail().getText().trim();
        if (email.isEmpty()) return;
        
        try {
            // 1️⃣ RECHERCHE DANS users (étudiants/enseignants)
            utilisateurCourant = userDAO.searchUserByEmail(email);
            
            // 2️⃣ SI PAS TROUVÉ, RECHERCHE DANS admin
            if (utilisateurCourant == null) {
                utilisateurCourant = userDAO.searchAdminByEmail(email);
                
                if (utilisateurCourant != null) {
                    // ✅ C'est un administrateur
                    view.getLblSecret().setText("Mot de passe (Admin)");
                    view.getTxtSecret().setPromptText("Saisissez votre mot de passe admin");
                    return;
                }
            }
            
            // 3️⃣ UTILISATEUR TROUVÉ DANS users
            if (utilisateurCourant != null) {
                if (utilisateurCourant.getPassword() == null || 
                    utilisateurCourant.getPassword().trim().isEmpty()) {
                    // Première connexion
                    view.getLblSecret().setText("Code Permanent (Première connexion)");
                    view.getTxtSecret().setPromptText("Ex: 50123456");
                } else {
                    // Connexion classique
                    view.getLblSecret().setText("Mot de passe");
                    view.getTxtSecret().setPromptText("Saisissez votre mot de passe");
                }
            } else {
                // 4️⃣ EMAIL NON RECONNU
                view.getLblSecret().setText("Secret");
                view.getTxtSecret().setPromptText("Email non reconnu");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur Réseau", 
                "Impossible de vérifier l'état de l'adresse email.");
        }
    }

    // ==========================================
    // GESTION DE LA CONNEXION (VERSION CORRIGÉE)
    // ==========================================
    private void handleLogin() {
        String email = view.getTxtEmail().getText().trim();
        String secretSaisi = view.getTxtSecret().getText().trim();

        if (email.isEmpty() || secretSaisi.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", 
                "Veuillez remplir tous les champs.");
            return;
        }

        try {
            // 1️⃣ RECHERCHE DANS users
            utilisateurCourant = userDAO.searchUserByEmail(email);
            
            // 2️⃣ SI PAS TROUVÉ, RECHERCHE DANS admin
            if (utilisateurCourant == null) {
                utilisateurCourant = userDAO.searchAdminByEmail(email);
                
                if (utilisateurCourant != null) {
                    // ✅ AUTHENTIFICATION ADMIN
                    String hashSaisi = PasswordHasher.hashSHA256(secretSaisi);
                    if (hashSaisi.equals(utilisateurCourant.getPassword())) {
                        ouvrirInterfaceAdmin();
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", 
                            "Mot de passe admin incorrect.");
                    }
                    return;
                }
            }
            
            // 3️⃣ UTILISATEUR NON TROUVÉ
            if (utilisateurCourant == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Compte inconnu", 
                    "Cette adresse email n'existe pas.\n" +
                    "Vérifiez que vous avez saisi le bon email.");
                return;
            }

            // 4️⃣ TRAITEMENT DES UTILISATEURS (étudiants/enseignants)            
            // CAS 1 : PREMIÈRE CONNEXION
            if (utilisateurCourant.getPassword() == null || 
                utilisateurCourant.getPassword().trim().isEmpty()) {
                
                try {
                    long codePermanentSaisi = Long.parseLong(secretSaisi); // ✅ long au lieu de int
                    
                    if (utilisateurCourant.getCode_permanent() == codePermanentSaisi) {
                        String nouveauMdp = afficherDialogCreationPassword();
                        
                        if (nouveauMdp != null && !nouveauMdp.trim().isEmpty()) {
                            boolean succes = userDAO.updateUserPassword(
                                utilisateurCourant.getId(), 
                                nouveauMdp
                            );
                            if (succes) {
                                afficherAlerte(Alert.AlertType.INFORMATION, 
                                    "Compte configuré !", 
                                    "Votre mot de passe a été enregistré avec succès.\n" +
                                    "Veuillez maintenant vous connecter avec votre nouveau mot de passe.");
                                view.getTxtSecret().clear(); 
                                ajusterFormulaireSelonEmail(); 
                            } else {
                                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                                    "Impossible d'enregistrer le mot de passe.");
                            }
                        }
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Authentification échouée", 
                            "Le code permanent saisi est incorrect.");
                    }
                } catch (NumberFormatException e) {
                    afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                        "Pour votre première connexion, saisissez votre Code Permanent (chiffres uniquement).");
                }
                return; 
            }

            // CAS 2 : CONNEXION CLASSIQUE
            User userValide = userDAO.authentifierUser(email, secretSaisi).orElse(null);
            if (userValide != null) {
                if ("ADMIN".equalsIgnoreCase(userValide.getProfession())) {
                    ouvrirInterfaceAdmin();
                } else {
                    ouvrirInterfaceElecteur(userValide);
                }
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Échec de connexion", 
                    "Mot de passe incorrect.");
            }
            
        } catch (SQLException e) {
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
        dialog.setHeaderText("Identité confirmée !\nVeuillez choisir votre mot de passe.");

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

        final Button btnValiderNode = (Button) dialog.getDialogPane().lookupButton(boutonValider);
        btnValiderNode.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String mdp1 = pf1.getText().trim();
            String mdp2 = pf2.getText().trim();
            
            if (mdp1.isEmpty() || mdp2.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Validation", 
                    "Les champs ne peuvent pas être vides.");
                event.consume();
            } else if (!mdp1.equals(mdp2)) {
                afficherAlerte(Alert.AlertType.WARNING, "Validation", 
                    "Les deux mots de passe ne correspondent pas.");
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

    // REDIRECTIONS
    private void ouvrirInterfaceAdmin() {
        try {
            // 1️⃣ Récupérer la fenêtre (Stage) actuelle
            Stage stage = (Stage) view.getScene().getWindow();
            
            // 2️⃣ Créer le contrôleur du Dashboard
            AdminDashboardController dashboardController = new AdminDashboardController();
            
            // 3️⃣ Créer la scène avec le Dashboard
            Scene scene = new Scene(dashboardController, 1400, 700);
            
            // 4️⃣ Configurer la fenêtre
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(scene);
            stage.setMaximized(true);  // Optionnel : plein écran
            stage.centerOnScreen();
            
            System.out.println("✅ Redirection vers le Dashboard Admin");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture du dashboard : " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible d'ouvrir le tableau de bord administrateur.");
        }
    }

    private void ouvrirInterfaceElecteur(User user) {
        System.out.println("✅ Électeur connecté : " + user.getEmail());
        // TODO: Ouvrir l'interface électeur
        afficherAlerte(Alert.AlertType.INFORMATION, "Électeur", 
            "Bienvenue " + user.getPrenom() + " " + user.getNom() + " !");
    }
    // UTILITAIRE
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}