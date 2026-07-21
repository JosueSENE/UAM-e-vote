package app.view;

import app.model.Ufr;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

public class AdminUfrView extends BorderPane {

    private TableView<Ufr> tableUfr;
    private TableColumn<Ufr, Integer> colId;
    private TableColumn<Ufr, String> colNom;

    private TextField txtSearch;
    private TextField txtId;
    private TextField txtNom;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    public AdminUfrView() {
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

        Label lblTitle = new Label("Gestion des UFRs");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblSearch = new Label("Rechercher : ");
        lblSearch.setStyle("-fx-font-weight: bold;");
        txtSearch = new TextField();
        txtSearch.setPromptText("Id ou Nom");
        txtSearch.setPrefWidth(200);
        txtSearch.setStyle("-fx-background-radius: 15;");

        HBox searchBox = new HBox(5, lblSearch, txtSearch);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(btnRetour, lblTitle, spacer, searchBox);

        tableUfr = new TableView<>();
        tableUfr.setPlaceholder(new Label("Aucun contenu dans la table"));
        tableUfr.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableUfr.setPrefHeight(550);

        colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        tableUfr.getColumns().add(colId);
        tableUfr.getColumns().add(colNom);

        leftBox.getChildren().addAll(headerBox, tableUfr);
        this.setCenter(leftBox);
    }

    private void initRightFormSection() {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(15));
        formBox.setPrefWidth(320);
        formBox.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);");

        Label lblFormTitle = new Label("Informations UFR");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblFormTitle.setStyle("-fx-text-fill: #333;");

        txtId = new TextField();
        txtId.setPromptText("Auto-généré");
        txtId.setEditable(false);
        VBox fieldId = createFormField("Id :", txtId);

        txtNom = new TextField();
        txtNom.setPromptText("Ex: SAT");
        VBox fieldNom = createFormField("Nom :", txtNom);

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

        formBox.getChildren().addAll(lblFormTitle, new Separator(), fieldId, fieldNom, new Separator(), gridButtons);

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

    public TableView<Ufr> getTableUfr() { return tableUfr; }
    public TextField getTxtSearch() { return txtSearch; }
    public TextField getTxtId() { return txtId; }
    public TextField getTxtNom() { return txtNom; }
    public Button getBtnAjouter() { return btnAjouter; }
    public Button getBtnModifier() { return btnModifier; }
    public Button getBtnSupprimer() { return btnSupprimer; }
    public Button getBtnVider() { return btnVider; }
    public Button getBtnRetour() { return btnRetour; }

    public void clearFormFields() {
        txtId.clear();
        txtNom.clear();
    }
}