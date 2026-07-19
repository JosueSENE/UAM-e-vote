package app.view;

import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminUsersView extends BorderPane {

    private TableView<User> tableUsers;
    private TableColumn<User, Integer> colCodePermanent;
    private TableColumn<User, String> colNom;
    private TableColumn<User, String> colPrenom;
    private TableColumn<User, String> colEmail;
    private TableColumn<User, String> colProfession;

    private TextField txtSearch;
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private ComboBox<String> comboProfession;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    public AdminUsersView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa;");
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

        Label lblTitle = new Label("Gestion des Électeurs");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblSearch = new Label("Rechercher : ");
        lblSearch.setStyle("-fx-font-weight: bold;");
        txtSearch = new TextField();
        txtSearch.setPromptText("Nom, prénom, code permanent...");
        txtSearch.setPrefWidth(200);
        txtSearch.setStyle("-fx-background-radius: 15;");

        HBox searchBox = new HBox(5, lblSearch, txtSearch);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(btnRetour, lblTitle, spacer, searchBox);

        tableUsers = new TableView<>();
        tableUsers.setPlaceholder(new Label("Aucun contenu dans la table"));
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableUsers.setPrefHeight(550);

        colCodePermanent = new TableColumn<>("Code Permanent");
        colCodePermanent.setCellValueFactory(new PropertyValueFactory<>("codePermanent"));

        colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colProfession = new TableColumn<>("Profession");
        colProfession.setCellValueFactory(new PropertyValueFactory<>("profession"));

        tableUsers.getColumns().add(colCodePermanent);
        tableUsers.getColumns().add(colNom);
        tableUsers.getColumns().add(colPrenom);
        tableUsers.getColumns().add(colEmail);
        tableUsers.getColumns().add(colProfession);

        leftBox.getChildren().addAll(headerBox, tableUsers);
        this.setCenter(leftBox);
    }

    private void initRightFormSection() {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(15));
        formBox.setPrefWidth(320);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);");

        Label lblFormTitle = new Label("Informations Électeur");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblFormTitle.setStyle("-fx-text-fill: #333;");

        txtCodePermanent = new TextField();
        txtCodePermanent.setPromptText("Ex: 501699");
        txtCodePermanent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCodePermanent.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtCodePermanent.getText().length() > 6) {
                String s = txtCodePermanent.getText().substring(0, 6);
                txtCodePermanent.setText(s);
            }
        });
        VBox fieldCodePermanent = createFormField("Code Permanent  :", txtCodePermanent);

        txtNom = new TextField();
        txtNom.setPromptText("Ex: Ndiaye");
        VBox fieldNom = createFormField("Nom :", txtNom);

        txtPrenom = new TextField();
        txtPrenom.setPromptText("Ex: Nicolo Zaniolo");
        VBox fieldPrenom = createFormField("Prénom :", txtPrenom);

        txtEmail = new TextField();
        txtEmail.setPromptText("zaniolo.nicolo@uam.edu.sn");
        VBox fieldEmail = createFormField("Email :", txtEmail);

        comboProfession = new ComboBox<>();
        comboProfession.getItems().addAll("ADMINISTRATION", "ETUDIANT", "ENSEIGNANT");
        comboProfession.setPromptText("Sélectionner la Profession");
        comboProfession.setMaxWidth(Double.MAX_VALUE);
        VBox fieldProfession = createFormField("Profession :", comboProfession);

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

        formBox.getChildren().addAll(lblFormTitle, new Separator(), fieldCodePermanent, fieldNom, fieldPrenom, fieldEmail, fieldProfession, new Separator(), gridButtons);

        HBox rightBox = new HBox(formBox);
        rightBox.setPadding(new Insets(10));
        this.setRight(rightBox);
    }

    private VBox createFormField(String labelText, Control input) {
        VBox vbox = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lbl.setStyle("-fx-text-fill: #555;");
        vbox.getChildren().addAll(lbl, input);
        return vbox;
    }

    public TableView<User> getTableUsers() {return tableUsers;}
    public TextField getTxtSearch() {return txtSearch;}
    public TextField getTxtCodePermanent() {return txtCodePermanent;}
    public TextField getTxtNom() {return txtNom;}
    public TextField getTxtPrenom() {return txtPrenom;}
    public TextField getTxtEmail() {return txtEmail;}
    public ComboBox<String> getComboProfession() {return comboProfession;}
    public Button getBtnAjouter() {return btnAjouter;}
    public Button getBtnModifier() {return btnModifier;}
    public Button getBtnSupprimer() {return btnSupprimer;}
    public Button getBtnVider() {return btnVider;}
    public Button getBtnRetour() {return btnRetour;}

    public void setCodePermanentEditable(boolean editable) {
        txtCodePermanent.setDisable(!editable);
    }

    public void clearFormFields() {
        txtCodePermanent.clear();
        setCodePermanentEditable(true);
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        comboProfession.setValue(null);
    }
}
