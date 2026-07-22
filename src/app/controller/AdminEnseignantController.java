package app.controller;

import app.dao.EnseignantDAO;
import app.dao.UserDAO;
import app.model.User;
import app.view.AdminEnseignantView;
import app.utils.PasswordHasher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AdminEnseignantController extends BorderPane {

    private final AdminEnseignantView view;
    private final EnseignantDAO enseignantDAO;
    private final UserDAO userDAO;

    // Éléments de la Table
    private TableView<User> tableEnseignants;

    // Éléments du Formulaire
    private TextField txtSearch;
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private TextField txtLogin;
    private TextField txtPassword;
    private ComboBox<String> comboUfr;
    private ComboBox<String> comboDepartement;
    private ListView<String> listFilieres;
    private CheckBox chkActif;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;
    private Button btnRefresh;

    // Données
    private ObservableList<User> enseignantList;

    public AdminEnseignantController() {
        this.enseignantDAO = new EnseignantDAO();
        this.userDAO = new UserDAO();
        this.enseignantList = FXCollections.observableArrayList();
        this.view = new AdminEnseignantView();
        this.setCenter(this.view);

        lierComposants();
        configurerEvenements();
        loadEnseignantsData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    // ==========================================
    // INITIALISATION
    // ==========================================

    private void lierComposants() {
        this.tableEnseignants = this.view.getTableEnseignants();
        this.txtSearch = this.view.getTxtSearch();
        this.txtCodePermanent = this.view.getTxtCodePermanent();
        this.txtNom = this.view.getTxtNom();
        this.txtPrenom = this.view.getTxtPrenom();
        this.txtEmail = this.view.getTxtEmail();
        this.txtLogin = this.view.getTxtLogin();
        this.txtPassword = this.view.getTxtPassword();
        this.comboUfr = this.view.getComboUfr();
        this.comboDepartement = this.view.getComboDepartement();
        this.listFilieres = this.view.getListFilieres();
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
        this.btnRefresh.setOnAction(e -> loadEnseignantsData());

        view.setCodePermanentEditable(true);
    }

    // ==========================================
    // CHARGEMENT DES DONNÉES
    // ==========================================

    private void loadEnseignantsData() {
        try {
            java.util.List<User> list = enseignantDAO.getAllEnseignants();
            enseignantList.setAll(list);
            tableEnseignants.setItems(enseignantList);
            System.out.println("✅ " + list.size() + " enseignant(s) chargé(s)");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger les enseignants : " + e.getMessage());
        }
    }

    // ==========================================
    // SÉLECTION DANS LA TABLE
    // ==========================================

    private void setupSelectionListener() {
        tableEnseignants.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirFormulaire(newSelection);
            }
        });
    }

    private void remplirFormulaire(User enseignant) {
        txtCodePermanent.setText(String.valueOf(enseignant.getCodePermanent()));
        view.setCodePermanentEditable(false);
        txtNom.setText(enseignant.getNom());
        txtPrenom.setText(enseignant.getPrenom());
        txtEmail.setText(enseignant.getEmail());
        txtLogin.setText(enseignant.getLogin());
        txtPassword.clear();
        
        if (enseignant.getUfrNom() != null) {
            comboUfr.setValue(enseignant.getUfrNom());
        } else {
            comboUfr.setValue("POLYTECHNIQUE");
        }
        
        // Charger les filières de l'enseignant
        if (enseignant.getFilieresList() != null && !enseignant.getFilieresList().isEmpty()) {
            String[] filieres = enseignant.getFilieresList().split(",\\s*");
            for (String filiere : filieres) {
                for (String item : listFilieres.getItems()) {
                    if (item.equals(filiere)) {
                        listFilieres.getSelectionModel().select(item);
                        break;
                    }
                }
            }
        }
        
        chkActif.setSelected(enseignant.getPassword() != null && !enseignant.getPassword().isEmpty());
    }

    // ==========================================
    // RECHERCHE EN TEMPS RÉEL
    // ==========================================

    private void setupRealtimeSearch() {
        FilteredList<User> filteredData = new FilteredList<>(enseignantList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(enseignant -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (enseignant.getNom() != null && enseignant.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getPrenom() != null && enseignant.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(enseignant.getCodePermanent()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getEmail() != null && enseignant.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getLogin() != null && enseignant.getLogin().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getUfrNom() != null && enseignant.getUfrNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getDepartementNom() != null && enseignant.getDepartementNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (enseignant.getFilieresList() != null && enseignant.getFilieresList().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableEnseignants.comparatorProperty());
        tableEnseignants.setItems(sortedData);
    }

    // ==========================================
    // AJOUT D'UN ENSEIGNANT
    // ==========================================

    private void gererAjout() {
        if (!validerFormulaire()) {
            return;
        }

        try {
            User enseignant = new User();
            enseignant.setCodePermanent(Integer.parseInt(txtCodePermanent.getText().trim()));
            enseignant.setNom(txtNom.getText().trim());
            enseignant.setPrenom(txtPrenom.getText().trim());
            enseignant.setEmail(txtEmail.getText().trim());
            enseignant.setLogin(txtLogin.getText().trim());
            enseignant.setRole("ENSEIGNANT");
            
            // Gestion du mot de passe
            String password = txtPassword.getText().trim();
            if (password != null && !password.isEmpty()) {
                enseignant.setPassword(PasswordHasher.hashSHA256(password));
            } else {
                enseignant.setPassword(null);
            }
            
            // Gestion de l'UFR
            String ufrNom = comboUfr.getValue();
            if (ufrNom != null && !ufrNom.isEmpty()) {
                Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
                enseignant.setUfrId(ufrId);
                enseignant.setUfrNom(ufrNom);
            }

            // Gestion des filières sélectionnées
            String filieresStr = view.getSelectedFilieresAsString();
            enseignant.setFilieresList(filieresStr);

            if (enseignantDAO.addEnseignant(enseignant)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ L'enseignant a été ajouté avec succès.");
                loadEnseignantsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                    "❌ Impossible d'ajouter l'enseignant. Vérifiez que le Code Permanent " +
                    "ou l'email n'est pas déjà enregistré.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", 
                "Le Code Permanent doit être un nombre.");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Une erreur est survenue : " + e.getMessage());
        }
    }

    // ==========================================
    // MODIFICATION D'UN ENSEIGNANT
    // ==========================================

    private void gererModification() {
        User selectedEnseignant = tableEnseignants.getSelectionModel().getSelectedItem();
        if (selectedEnseignant == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez d'abord un enseignant dans la table.");
            return;
        }

        if (!validerFormulaire()) {
            return;
        }

        try {
            selectedEnseignant.setNom(txtNom.getText().trim());
            selectedEnseignant.setPrenom(txtPrenom.getText().trim());
            selectedEnseignant.setEmail(txtEmail.getText().trim());
            selectedEnseignant.setLogin(txtLogin.getText().trim());
            
            // Gestion du mot de passe
            String password = txtPassword.getText().trim();
            if (password != null && !password.isEmpty()) {
                selectedEnseignant.setPassword(PasswordHasher.hashSHA256(password));
            }
            
            // Gestion de l'UFR
            String ufrNom = comboUfr.getValue();
            if (ufrNom != null && !ufrNom.isEmpty()) {
                Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
                selectedEnseignant.setUfrId(ufrId);
                selectedEnseignant.setUfrNom(ufrNom);
            }

            // Gestion des filières
            String filieresStr = view.getSelectedFilieresAsString();
            selectedEnseignant.setFilieresList(filieresStr);

            if (enseignantDAO.updateEnseignant(selectedEnseignant)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ Enseignant mis à jour avec succès.");
                loadEnseignantsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                    "❌ La mise à jour a échoué.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Une erreur est survenue : " + e.getMessage());
        }
    }

    // ==========================================
    // SUPPRESSION D'UN ENSEIGNANT
    // ==========================================

    private void gererSuppression() {
        User selectedEnseignant = tableEnseignants.getSelectionModel().getSelectedItem();
        if (selectedEnseignant == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez l'enseignant à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("⚠️ Suppression d'un enseignant");
        confirm.setContentText("Voulez-vous vraiment supprimer l'enseignant \n" + 
            selectedEnseignant.getPrenom() + " " + selectedEnseignant.getNom() + " ?\n\n" +
            "Cette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (enseignantDAO.deleteEnseignant(selectedEnseignant.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                        "✅ Enseignant supprimé avec succès.");
                    loadEnseignantsData();
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

        // Vérifier qu'au moins une filière est sélectionnée
        if (listFilieres.getSelectionModel().getSelectedItems().isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Filières requises", 
                "Veuillez sélectionner au moins une filière.");
            return false;
        }

        return true;
    }

    // ==========================================
    // RÉINITIALISATION DU FORMULAIRE
    // ==========================================

    private void clearForm() {
        tableEnseignants.getSelectionModel().clearSelection();
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

    // AFFICHAGE DES ALERTES

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