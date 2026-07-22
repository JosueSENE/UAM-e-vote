package app.controller;

import app.dao.AdminDAO;
import app.dao.UserDAO;
import app.model.Admin;
import app.view.AdminManagementView;
import app.utils.PasswordHasher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AdminManagementController extends BorderPane {

    private final AdminManagementView view;
    private final AdminDAO adminDAO;
    private final UserDAO userDAO;

    // Éléments de la Table
    private TableView<Admin> tableAdmins;

    // Éléments du Formulaire
    private TextField txtSearch;
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private TextField txtLogin;
    private TextField txtPassword;
    private ComboBox<String> comboUfr;
    private CheckBox chkActif;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;
    private Button btnRefresh;

    // Données
    private ObservableList<Admin> adminList;

    public AdminManagementController() {
        this.adminDAO = new AdminDAO();
        this.userDAO = new UserDAO();
        this.adminList = FXCollections.observableArrayList();
        this.view = new AdminManagementView();
        this.setCenter(this.view);

        // Liaison des composants
        lierComposants();

        // Configuration des événements
        configurerEvenements();

        // Chargement des données
        loadAdminsData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    // ==========================================
    // INITIALISATION
    // ==========================================

    private void lierComposants() {
        this.tableAdmins = this.view.getTableAdmins();
        this.txtSearch = this.view.getTxtSearch();
        this.txtCodePermanent = this.view.getTxtCodePermanent();
        this.txtNom = this.view.getTxtNom();
        this.txtPrenom = this.view.getTxtPrenom();
        this.txtEmail = this.view.getTxtEmail();
        this.txtLogin = this.view.getTxtLogin();
        this.txtPassword = this.view.getTxtPassword();
        this.comboUfr = this.view.getComboUfr();
        this.chkActif = this.view.getChkActif();
        this.btnAjouter = this.view.getBtnAjouter();
        this.btnModifier = this.view.getBtnModifier();
        this.btnSupprimer = this.view.getBtnSupprimer();
        this.btnVider = this.view.getBtnVider();
        this.btnRetour = this.view.getBtnRetour();
        this.btnRefresh = this.view.getBtnRefresh();
    }

    private void configurerEvenements() {
        this.btnAjouter.setOnAction(e -> gererAjout());
        this.btnModifier.setOnAction(e -> gererModification());
        this.btnSupprimer.setOnAction(e -> gererSuppression());
        this.btnVider.setOnAction(e -> clearForm());
        this.btnRetour.setOnAction(e -> retournerDashboard());
        this.btnRefresh.setOnAction(e -> loadAdminsData());

        view.setCodePermanentEditable(true);
    }

    // ==========================================
    // CHARGEMENT DES DONNÉES
    // ==========================================

    private void loadAdminsData() {
        try {
            List<Admin> list = adminDAO.getAllAdmins();
            adminList.setAll(list);
            tableAdmins.setItems(adminList);
            System.out.println("✅ " + list.size() + " administrateur(s) chargé(s)");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger les administrateurs : " + e.getMessage());
        }
    }

    // ==========================================
    // SÉLECTION DANS LA TABLE
    // ==========================================

    private void setupSelectionListener() {
        tableAdmins.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirFormulaire(newSelection);
            }
        });
    }

    private void remplirFormulaire(Admin admin) {
        txtCodePermanent.setText(String.valueOf(admin.getCodePermanent()));
        view.setCodePermanentEditable(false);
        txtNom.setText(admin.getNom());
        txtPrenom.setText(admin.getPrenom());
        txtEmail.setText(admin.getEmail());
        txtLogin.setText(admin.getLogin());
        txtPassword.clear();
        
        if (admin.getUfrNom() != null) {
            comboUfr.setValue(admin.getUfrNom());
        } else {
            comboUfr.setValue("POLYTECHNIQUE");
        }
        
        chkActif.setSelected(admin.getPassword() != null && !admin.getPassword().isEmpty());
    }

    // ==========================================
    // RECHERCHE EN TEMPS RÉEL
    // ==========================================

    private void setupRealtimeSearch() {
        FilteredList<Admin> filteredData = new FilteredList<>(adminList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(admin -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (admin.getNom() != null && admin.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (admin.getPrenom() != null && admin.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(admin.getCodePermanent()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (admin.getEmail() != null && admin.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (admin.getLogin() != null && admin.getLogin().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (admin.getUfrNom() != null && admin.getUfrNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Admin> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableAdmins.comparatorProperty());
        tableAdmins.setItems(sortedData);
    }

    // ==========================================
    // AJOUT D'UN ADMINISTRATEUR
    // ==========================================

    private void gererAjout() {
        if (!validerFormulaire()) {
            return;
        }

        try {
            Admin admin = new Admin();
            admin.setCodePermanent(Integer.parseInt(txtCodePermanent.getText().trim()));
            admin.setNom(txtNom.getText().trim());
            admin.setPrenom(txtPrenom.getText().trim());
            admin.setEmail(txtEmail.getText().trim());
            admin.setLogin(txtLogin.getText().trim());
            
            // Gestion du mot de passe
            String password = txtPassword.getText().trim();
            if (password != null && !password.isEmpty()) {
                admin.setPassword(PasswordHasher.hashSHA256(password));
            } else {
                admin.setPassword(null); // Première connexion
            }
            
            // Gestion de l'UFR
            String ufrNom = comboUfr.getValue();
            if (ufrNom != null && !ufrNom.isEmpty()) {
                Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
                admin.setUfrId(ufrId);
                admin.setUfrNom(ufrNom);
            }

            if (adminDAO.addAdmin(admin)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ L'administrateur a été ajouté avec succès.");
                loadAdminsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                    "❌ Impossible d'ajouter l'administrateur. Vérifiez que le Code Permanent " +
                    "ou l'email n'est pas déjà enregistré.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", 
                "Le Code Permanent doit obligatoirement être un nombre.");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Une erreur est survenue : " + e.getMessage());
        }
    }

    // ==========================================
    // MODIFICATION D'UN ADMINISTRATEUR
    // ==========================================

    private void gererModification() {
        Admin selectedAdmin = tableAdmins.getSelectionModel().getSelectedItem();
        if (selectedAdmin == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez d'abord un administrateur dans la table.");
            return;
        }

        if (!validerFormulaire()) {
            return;
        }

        try {
            selectedAdmin.setNom(txtNom.getText().trim());
            selectedAdmin.setPrenom(txtPrenom.getText().trim());
            selectedAdmin.setEmail(txtEmail.getText().trim());
            selectedAdmin.setLogin(txtLogin.getText().trim());
            
            // Gestion du mot de passe
            String password = txtPassword.getText().trim();
            if (password != null && !password.isEmpty()) {
                selectedAdmin.setPassword(PasswordHasher.hashSHA256(password));
            }
            // Si le champ est vide, on garde l'ancien mot de passe
            
            // Gestion de l'UFR
            String ufrNom = comboUfr.getValue();
            if (ufrNom != null && !ufrNom.isEmpty()) {
                Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
                selectedAdmin.setUfrId(ufrId);
                selectedAdmin.setUfrNom(ufrNom);
            }

            if (adminDAO.updateAdmin(selectedAdmin)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ Administrateur mis à jour avec succès.");
                loadAdminsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                    "❌ La mise à jour en base de données a échoué.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Une erreur est survenue : " + e.getMessage());
        }
    }

    // ==========================================
    // SUPPRESSION D'UN ADMINISTRATEUR
    // ==========================================

    private void gererSuppression() {
        Admin selectedAdmin = tableAdmins.getSelectionModel().getSelectedItem();
        if (selectedAdmin == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez l'administrateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("⚠️ Suppression d'un administrateur");
        confirm.setContentText("Voulez-vous vraiment supprimer l'administrateur \n" + 
            selectedAdmin.getPrenom() + " " + selectedAdmin.getNom() + " ?\n\n" +
            "Cette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (adminDAO.deleteAdmin(selectedAdmin.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                        "✅ Administrateur supprimé avec succès.");
                    loadAdminsData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                        "❌ La suppression a échoué.");
                }
            }
        });
    }

    // ==========================================
    // VALIDATION DU FORMULAIRE
    // ==========================================

    private boolean validerFormulaire() {
        String codePermanentStr = txtCodePermanent.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String login = txtLogin.getText().trim();

        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || 
            email.isEmpty() || login.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        if (codePermanentStr.length() != 6) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Le Code Permanent doit être composé de 6 chiffres exactement.");
            return false;
        }

        try {
            Integer.parseInt(codePermanentStr);
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Le Code Permanent doit être un nombre.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@uam\\..*$")) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "L'adresse email doit être institutionnelle (@uam.*)");
            return false;
        }

        if (login.length() < 3) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Le login doit contenir au moins 3 caractères.");
            return false;
        }

        String password = txtPassword.getText().trim();
        if (password != null && !password.isEmpty() && password.length() < 6) {
            afficherAlerte(Alert.AlertType.WARNING, "Mot de passe faible", 
                "Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }

        return true;
    }

    // ==========================================
    // RÉINITIALISATION DU FORMULAIRE
    // ==========================================

    private void clearForm() {
        tableAdmins.getSelectionModel().clearSelection();
        view.clearFormFields();
        view.setCodePermanentEditable(true);
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    private void retournerDashboard() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDashboardController adminDashboardController = new AdminDashboardController();
            javafx.scene.Scene adminScene = new javafx.scene.Scene(adminDashboardController, 1400, 700);
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(adminScene);
            stage.centerOnScreen();
            System.out.println("✅ Retour au tableau de bord Admin");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de retourner au tableau de bord : " + e.getMessage());
        }
    }

    // ==========================================
    // AFFICHAGE DES ALERTES
    // ==========================================

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.getButtonTypes().forEach(button -> {
            Button btn = (Button) dialogPane.lookupButton(button);
            btn.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold;");
        });
        
        alert.showAndWait();
    }
}