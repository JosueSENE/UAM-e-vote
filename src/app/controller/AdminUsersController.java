package app.controller;

import app.dao.UserDAO;
import app.model.User;
import app.view.AdminUsersView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AdminUsersController extends BorderPane {

    private final AdminUsersView view;

    // Éléments de la Table
    private TableView<User> tableUsers;

    // Éléments du Formulaire
    private TextField txtSearch;
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private TextField txtLogin;
    private ComboBox<String> comboRole;
    private ComboBox<String> comboNiveau;
    private ComboBox<String> comboUfr;
    private ComboBox<String> comboFiliere;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    // Données et DAO
    private UserDAO userDAO;
    private ObservableList<User> userList;
    private ObservableList<String> ufrList;
    private ObservableList<String> filiereList;

    // Constantes
    private static final String[] ROLES = {"ETUDIANT", "ENSEIGNANT", "ADMIN"};
    private static final String[] NIVEAUX = {"L1", "L2", "L3", "M1", "M2"};
    private static final String[] UFR_LIST = {"POLYTECHNIQUE", "UFR SEG", "UFR STA", "UFR TECNA"};

    public AdminUsersController() {
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        this.ufrList = FXCollections.observableArrayList(UFR_LIST);
        this.filiereList = FXCollections.observableArrayList();
        this.view = new AdminUsersView();
        this.setCenter(this.view);

        // Liaison des composants graphiques
        lierComposants();

        // Initialisation des ComboBox
        initializeComboBoxes();

        // Configuration des événements
        configurerEvenements();

        // Chargement des données
        loadUsersData();
        setupSelectionListener();
        setupRealtimeSearch();
        setupUfrListener();
    }

    // ==========================================
    // INITIALISATION
    // ==========================================

    private void lierComposants() {
        this.tableUsers = this.view.getTableUsers();
        this.txtSearch = this.view.getTxtSearch();
        this.txtCodePermanent = this.view.getTxtCodePermanent();
        this.txtNom = this.view.getTxtNom();
        this.txtPrenom = this.view.getTxtPrenom();
        this.txtEmail = this.view.getTxtEmail();
        this.txtLogin = this.view.getTxtLogin();
        this.comboRole = this.view.getComboRole();
        this.comboNiveau = this.view.getComboNiveau();
        this.comboUfr = this.view.getComboUfr();
        this.comboFiliere = this.view.getComboFiliere();
        this.btnAjouter = this.view.getBtnAjouter();
        this.btnModifier = this.view.getBtnModifier();
        this.btnSupprimer = this.view.getBtnSupprimer();
        this.btnVider = this.view.getBtnVider();
        this.btnRetour = this.view.getBtnRetour();
    }

    private void initializeComboBoxes() {
        comboRole.setItems(FXCollections.observableArrayList(ROLES));
        comboRole.setValue("ETUDIANT");

        comboNiveau.setItems(FXCollections.observableArrayList(NIVEAUX));
        comboNiveau.setValue("L1");

        comboUfr.setItems(ufrList);
        comboUfr.setValue("POLYTECHNIQUE");

        comboFiliere.setItems(filiereList);
    }

    private void configurerEvenements() {
        this.btnAjouter.setOnAction(e -> gererAjout());
        this.btnModifier.setOnAction(e -> gererModification());
        this.btnSupprimer.setOnAction(e -> gererSuppression());
        this.btnVider.setOnAction(e -> clearForm());
        this.btnRetour.setOnAction(e -> retourAuTableauBord());
        
        // Désactiver la modification du code permanent par défaut
        view.setCodePermanentEditable(true);
    }

    // ==========================================
    // GESTION DES UFR ET FILIERES
    // ==========================================

    private void setupUfrListener() {
        comboUfr.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                chargerFilieresParUfr(newVal);
            }
        });
        chargerFilieresParUfr("POLYTECHNIQUE");
    }

    private void chargerFilieresParUfr(String ufrNom) {
        filiereList.clear();
        try {
            List<String> filieres = userDAO.getFilieresByUfrNom(ufrNom);
            filiereList.addAll(filieres);
            if (!filieres.isEmpty()) {
                comboFiliere.setValue(filieres.get(0));
            } else {
                comboFiliere.setValue(null);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des filières pour " + ufrNom);
            e.printStackTrace();
        }
    }

    // ==========================================
    // CHARGEMENT DES DONNÉES
    // ==========================================

    private void loadUsersData() {
        try {
            List<User> list = userDAO.getAllUsers();
            userList.setAll(list);
            tableUsers.setItems(userList);
            System.out.println("✅ " + list.size() + " utilisateur(s) chargé(s)");
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    // ==========================================
    // SÉLECTION DANS LA TABLE
    // ==========================================

    private void setupSelectionListener() {
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirFormulaire(newSelection);
            }
        });
    }

    private void remplirFormulaire(User user) {
        txtCodePermanent.setText(String.valueOf(user.getCodePermanent()));
        view.setCodePermanentEditable(false);
        txtNom.setText(user.getNom());
        txtPrenom.setText(user.getPrenom());
        txtEmail.setText(user.getEmail());
        txtLogin.setText(user.getLogin());
        comboRole.setValue(user.getRole());
        
        // Gestion du niveau
        if (user.getNiveau() != null && !user.getNiveau().isEmpty()) {
            comboNiveau.setValue(user.getNiveau());
            comboNiveau.setDisable(false);
        } else {
            comboNiveau.setValue(null);
            comboNiveau.setDisable(true);
        }
        
        // Gestion de l'UFR
        if (user.getUfrId() != null) {
            String ufrNom = userDAO.getUfrNomById(user.getUfrId());
            if (ufrNom != null) {
                comboUfr.setValue(ufrNom);
                chargerFilieresParUfr(ufrNom);
            }
        } else {
            comboUfr.setValue(null);
        }
        
        // Gestion de la filière
        if (user.getFiliereId() != null) {
            String filiereNom = userDAO.getFiliereNomById(user.getFiliereId());
            if (filiereNom != null) {
                comboFiliere.setValue(filiereNom);
            }
        } else {
            comboFiliere.setValue(null);
        }
        
        // Activer/désactiver les champs selon le rôle
        ajusterChampsSelonRole(user.getRole());
    }

    // ==========================================
    // RECHERCHE EN TEMPS RÉEL
    // ==========================================

    private void setupRealtimeSearch() {
        FilteredList<User> filteredData = new FilteredList<>(userList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                return rechercherUtilisateur(user, lowerCaseFilter);
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUsers.comparatorProperty());
        tableUsers.setItems(sortedData);
    }

    private boolean rechercherUtilisateur(User user, String filter) {
        if (user.getNom() != null && user.getNom().toLowerCase().contains(filter)) {
            return true;
        }
        if (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(filter)) {
            return true;
        }
        if (String.valueOf(user.getCodePermanent()).contains(filter)) {
            return true;
        }
        if (user.getEmail() != null && user.getEmail().toLowerCase().contains(filter)) {
            return true;
        }
        if (user.getLogin() != null && user.getLogin().toLowerCase().contains(filter)) {
            return true;
        }
        if (user.getRole() != null && user.getRole().toLowerCase().contains(filter)) {
            return true;
        }
        if (user.getFiliereNom() != null && user.getFiliereNom().toLowerCase().contains(filter)) {
            return true;
        }
        if (user.getUfrNom() != null && user.getUfrNom().toLowerCase().contains(filter)) {
            return true;
        }
        return false;
    }

    // ==========================================
    // AJOUT D'UN UTILISATEUR
    // ==========================================

    private void gererAjout() {
        if (!validerFormulaire()) {
            return;
        }

        try {
            User u = construireUtilisateurDepuisFormulaire();
            
            if (userDAO.addUser(u)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ L'utilisateur a été ajouté avec succès.");
                loadUsersData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                    "❌ Impossible d'ajouter l'utilisateur. Vérifiez que le Code Permanent " +
                    "ou l'email n'est pas déjà enregistré.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", 
                "Le Code Permanent doit obligatoirement être un nombre.");
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur base de données", 
                "Erreur lors de l'ajout : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Une erreur est survenue : " + e.getMessage());
        }
    }

    // ==========================================
    // MODIFICATION D'UN UTILISATEUR
    // ==========================================

    private void gererModification() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez d'abord un utilisateur dans la table.");
            return;
        }

        if (!validerFormulaire()) {
            return;
        }

        try {
            // Mettre à jour l'utilisateur sélectionné avec les valeurs du formulaire
            mettreAJourUtilisateur(selectedUser);
            
            if (userDAO.updateUser(selectedUser)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                    "✅ Utilisateur mis à jour avec succès.");
                loadUsersData();
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
    // SUPPRESSION D'UN UTILISATEUR
    // ==========================================

    private void gererSuppression() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", 
                "Sélectionnez l'utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("⚠️ Suppression d'un utilisateur");
        confirm.setContentText("Voulez-vous vraiment supprimer l'utilisateur \n" + 
            selectedUser.getPrenom() + " " + selectedUser.getNom() + " ?\n\n" +
            "Cette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (userDAO.deleteUser(selectedUser.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", 
                        "✅ Utilisateur supprimé avec succès.");
                    loadUsersData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                        "❌ La suppression a échoué.");
                }
            }
        });
    }

    // ==========================================
    // MÉTHODES UTILITAIRES DE CONSTRUCTION
    // ==========================================

    private User construireUtilisateurDepuisFormulaire() throws SQLException {
        User u = new User();
        u.setCodePermanent(Integer.parseInt(txtCodePermanent.getText().trim()));
        u.setNom(txtNom.getText().trim());
        u.setPrenom(txtPrenom.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setLogin(txtLogin.getText().trim());
        u.setRole(comboRole.getValue());
        u.setPassword(null); // Pas de mot de passe initial
        
        String role = comboRole.getValue();
        String ufrNom = comboUfr.getValue();
        
        if (ufrNom != null && !ufrNom.isEmpty()) {
            Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
            u.setUfrId(ufrId);
        }
        
        if ("ETUDIANT".equals(role)) {
            u.setNiveau(comboNiveau.getValue());
            String filiereNom = comboFiliere.getValue();
            if (filiereNom != null && !filiereNom.isEmpty()) {
                Integer filiereId = userDAO.getFiliereIdByNom(filiereNom);
                u.setFiliereId(filiereId);
            }
        } else {
            u.setNiveau(null);
            u.setFiliereId(null);
        }
        
        return u;
    }

    private void mettreAJourUtilisateur(User u) throws SQLException {
        u.setNom(txtNom.getText().trim());
        u.setPrenom(txtPrenom.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setLogin(txtLogin.getText().trim());
        u.setRole(comboRole.getValue());
        
        String role = comboRole.getValue();
        String ufrNom = comboUfr.getValue();
        
        if (ufrNom != null && !ufrNom.isEmpty()) {
            Integer ufrId = userDAO.getUfrIdByNom(ufrNom);
            u.setUfrId(ufrId);
        } else {
            u.setUfrId(null);
        }
        
        if ("ETUDIANT".equals(role)) {
            u.setNiveau(comboNiveau.getValue());
            String filiereNom = comboFiliere.getValue();
            if (filiereNom != null && !filiereNom.isEmpty()) {
                Integer filiereId = userDAO.getFiliereIdByNom(filiereNom);
                u.setFiliereId(filiereId);
            } else {
                u.setFiliereId(null);
            }
        } else {
            u.setNiveau(null);
            u.setFiliereId(null);
        }
    }

    // ==========================================
    // AJUSTEMENT DES CHAMPS SELON LE RÔLE
    // ==========================================

    private void ajusterChampsSelonRole(String role) {
        if ("ETUDIANT".equals(role)) {
            comboNiveau.setDisable(false);
            comboFiliere.setDisable(false);
            comboUfr.setDisable(true);
        } else if ("ENSEIGNANT".equals(role)) {
            comboNiveau.setDisable(true);
            comboFiliere.setDisable(true);
            comboUfr.setDisable(false);
        } else if ("ADMIN".equals(role)) {
            comboNiveau.setDisable(true);
            comboFiliere.setDisable(true);
            comboUfr.setDisable(false);
        }
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
        String role = comboRole.getValue();

        // Vérification des champs obligatoires
        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || 
            email.isEmpty() || login.isEmpty() || role == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        // Validation du Code Permanent
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

        // Validation de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@uam\\..*$")) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "L'adresse email doit être institutionnelle (@uam.*)");
            return false;
        }

        // Validation du login
        if (login.length() < 3) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Le login doit contenir au moins 3 caractères.");
            return false;
        }

        // Validation spécifique aux étudiants
        if ("ETUDIANT".equals(role)) {
            if (comboNiveau.getValue() == null || comboFiliere.getValue() == null) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                    "Pour un étudiant, le niveau et la filière sont obligatoires.");
                return false;
            }
        }

        // Validation spécifique aux enseignants et admins
        if (("ENSEIGNANT".equals(role) || "ADMIN".equals(role)) && 
            (comboUfr.getValue() == null || comboUfr.getValue().isEmpty())) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                "Pour un " + role.toLowerCase() + ", l'UFR est obligatoire.");
            return false;
        }

        return true;
    }

    // ==========================================
    // RÉINITIALISATION DU FORMULAIRE
    // ==========================================

    private void clearForm() {
        tableUsers.getSelectionModel().clearSelection();
        view.clearFormFields();
        view.setCodePermanentEditable(true);
        
        // Réinitialiser les valeurs par défaut
        comboRole.setValue("ETUDIANT");
        comboNiveau.setValue("L1");
        comboUfr.setValue("POLYTECHNIQUE");
        chargerFilieresParUfr("POLYTECHNIQUE");
        comboNiveau.setDisable(false);
        comboFiliere.setDisable(false);
        comboUfr.setDisable(true);
        
        txtCodePermanent.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtLogin.clear();
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    private void retourAuTableauBord() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDashboardController adminDashboardController = new AdminDashboardController();
            javafx.scene.Scene adminScene = new javafx.scene.Scene(adminDashboardController, 1150, 720);
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(adminScene);
            stage.setMaximized(true);
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
        
        // Personnalisation du style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.getButtonTypes().forEach(button -> {
            Button btn = (Button) dialogPane.lookupButton(button);
            btn.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold;");
        });
        
        alert.showAndWait();
    }

    // ==========================================
    // GETTERS POUR LES TESTS
    // ==========================================

    public AdminUsersView getView() {
        return view;
    }

    public ObservableList<User> getUserList() {
        return userList;
    }
}