package app.view;

import app.model.Election;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;

public class AdminElectionView extends BorderPane {

    private TableView<Election> tableElections;
    private TableColumn<Election, Integer> colId;
    private TableColumn<Election, String> colTitre;
    private TableColumn<Election, String> colType;
    private TableColumn<Election, LocalDateTime> colDebut;
    private TableColumn<Election, LocalDateTime> colFin;
    private TableColumn<Election, String> colStatut;
    private TableColumn<Election, String> colCible;
    private TableColumn<Election, String> colNiveau;
    private TableColumn<Election, String> colProfession;

    private TextField txtSearch;
    private TextField txtId;
    private TextField txtTitre;
    private TextField txtType;
    private DatePicker txtDebut;
    private DatePicker txtFin;

    private ComboBox<String> cbTypeCible;
    private TextField txtCibleId;
    private ComboBox<String> cbNiveau;
    private ComboBox<String> cbProfession;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    public AdminElectionView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa;");

        this.cbTypeCible = new ComboBox<>();
        this.cbTypeCible.getItems().addAll("TOUS", "UFR", "DEPARTEMENT", "FILIERE");
        this.cbTypeCible.setValue("TOUS");

        this.txtCibleId = new TextField();
        this.txtCibleId.setPromptText("ID Cible");

        this.cbNiveau = new ComboBox<>();
        this.cbNiveau.getItems().addAll("", "L1", "L2", "L3", "M1", "M2");

        this.cbProfession = new ComboBox<>();
        this.cbProfession.getItems().addAll("", "ETUDIANTS", "ENSEIGNANTS");

        initLeftTableSection();
        initRightFormSection();
    }

    private void initLeftTableSection() {
        VBox leftBox = new VBox(15);
        leftBox.setPadding(new Insets(10));
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnRetour.setOnMouseEntered(e -> btnRetour.setCursor(javafx.scene.Cursor.HAND));
        btnRetour.setOnMouseExited(e -> btnRetour.setCursor(javafx.scene.Cursor.DEFAULT));

        Label lblTitle = new Label("Gestion des Élections");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblSearch = new Label("Rechercher : ");
        lblSearch.setStyle("-fx-font-weight: bold;");
        txtSearch = new TextField();
        txtSearch.setPromptText("Id, titre, type...");
        txtSearch.setPrefWidth(200);
        txtSearch.setStyle("-fx-background-radius: 15;");

        HBox searchBox = new HBox(5, lblSearch, txtSearch);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(btnRetour, lblTitle, spacer, searchBox);

        tableElections = new TableView<>();
        tableElections.setPlaceholder(new Label("Aucun contenu dans la table"));
        tableElections.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableElections.setPrefHeight(550);

        colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));

        colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("typeElection"));

        colDebut = new TableColumn<>("Date Début");
        colDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));

        colFin = new TableColumn<>("Date Fin");
        colFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colCible = new TableColumn<>("Cible (ID - Nom)");
        colCible.setCellValueFactory(cell -> {
            Election el = cell.getValue();
            if (el == null) return new SimpleStringProperty("N/A");

            StringBuilder sb = new StringBuilder();
            if (el.getCible_ufr_id() != null) {
                sb.append("UFR [ID: ").append(el.getCible_ufr_id()).append(" - ").append(el.getUfrNom()).append("]");
            } else if (el.getCible_departement_id() != null) {
                sb.append("Dépt [ID: ").append(el.getCible_departement_id()).append(" - ").append(el.getDepartementNom()).append("]");
            } else if (el.getCible_filiere_id() != null) {
                sb.append("Filière [ID: ").append(el.getCible_filiere_id()).append(" - ").append(el.getFiliereNom()).append("]");
            } else {
                sb.append("Toute l'UAM");
            }
            return new SimpleStringProperty(sb.toString());
        });

        colNiveau = new TableColumn<>("Niveau");
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("cible_niveau"));

        colProfession = new TableColumn<>("Profession");
        colProfession.setCellValueFactory(new PropertyValueFactory<>("cible_profession"));

        tableElections.getColumns().add(colId);
        tableElections.getColumns().add(colTitre);
        tableElections.getColumns().add(colType);
        tableElections.getColumns().add(colDebut);
        tableElections.getColumns().add(colFin);
        tableElections.getColumns().add(colStatut);
        tableElections.getColumns().add(colCible);
        tableElections.getColumns().add(colNiveau);
        tableElections.getColumns().add(colProfession);

        leftBox.getChildren().addAll(headerBox, tableElections);
        this.setCenter(leftBox);
    }

    private void initRightFormSection() {
        VBox formBox = new VBox(8);
        formBox.setPadding(new Insets(15));
        formBox.setPrefWidth(320);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);");

        Label lblFormTitle = new Label("Informations Élection");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblFormTitle.setStyle("-fx-text-fill: #333;");

        txtId = new TextField();
        txtId.setPromptText("Auto-généré");
        txtId.setEditable(false);
        VBox fieldId = createFormField("Id :", txtId);

        txtTitre = new TextField();
        txtTitre.setPromptText("Ex: Élection Délégué");
        VBox fieldTitre = createFormField("Titre :", txtTitre);

        txtType = new TextField();
        txtType.setPromptText("Ex: Général");
        VBox fieldType = createFormField("Type :", txtType);

        txtDebut = new DatePicker();
        VBox fieldDebut = createFormField("Début :", txtDebut);

        txtFin = new DatePicker();
        VBox fieldFin = createFormField("Fin :", txtFin);

        VBox fieldTypeCible = createFormField("Type Cible :", cbTypeCible);
        VBox fieldCibleId = createFormField("ID Cible :", txtCibleId);
        VBox fieldNiveau = createFormField("Niveau :", cbNiveau);
        VBox fieldProfession = createFormField("Profession :", cbProfession);

        GridPane gridButtons = new GridPane();
        gridButtons.setHgap(10);
        gridButtons.setVgap(10);

        btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #2ec4b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setCursor(javafx.scene.Cursor.HAND);

        btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnModifier.setMaxWidth(Double.MAX_VALUE);
        btnModifier.setCursor(javafx.scene.Cursor.HAND);

        btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnSupprimer.setMaxWidth(Double.MAX_VALUE);
        btnSupprimer.setCursor(javafx.scene.Cursor.HAND);

        btnVider = new Button("Vider");
        btnVider.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnVider.setMaxWidth(Double.MAX_VALUE);
        btnVider.setCursor(javafx.scene.Cursor.HAND);

        gridButtons.add(btnAjouter, 0, 0);
        gridButtons.add(btnModifier, 1, 0);
        gridButtons.add(btnSupprimer, 0, 1);
        gridButtons.add(btnVider, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridButtons.getColumnConstraints().addAll(col1, col2);

        formBox.getChildren().addAll(
                lblFormTitle, 
                new Separator(), 
                fieldId, 
                fieldTitre, 
                fieldType, 
                fieldDebut, 
                fieldFin, 
                fieldTypeCible,
                fieldCibleId,
                fieldNiveau,
                fieldProfession,
                new Separator(), 
                gridButtons
        );

        HBox rightBox = new HBox(formBox);
        rightBox.setPadding(new Insets(10));
        this.setRight(rightBox);
    }

    private VBox createFormField(String labelText, Control input) {
        VBox vbox = new VBox(3);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lbl.setStyle("-fx-text-fill: #555;");
        vbox.getChildren().addAll(lbl, input);
        return vbox;
    }

    public TableView<Election> getTableElections() { return tableElections; }
    public TextField getTxtSearch() { return txtSearch; }
    public TextField getTxtId() { return txtId; }
    public TextField getTxtTitre() { return txtTitre; }
    public TextField getTxtType() { return txtType; }
    public DatePicker getTxtDebut() { return txtDebut; }
    public DatePicker getTxtFin() { return txtFin; }
    public ComboBox<String> getCbTypeCible() { return cbTypeCible; }
    public TextField getTxtCibleId() { return txtCibleId; }
    public ComboBox<String> getCbNiveau() { return cbNiveau; }
    public ComboBox<String> getCbProfession() { return cbProfession; }
    public Button getBtnAjouter() { return btnAjouter; }
    public Button getBtnModifier() { return btnModifier; }
    public Button getBtnSupprimer() { return btnSupprimer; }
    public Button getBtnVider() { return btnVider; }
    public Button getBtnRetour() { return btnRetour; }

    public void clearFormFields() {
        txtId.clear();
        txtTitre.clear();
        txtType.clear();
        txtDebut.setValue(null);
        txtFin.setValue(null);
        cbTypeCible.setValue("TOUS");
        txtCibleId.clear();
        cbNiveau.setValue(null);
        cbProfession.setValue(null);
    }
}