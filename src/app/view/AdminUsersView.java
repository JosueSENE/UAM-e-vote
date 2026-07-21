package app.view;

import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminUsersView extends BorderPane {

    // ==========================================
    // TABLE
    // ==========================================
    
    private TableView<User> tableUsers;
    private TableColumn<User, Integer> colId;
    private TableColumn<User, Integer> colCodePermanent;
    private TableColumn<User, String> colNom;
    private TableColumn<User, String> colPrenom;
    private TableColumn<User, String> colEmail;
    private TableColumn<User, String> colLogin;
    private TableColumn<User, String> colRole;
    private TableColumn<User, String> colNiveau;
    private TableColumn<User, String> colFiliere;
    private TableColumn<User, String> colUfr;

    // ==========================================
    // CHAMPS DE RECHERCHE
    // ==========================================
    
    private TextField txtSearch;

    // ==========================================
    // CHAMPS DU FORMULAIRE
    // ==========================================
    
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private TextField txtLogin;
    private ComboBox<String> comboRole;
    private ComboBox<String> comboNiveau;
    private ComboBox<String> comboUfr;
    private ComboBox<String> comboFiliere;

    // ==========================================
    // BOUTONS
    // ==========================================
    
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;
    private Button btnRefresh;

    // ==========================================
    // CONSTANTES
    // ==========================================
    
    private static final String[] ROLES = {"ETUDIANT", "ENSEIGNANT", "ADMIN"};
    private static final String[] NIVEAUX = {"L1", "L2", "L3", "M1", "M2"};
    private static final String[] UFR_LIST = {"POLYTECHNIQUE", "UFR SEG", "UFR STA", "UFR TECNA"};

    public AdminUsersView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f2f5;");
        initLeftTableSection();
        initRightFormSection();
    }

    // ==========================================
    // SECTION TABLE (GAUCHE)
    // ==========================================

    private void initLeftTableSection() {
        VBox leftBox = new VBox(15);
        leftBox.setPadding(new Insets(10));
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        // ===== HEADER =====
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRetour.setOnMouseEntered(e -> btnRetour.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnRetour.setOnMouseExited(e -> btnRetour.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));

        Label lblTitle = new Label("👥 Gestion des Utilisateurs");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ===== RECHERCHE =====
        Label lblSearch = new Label("🔍 Rechercher : ");
        lblSearch.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Nom, prénom, email, login...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 15; -fx-padding: 8 15;");
        
        // Bouton Rafraîchir
        btnRefresh = new Button("🔄");
        btnRefresh.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRefresh.setTooltip(new Tooltip("Rafraîchir la liste"));
        btnRefresh.setOnMouseEntered(e -> btnRefresh.setStyle("-fx-background-color: #006699; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnRefresh.setOnMouseExited(e -> btnRefresh.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));

        HBox searchBox = new HBox(5, lblSearch, txtSearch, btnRefresh);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(btnRetour, lblTitle, spacer, searchBox);

        // ===== TABLE =====
        tableUsers = new TableView<>();
        tableUsers.setPlaceholder(new Label("📋 Aucun utilisateur trouvé"));
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableUsers.setPrefHeight(550);
        tableUsers.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

        // Colonnes
        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        colId.setStyle("-fx-alignment: CENTER;");

        colCodePermanent = new TableColumn<>("Code Perm.");
        colCodePermanent.setCellValueFactory(new PropertyValueFactory<>("codePermanent"));
        colCodePermanent.setPrefWidth(100);
        colCodePermanent.setStyle("-fx-alignment: CENTER;");

        colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);

        colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        colLogin = new TableColumn<>("Login");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colLogin.setPrefWidth(120);

        colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(100);
        colRole.setStyle("-fx-alignment: CENTER;");

        colNiveau = new TableColumn<>("Niveau");
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colNiveau.setPrefWidth(80);
        colNiveau.setStyle("-fx-alignment: CENTER;");

        colFiliere = new TableColumn<>("Filière");
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiereNom"));
        colFiliere.setPrefWidth(150);

        colUfr = new TableColumn<>("UFR");
        colUfr.setCellValueFactory(new PropertyValueFactory<>("ufrNom"));
        colUfr.setPrefWidth(120);

        tableUsers.getColumns().addAll(
            colId, colCodePermanent, colNom, colPrenom, 
            colEmail, colLogin, colRole, colNiveau, 
            colFiliere, colUfr
        );

        leftBox.getChildren().addAll(headerBox, tableUsers);
        this.setCenter(leftBox);
    }

    // ==========================================
    // SECTION FORMULAIRE (DROITE)
    // ==========================================

    private void initRightFormSection() {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(20));
        formBox.setPrefWidth(350);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        // ===== TITRE =====
        Label lblFormTitle = new Label("✏️ Informations Utilisateur");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblFormTitle.setStyle("-fx-text-fill: #005088;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");

        // ===== CHAMPS =====
        // Code Permanent
        txtCodePermanent = new TextField();
        txtCodePermanent.setPromptText("Ex: 501699");
        txtCodePermanent.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        txtCodePermanent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCodePermanent.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtCodePermanent.getText().length() > 6) {
                String s = txtCodePermanent.getText().substring(0, 6);
                txtCodePermanent.setText(s);
            }
        });
        VBox fieldCodePermanent = createFormField("📋 Code Permanent *", txtCodePermanent);

        // Nom
        txtNom = new TextField();
        txtNom.setPromptText("Ex: Ndiaye");
        txtNom.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldNom = createFormField("👤 Nom *", txtNom);

        // Prénom
        txtPrenom = new TextField();
        txtPrenom.setPromptText("Ex: Jean");
        txtPrenom.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldPrenom = createFormField("👤 Prénom *", txtPrenom);

        // Email
        txtEmail = new TextField();
        txtEmail.setPromptText("jean.ndiaye@uam.edu.sn");
        txtEmail.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldEmail = createFormField("📧 Email *", txtEmail);

        // Login
        txtLogin = new TextField();
        txtLogin.setPromptText("jean.ndiaye");
        txtLogin.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldLogin = createFormField("🔑 Login *", txtLogin);

        // Rôle
        comboRole = new ComboBox<>();
        comboRole.getItems().addAll(ROLES);
        comboRole.setPromptText("Sélectionner un rôle");
        comboRole.setMaxWidth(Double.MAX_VALUE);
        comboRole.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        comboRole.setValue("ETUDIANT");
        
        // Listener pour ajuster les champs selon le rôle
        comboRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            ajusterChampsSelonRole(newVal);
        });
        
        VBox fieldRole = createFormField("🎯 Rôle *", comboRole);

        // Niveau
        comboNiveau = new ComboBox<>();
        comboNiveau.getItems().addAll(NIVEAUX);
        comboNiveau.setPromptText("Sélectionner un niveau");
        comboNiveau.setMaxWidth(Double.MAX_VALUE);
        comboNiveau.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        comboNiveau.setValue("L1");
        VBox fieldNiveau = createFormField("📚 Niveau", comboNiveau);

        // UFR
        comboUfr = new ComboBox<>();
        comboUfr.getItems().addAll(UFR_LIST);
        comboUfr.setPromptText("Sélectionner un UFR");
        comboUfr.setMaxWidth(Double.MAX_VALUE);
        comboUfr.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        comboUfr.setValue("POLYTECHNIQUE");
        VBox fieldUfr = createFormField("🏛️ UFR", comboUfr);

        // Filière
        comboFiliere = new ComboBox<>();
        comboFiliere.setPromptText("Sélectionner une filière");
        comboFiliere.setMaxWidth(Double.MAX_VALUE);
        comboFiliere.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldFiliere = createFormField("📖 Filière", comboFiliere);

        // ===== BOUTONS =====
        GridPane gridButtons = new GridPane();
        gridButtons.setHgap(10);
        gridButtons.setVgap(10);

        btnAjouter = new Button("➕ Ajouter");
        btnAjouter.setStyle("-fx-background-color: #2ec4b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setPrefHeight(40);
        btnAjouter.setOnMouseEntered(e -> btnAjouter.setStyle("-fx-background-color: #26a69a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnAjouter.setOnMouseExited(e -> btnAjouter.setStyle("-fx-background-color: #2ec4b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));

        btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnModifier.setMaxWidth(Double.MAX_VALUE);
        btnModifier.setPrefHeight(40);
        btnModifier.setOnMouseEntered(e -> btnModifier.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnModifier.setOnMouseExited(e -> btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));

        btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnSupprimer.setMaxWidth(Double.MAX_VALUE);
        btnSupprimer.setPrefHeight(40);
        btnSupprimer.setOnMouseEntered(e -> btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnSupprimer.setOnMouseExited(e -> btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));

        btnVider = new Button("🧹 Vider");
        btnVider.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnVider.setMaxWidth(Double.MAX_VALUE);
        btnVider.setPrefHeight(40);
        btnVider.setOnMouseEntered(e -> btnVider.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnVider.setOnMouseExited(e -> btnVider.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));

        gridButtons.add(btnAjouter, 0, 0);
        gridButtons.add(btnModifier, 1, 0);
        gridButtons.add(btnSupprimer, 0, 1);
        gridButtons.add(btnVider, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridButtons.getColumnConstraints().addAll(col1, col2);

        // ===== ASSEMBLAGE =====
        formBox.getChildren().addAll(
            lblFormTitle, 
            separator,
            fieldCodePermanent,
            fieldNom,
            fieldPrenom,
            fieldEmail,
            fieldLogin,
            fieldRole,
            fieldNiveau,
            fieldUfr,
            fieldFiliere,
            new Separator(),
            gridButtons
        );

        HBox rightBox = new HBox(formBox);
        rightBox.setPadding(new Insets(10));
        this.setRight(rightBox);
        
        // Appliquer les ajustements par défaut
        ajusterChampsSelonRole("ETUDIANT");
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private VBox createFormField(String labelText, Control input) {
        VBox vbox = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lbl.setStyle("-fx-text-fill: #555;");
        vbox.getChildren().addAll(lbl, input);
        return vbox;
    }

    private void ajusterChampsSelonRole(String role) {
        if (role == null) return;
        
        switch (role) {
            case "ETUDIANT":
                comboNiveau.setDisable(false);
                comboFiliere.setDisable(false);
                comboUfr.setDisable(true);
                comboNiveau.setStyle("-fx-opacity: 1;");
                comboFiliere.setStyle("-fx-opacity: 1;");
                comboUfr.setStyle("-fx-opacity: 0.6;");
                break;
            case "ENSEIGNANT":
                comboNiveau.setDisable(true);
                comboFiliere.setDisable(true);
                comboUfr.setDisable(false);
                comboNiveau.setStyle("-fx-opacity: 0.6;");
                comboFiliere.setStyle("-fx-opacity: 0.6;");
                comboUfr.setStyle("-fx-opacity: 1;");
                comboNiveau.setValue(null);
                comboFiliere.setValue(null);
                break;
            case "ADMIN":
                comboNiveau.setDisable(true);
                comboFiliere.setDisable(true);
                comboUfr.setDisable(false);
                comboNiveau.setStyle("-fx-opacity: 0.6;");
                comboFiliere.setStyle("-fx-opacity: 0.6;");
                comboUfr.setStyle("-fx-opacity: 1;");
                comboNiveau.setValue(null);
                comboFiliere.setValue(null);
                break;
            default:
                break;
        }
    }

    public void setCodePermanentEditable(boolean editable) {
        txtCodePermanent.setEditable(editable);
        txtCodePermanent.setDisable(!editable);
        if (!editable) {
            txtCodePermanent.setStyle("-fx-background-radius: 8; -fx-padding: 8 12; -fx-background-color: #f5f5f5;");
        } else {
            txtCodePermanent.setStyle("-fx-background-radius: 8; -fx-padding: 8 12; -fx-background-color: white;");
        }
    }

    public void clearFormFields() {
        txtCodePermanent.clear();
        setCodePermanentEditable(true);
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtLogin.clear();
        comboRole.setValue("ETUDIANT");
        comboNiveau.setValue("L1");
        comboUfr.setValue("POLYTECHNIQUE");
        comboFiliere.setValue(null);
        ajusterChampsSelonRole("ETUDIANT");
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public TableView<User> getTableUsers() { return tableUsers; }
    public TextField getTxtSearch() { return txtSearch; }
    public TextField getTxtCodePermanent() { return txtCodePermanent; }
    public TextField getTxtNom() { return txtNom; }
    public TextField getTxtPrenom() { return txtPrenom; }
    public TextField getTxtEmail() { return txtEmail; }
    public TextField getTxtLogin() { return txtLogin; }
    public ComboBox<String> getComboRole() { return comboRole; }
    public ComboBox<String> getComboNiveau() { return comboNiveau; }
    public ComboBox<String> getComboUfr() { return comboUfr; }
    public ComboBox<String> getComboFiliere() { return comboFiliere; }
    public Button getBtnAjouter() { return btnAjouter; }
    public Button getBtnModifier() { return btnModifier; }
    public Button getBtnSupprimer() { return btnSupprimer; }
    public Button getBtnVider() { return btnVider; }
    public Button getBtnRetour() { return btnRetour; }
    public Button getBtnRefresh() { return btnRefresh; }
}