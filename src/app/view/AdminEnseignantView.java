package app.view;

import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminEnseignantView extends BorderPane {

    // ==========================================
    // TABLE
    // ==========================================
    
    private TableView<User> tableEnseignants;
    private TableColumn<User, Integer> colId;
    private TableColumn<User, Integer> colCodePermanent;
    private TableColumn<User, String> colNom;
    private TableColumn<User, String> colPrenom;
    private TableColumn<User, String> colEmail;
    private TableColumn<User, String> colLogin;
    private TableColumn<User, String> colUfr;
    private TableColumn<User, String> colDepartement;
    private TableColumn<User, String> colFilieres;
    private TableColumn<User, String> colStatus;

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
    private TextField txtPassword;
    private ComboBox<String> comboUfr;
    private ComboBox<String> comboDepartement;
    private ListView<String> listFilieres;
    private CheckBox chkActif;

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
    // DONNÉES RÉELLES DE L'UAM
    // ==========================================
    
    // UFR
    private static final String[] UFR_LIST = {"POLYTECHNIQUE", "UFR SEG", "UFR STA", "UFR TECNA"};
    
    // Départements - POLYTECHNIQUE
    private static final String[] DEPARTEMENTS_POLY = {
        "Sciences et Techniques de l'Ingénieur (DSTI)",
        "Sciences et Techniques Agronomiques, Alimentaires et Nutritionnelles (DST2AN)",
        "Gestion des Organisations (DGO)",
        "Urbanisme, Architecture, Aménagement Durable des Territoires (DU2ADT)",
        "Géosciences Appliquées et Environnement (DGAE)"
    };
    
    // Départements - UFR SEG
    private static final String[] DEPARTEMENTS_SEG = {
        "Économie",
        "Gestion"
    };
    
    // Départements - UFR STA
    private static final String[] DEPARTEMENTS_STA = {
        "Mathématiques, Informatique et Modélisation",
        "Sciences de la Mer et du Littoral"
    };
    
    // Départements - UFR TECNA
    private static final String[] DEPARTEMENTS_TECNA = {
        "Communication Numérique",
        "Audiovisuel",
        "Informatique des Médias",
        "Multimédia"
    };
    
    // ==========================================
    // FILIÈRES PAR DÉPARTEMENT
    // ==========================================
    
    // DSTI - Sciences et Techniques de l'Ingénieur
    private static final String[] FILIERES_DSTI = {
        "Génie des Procédés",
        "Infrastructures et Génie Civil",
        "Ingénierie Informatique",
        "Électronique et Télécommunications",
        "Systèmes Électriques et Énergétiques"
    };
    
    // DST2AN - Sciences et Techniques Agronomiques
    private static final String[] FILIERES_DST2AN = {
        "Agroécologie et Productions Végétales",
        "Productions Animales et Systèmes d'Élevage Durables",
        "Technologies Agroalimentaires Durables",
        "Leadership, Innovation et Conseil Agroenvironnemental"
    };
    
    // DGO - Gestion des Organisations
    private static final String[] FILIERES_DGO = {
        "Finance et Comptabilité",
        "Management des Organisations"
    };
    
    // DU2ADT - Urbanisme, Architecture
    private static final String[] FILIERES_DU2ADT = {
        "Urbanisme",
        "Architecture"
    };
    
    // DGAE - Géosciences et Environnement
    private static final String[] FILIERES_DGAE = {
        "Environnement",
        "Géomatique",
        "Hydraulique et Assainissement",
        "Mines et Géologie"
    };
    
    // UFR STA - Mathématiques, Informatique et Modélisation
    private static final String[] FILIERES_MIM = {
        "Mathématiques - Physique - Informatique (MPI)",
        "Mathématiques Appliquées aux Sciences Sociales (MASS)",
        "Mathématiques",
        "Physique",
        "Informatique"
    };
    
    // UFR STA - Sciences Physiques et Technologies
    private static final String[] FILIERES_SMU = {
        "Sciences Physiques",
        "Science de la Mer et du Littoral"
    };
    
    // UFR SEG - Économie
    private static final String[] FILIERES_ECONOMIE = {
        "Licence en Économie",
        "Masters en Économie"
    };
    
    // UFR SEG - Gestion
    private static final String[] FILIERES_GESTION = {
        "Finance-Comptabilité",
        "Management des Organisations",
        "Marketing",
        "Comptabilité",
        "Gestion Financière"
    };
    
    // UFR TECNA - Communication Numérique
    private static final String[] FILIERES_COMMUNICATION = {
        "Communication Numérique",
        "Journalisme Numérique"
    };
    
    // UFR TECNA - Audiovisuel
    private static final String[] FILIERES_AUDIOVISUEL = {
        "Audiovisuel"
    };
    
    // UFR TECNA - Multimédia
    private static final String[] FILIERES_MULTIMEDIA = {
        "Création Multimédia"
    };
    
    // UFR TECNA - Informatique des Médias
    private static final String[] FILIERES_MEDIAS = {
        "Développement des Médias Numériques"
    };

    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

    public AdminEnseignantView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f2f5;");
        
        initLeftTableSection();
        initRightFormSection();
        
        // Initialisation
        updateDepartementList();
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

        Label lblTitle = new Label("👨‍🏫 Gestion des Enseignants");
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
        
        btnRefresh = new Button("🔄");
        btnRefresh.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnRefresh.setTooltip(new Tooltip("Rafraîchir la liste"));
        btnRefresh.setOnMouseEntered(e -> btnRefresh.setStyle("-fx-background-color: #006699; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        btnRefresh.setOnMouseExited(e -> btnRefresh.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));

        HBox searchBox = new HBox(5, lblSearch, txtSearch, btnRefresh);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(btnRetour, lblTitle, spacer, searchBox);

        // ===== TABLE =====
        tableEnseignants = new TableView<>();
        tableEnseignants.setPlaceholder(new Label("📋 Aucun enseignant trouvé"));
        tableEnseignants.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableEnseignants.setPrefHeight(550);
        tableEnseignants.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");

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
        colNom.setPrefWidth(120);

        colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(120);

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);

        colLogin = new TableColumn<>("Login");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colLogin.setPrefWidth(120);

        colUfr = new TableColumn<>("UFR");
        colUfr.setCellValueFactory(new PropertyValueFactory<>("ufrNom"));
        colUfr.setPrefWidth(100);

        colDepartement = new TableColumn<>("Département");
        colDepartement.setCellValueFactory(new PropertyValueFactory<>("departementNom"));
        colDepartement.setPrefWidth(150);

        colFilieres = new TableColumn<>("Filières");
        colFilieres.setCellValueFactory(new PropertyValueFactory<>("filieresList"));
        colFilieres.setPrefWidth(180);

        colStatus = new TableColumn<>("Statut");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(80);
        colStatus.setStyle("-fx-alignment: CENTER;");

        tableEnseignants.getColumns().addAll(
            colId, colCodePermanent, colNom, colPrenom, 
            colEmail, colLogin, colUfr, colDepartement, colFilieres, colStatus
        );

        leftBox.getChildren().addAll(headerBox, tableEnseignants);
        this.setCenter(leftBox);
    }

    // ==========================================
    // SECTION FORMULAIRE (DROITE)
    // ==========================================

    private void initRightFormSection() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(20));
        formBox.setPrefWidth(420);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        // ===== TITRE =====
        Label lblFormTitle = new Label("✏️ Informations Enseignant");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblFormTitle.setStyle("-fx-text-fill: #005088;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");

        // ===== CHAMPS =====
        // Code Permanent
        txtCodePermanent = new TextField();
        txtCodePermanent.setPromptText("Ex: 123456");
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
        txtNom.setPromptText("Ex: Diop");
        txtNom.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldNom = createFormField("👤 Nom *", txtNom);

        // Prénom
        txtPrenom = new TextField();
        txtPrenom.setPromptText("Ex: Aliou");
        txtPrenom.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldPrenom = createFormField("👤 Prénom *", txtPrenom);

        // Email
        txtEmail = new TextField();
        txtEmail.setPromptText("aliou.diop@uam.edu.sn");
        txtEmail.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldEmail = createFormField("📧 Email *", txtEmail);

        // Login
        txtLogin = new TextField();
        txtLogin.setPromptText("aliou.diop");
        txtLogin.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldLogin = createFormField("🔑 Login *", txtLogin);

        // Mot de passe
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Nouveau mot de passe (optionnel)");
        txtPassword.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        VBox fieldPassword = createFormField("🔐 Mot de passe", txtPassword);

        // UFR
        comboUfr = new ComboBox<>();
        comboUfr.getItems().addAll(UFR_LIST);
        comboUfr.setPromptText("Sélectionner un UFR");
        comboUfr.setMaxWidth(Double.MAX_VALUE);
        comboUfr.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        comboUfr.setValue("POLYTECHNIQUE");
        comboUfr.setOnAction(e -> {
            updateDepartementList();
            listFilieres.getItems().clear();
        });
        VBox fieldUfr = createFormField("🏛️ UFR *", comboUfr);

        // Département
        comboDepartement = new ComboBox<>();
        comboDepartement.setPromptText("Sélectionner un département");
        comboDepartement.setMaxWidth(Double.MAX_VALUE);
        comboDepartement.setStyle("-fx-background-radius: 8; -fx-padding: 8 12;");
        comboDepartement.setOnAction(e -> updateFiliereList());
        VBox fieldDepartement = createFormField("🏫 Département *", comboDepartement);

        // Filières (multisélection)
        Label lblFilieres = new Label("📚 Filières enseignées *");
        lblFilieres.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lblFilieres.setStyle("-fx-text-fill: #555;");
        
        listFilieres = new ListView<>();
        listFilieres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listFilieres.setPrefHeight(120);
        listFilieres.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        
        VBox fieldFilieres = new VBox(5);
        fieldFilieres.getChildren().addAll(lblFilieres, listFilieres);

        // Actif
        chkActif = new CheckBox("Compte actif");
        chkActif.setSelected(true);
        chkActif.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

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
            fieldPassword,
            fieldUfr,
            fieldDepartement,
            fieldFilieres,
            chkActif,
            new Separator(),
            gridButtons
        );

        HBox rightBox = new HBox(formBox);
        rightBox.setPadding(new Insets(10));
        this.setRight(rightBox);
    }

    // ==========================================
    // MÉTHODES DE MISE À JOUR DES LISTES
    // ==========================================

    /**
     * Met à jour la liste des départements en fonction de l'UFR sélectionné
     */
    private void updateDepartementList() {
        String ufr = comboUfr.getValue();
        comboDepartement.getItems().clear();
        listFilieres.getItems().clear();
        
        if (ufr == null) return;
        
        switch(ufr) {
            case "POLYTECHNIQUE":
                comboDepartement.getItems().addAll(DEPARTEMENTS_POLY);
                break;
            case "UFR SEG":
                comboDepartement.getItems().addAll(DEPARTEMENTS_SEG);
                break;
            case "UFR STA":
                comboDepartement.getItems().addAll(DEPARTEMENTS_STA);
                break;
            case "UFR TECNA":
                comboDepartement.getItems().addAll(DEPARTEMENTS_TECNA);
                break;
        }
        
        if (!comboDepartement.getItems().isEmpty()) {
            comboDepartement.setValue(comboDepartement.getItems().get(0));
            updateFiliereList();
        }
    }

    /**
     * Met à jour la liste des filières en fonction du département sélectionné
     */
    private void updateFiliereList() {
        String departement = comboDepartement.getValue();
        listFilieres.getItems().clear();
        
        if (departement == null) return;
        
        switch(departement) {
            // POLYTECHNIQUE - DSTI
            case "Sciences et Techniques de l'Ingénieur (DSTI)":
                listFilieres.getItems().addAll(FILIERES_DSTI);
                break;
                
            // POLYTECHNIQUE - DST2AN
            case "Sciences et Techniques Agronomiques, Alimentaires et Nutritionnelles (DST2AN)":
                listFilieres.getItems().addAll(FILIERES_DST2AN);
                break;
                
            // POLYTECHNIQUE - DGO
            case "Gestion des Organisations (DGO)":
                listFilieres.getItems().addAll(FILIERES_DGO);
                break;
                
            // POLYTECHNIQUE - DU2ADT
            case "Urbanisme, Architecture, Aménagement Durable des Territoires (DU2ADT)":
                listFilieres.getItems().addAll(FILIERES_DU2ADT);
                break;
                
            // POLYTECHNIQUE - DGAE
            case "Géosciences Appliquées et Environnement (DGAE)":
                listFilieres.getItems().addAll(FILIERES_DGAE);
                break;
                
            // UFR STA - Mathématiques, Informatique et Modélisation
            case "Mathématiques, Informatique et Modélisation":
                listFilieres.getItems().addAll(FILIERES_MIM);
                break;
                
            // UFR STA - Sciences Physiques et Technologies
            case "Sciences Physiques et Technologies":
                listFilieres.getItems().addAll(FILIERES_SMU);
                break;
                
            // UFR SEG - Économie
            case "Économie":
                listFilieres.getItems().addAll(FILIERES_ECONOMIE);
                break;
                
            // UFR SEG - Gestion
            case "Gestion":
                listFilieres.getItems().addAll(FILIERES_GESTION);
                break;
                
            // UFR TECNA - Communication Numérique
            case "Communication Numérique":
                listFilieres.getItems().addAll(FILIERES_COMMUNICATION);
                break;
                
            // UFR TECNA - Audiovisuel
            case "Audiovisuel":
                listFilieres.getItems().addAll(FILIERES_AUDIOVISUEL);
                break;
                
            // UFR TECNA - Multimédia
            case "Multimédia":
                listFilieres.getItems().addAll(FILIERES_MULTIMEDIA);
                break;
                
            // UFR TECNA - Informatique des Médias
            case "Informatique des Médias":
                listFilieres.getItems().addAll(FILIERES_MEDIAS);
                break;
                
            default:
                // Cas par défaut
                listFilieres.getItems().add("Filière non définie");
                break;
        }
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
        txtPassword.clear();
        comboUfr.setValue("POLYTECHNIQUE");
        updateDepartementList();
        listFilieres.getSelectionModel().clearSelection();
        chkActif.setSelected(true);
    }

    public String getSelectedFilieresAsString() {
        StringBuilder sb = new StringBuilder();
        for (String filiere : listFilieres.getSelectionModel().getSelectedItems()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(filiere);
        }
        return sb.toString();
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public TableView<User> getTableEnseignants() { return tableEnseignants; }
    public TextField getTxtSearch() { return txtSearch; }
    public TextField getTxtCodePermanent() { return txtCodePermanent; }
    public TextField getTxtNom() { return txtNom; }
    public TextField getTxtPrenom() { return txtPrenom; }
    public TextField getTxtEmail() { return txtEmail; }
    public TextField getTxtLogin() { return txtLogin; }
    public TextField getTxtPassword() { return txtPassword; }
    public ComboBox<String> getComboUfr() { return comboUfr; }
    public ComboBox<String> getComboDepartement() { return comboDepartement; }
    public ListView<String> getListFilieres() { return listFilieres; }
    public CheckBox getChkActif() { return chkActif; }
    public Button getBtnAjouter() { return btnAjouter; }
    public Button getBtnModifier() { return btnModifier; }
    public Button getBtnSupprimer() { return btnSupprimer; }
    public Button getBtnVider() { return btnVider; }
    public Button getBtnRetour() { return btnRetour; }
    public Button getBtnRefresh() { return btnRefresh; }
}