package app.controller;

import app.dao.UserDAO;
import app.model.User;
import app.model.Filiere;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.sql.SQLException;
import java.util.List;

public class AdminController extends BorderPane {

    // Éléments de la Table
    private TableView<User> tableUsers;
    private TableColumn<User, Long> colCodePermanent;
    private TableColumn<User, String> colNom;
    private TableColumn<User, String> colPrenom;
    private TableColumn<User, String> colEmail;
    private TableColumn<User, String> colProfession;
    private TableColumn<User, String> colFiliere;
    private TableColumn<User, String> colNiveau;

    // Éléments du Formulaire
    private TextField txtSearch;
    private TextField txtCodePermanent;
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private ComboBox<String> comboProfession;
    private ComboBox<Filiere> comboFiliere;
    private ComboBox<String> comboNiveau;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;

    private UserDAO userDAO;
    private ObservableList<User> userList;
    private ObservableList<Filiere> filiereList;

    public AdminController() {
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        this.filiereList = FXCollections.observableArrayList();
        
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa;");

        initLeftTableSection();
        initRightFormSection();
        
        loadUsersData();
        loadFiliereData();
        setupSelectionListener();
        setupRealtimeSearch();
        setupProfessionListener();
    }
    // PARTIE GAUCHE : TABLE
    private void initLeftTableSection() {
        VBox leftBox = new VBox(15);
        leftBox.setPadding(new Insets(10));
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("Gestion des Utilisateurs");
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

        headerBox.getChildren().addAll(lblTitle, spacer, searchBox);

        tableUsers = new TableView<>();
        tableUsers.setPlaceholder(new Label("Aucun contenu dans la table"));
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableUsers.setPrefHeight(550);

        colCodePermanent = new TableColumn<>("Code Permanent");
        colCodePermanent.setCellValueFactory(new PropertyValueFactory<>("code_permanent"));

        colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colProfession = new TableColumn<>("Profession");
        colProfession.setCellValueFactory(new PropertyValueFactory<>("profession"));

        // ✅ COLONNE FILIERE avec gestion d'exception
        colFiliere = new TableColumn<>("Filière");
        colFiliere.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user.getFiliere_id() != null) {
                try {
                    String nomFiliere = userDAO.getFiliereNameById(user.getFiliere_id());
                    return new javafx.beans.property.SimpleStringProperty(nomFiliere);
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la récupération du nom de la filière : " + e.getMessage());
                    return new javafx.beans.property.SimpleStringProperty("Erreur");
                }
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colNiveau = new TableColumn<>("Niveau");
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        tableUsers.getColumns().addAll(colCodePermanent, colNom, colPrenom, colEmail, 
                                       colProfession, colFiliere, colNiveau);

        leftBox.getChildren().addAll(headerBox, tableUsers);
        this.setCenter(leftBox);
    }

    // PARTIE DROITE : FORMULAIRE
    private void initRightFormSection() {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(15));
        formBox.setPrefWidth(350);
        formBox.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);");

        Label lblFormTitle = new Label("Informations Utilisateur");
        lblFormTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblFormTitle.setStyle("-fx-text-fill: #333;");

        // ✅ Code Permanent : 6 chiffres
        txtCodePermanent = new TextField();
        txtCodePermanent.setPromptText("Ex: 501234");
        txtCodePermanent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCodePermanent.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtCodePermanent.getText().length() > 6) {
                String s = txtCodePermanent.getText().substring(0, 6);
                txtCodePermanent.setText(s);
            }
        });
        VBox fieldCodePermanent = createFormField("Code Permanent (6 chiffres) :", txtCodePermanent);

        txtNom = new TextField();
        txtNom.setPromptText("Ex: Ndiaye");
        VBox fieldNom = createFormField("Nom :", txtNom);

        txtPrenom = new TextField();
        txtPrenom.setPromptText("Ex: Nicolo");
        VBox fieldPrenom = createFormField("Prénom :", txtPrenom);

        txtEmail = new TextField();
        txtEmail.setPromptText("nicolo.ndiaye@uam.edu.sn");
        VBox fieldEmail = createFormField("Email :", txtEmail);

        // ✅ Profession
        comboProfession = new ComboBox<>();
        comboProfession.getItems().addAll("ETUDIANT", "ENSEIGNANT", "ADMIN");
        comboProfession.setPromptText("Sélectionner la Profession");
        comboProfession.setMaxWidth(Double.MAX_VALUE);
        VBox fieldProfession = createFormField("Profession :", comboProfession);

        // ✅ ComboBox Filiere avec affichage correct des noms
        comboFiliere = new ComboBox<>();
        comboFiliere.setPromptText("Sélectionner la Filière");
        comboFiliere.setMaxWidth(Double.MAX_VALUE);
        comboFiliere.setVisible(false);
        comboFiliere.setManaged(false);
        
        // 👇 CORRECTION : Afficher le nom de la filière dans la liste déroulante
        comboFiliere.setCellFactory(new Callback<ListView<Filiere>, ListCell<Filiere>>() {
            @Override
            public ListCell<Filiere> call(ListView<Filiere> param) {
                return new ListCell<Filiere>() {
                    @Override
                    protected void updateItem(Filiere filiere, boolean empty) {
                        super.updateItem(filiere, empty);
                        if (empty || filiere == null) {
                            setText(null);
                        } else {
                            setText(filiere.getNom());
                        }
                    }
                };
            }
        });
        
        //  Afficher le nom dans le bouton de la ComboBox
        comboFiliere.setButtonCell(new ListCell<Filiere>() {
            @Override
            protected void updateItem(Filiere filiere, boolean empty) {
                super.updateItem(filiere, empty);
                if (empty || filiere == null) {
                    setText(null);
                } else {
                    setText(filiere.getNom());
                }
            }
        });
        
        VBox fieldFiliere = createFormField("Filière :", comboFiliere);

        // ✅ Niveau
        comboNiveau = new ComboBox<>();
        comboNiveau.getItems().addAll("L1", "L2", "L3", "M1", "M2");
        comboNiveau.setPromptText("Sélectionner le Niveau");
        comboNiveau.setMaxWidth(Double.MAX_VALUE);
        comboNiveau.setVisible(false);
        comboNiveau.setManaged(false);
        VBox fieldNiveau = createFormField("Niveau :", comboNiveau);

        // Boutons
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

        btnAjouter.setOnAction(e -> gererAjout());
        btnModifier.setOnAction(e -> gererModification());
        btnSupprimer.setOnAction(e -> gererSuppression());
        btnVider.setOnAction(e -> clearForm());

        Button btnRetour = new Button("Retour au Tableau de Bord");
        btnRetour.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnRetour.setMaxWidth(Double.MAX_VALUE);
        btnRetour.setCursor(javafx.scene.Cursor.HAND);
        btnRetour.setOnAction(e -> retourAuTableauBord());

        formBox.getChildren().addAll(
            lblFormTitle, new Separator(),
            fieldCodePermanent, fieldNom, fieldPrenom, fieldEmail, fieldProfession,
            fieldFiliere, fieldNiveau,
            new Separator(), gridButtons, new Separator(), btnRetour
        );

        HBox rightBox = new HBox(formBox);
        rightBox.setPadding(new Insets(10));
        this.setRight(rightBox);
    }

    // MÉTHODES UTILITAIRES
    private VBox createFormField(String labelText, Control input) {
        VBox vbox = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lbl.setStyle("-fx-text-fill: #555;");
        vbox.getChildren().addAll(lbl, input);
        return vbox;
    }

    // ✅ Chargement des utilisateurs avec gestion d'exception
    private void loadUsersData() {
        try {
            List<User> list = userDAO.getAllUsers();
            userList.setAll(list);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des utilisateurs : " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger les utilisateurs.\nVérifiez la connexion à la base de données.");
        }
    }

    // ✅ Chargement des filières avec gestion d'exception
    private void loadFiliereData() {
        try {
            List<Filiere> filieres = userDAO.getAllFilieres();
            filiereList.setAll(filieres);
            comboFiliere.setItems(filiereList);
            System.out.println("✅ " + filieres.size() + " filières chargées depuis la base.");
            
            // Debug : Afficher les noms des filières chargées
            for (Filiere f : filieres) {
                System.out.println("   - " + f.getId() + " : " + f.getNom());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des filières : " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger les filières.\nVérifiez la connexion à la base de données.");
        }
    }

    // ✅ Adaptation du formulaire selon la profession
    private void setupProfessionListener() {
        comboProfession.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isEtudiant = "ETUDIANT".equals(newVal);
            
            comboFiliere.setVisible(isEtudiant);
            comboFiliere.setManaged(isEtudiant);
            comboNiveau.setVisible(isEtudiant);
            comboNiveau.setManaged(isEtudiant);
            
            if (!isEtudiant) {
                comboFiliere.setValue(null);
                comboNiveau.setValue(null);
            }
        });
    }

    // ✅ Sélection dans la table avec gestion d'exception
    private void setupSelectionListener() {
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtCodePermanent.setText(String.valueOf(newSelection.getCode_permanent()));
                txtCodePermanent.setDisable(true);
                txtNom.setText(newSelection.getNom());
                txtPrenom.setText(newSelection.getPrenom());
                txtEmail.setText(newSelection.getEmail());
                comboProfession.setValue(newSelection.getProfession());
                
                // ✅ Remplir la filière si elle existe
                if (newSelection.getFiliere_id() != null) {
                    try {
                        String nomFiliere = userDAO.getFiliereNameById(newSelection.getFiliere_id());
                        // Trouver l'objet Filiere correspondant
                        for (Filiere f : filiereList) {
                            if (f.getNom().equals(nomFiliere)) {
                                comboFiliere.setValue(f);
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("❌ Erreur lors de la récupération du nom de la filière : " + e.getMessage());
                        comboFiliere.setValue(null);
                    }
                } else {
                    comboFiliere.setValue(null);
                }
                
                comboNiveau.setValue(newSelection.getNiveau());
            }
        });
    }

    // ✅ Recherche en temps réel
    private void setupRealtimeSearch() {
        FilteredList<User> filteredData = new FilteredList<>(userList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (user.getNom() != null && user.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(user.getCode_permanent()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getProfession() != null && user.getProfession().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUsers.comparatorProperty());
        tableUsers.setItems(sortedData);
    }

    // ==========================================
    // CRUD avec gestion des exceptions
    // ==========================================
    private void gererAjout() {
        String codePermanentStr = txtCodePermanent.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String profession = comboProfession.getValue();

        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (codePermanentStr.length() != 6) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", 
                "Le Code Permanent doit être composé de 6 chiffres exactement.");
            return;
        }

        try {
            Long codePermanent = Long.parseLong(codePermanentStr);

            User u = new User();
            u.setCode_permanent(codePermanent);
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setEmail(email);
            u.setProfession(profession);

            if ("ETUDIANT".equals(profession)) {
                Filiere filiere = comboFiliere.getValue();
                String niveau = comboNiveau.getValue();
                
                if (filiere == null || niveau == null) {
                    afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                        "Pour un étudiant, la filière et le niveau sont obligatoires.");
                    return;
                }
                
                u.setFiliere_id(filiere.getId());
                u.setNiveau(niveau);
            } else {
                u.setFiliere_id(null);
                u.setNiveau(null);
            }

            // ✅ Gestion de l'exception SQL
            try {
                if (userDAO.addUser(u)) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'utilisateur a été ajouté avec succès.");
                    loadUsersData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur.");
                }
            } catch (SQLException e) {
                System.err.println("❌ Erreur SQL lors de l'ajout : " + e.getMessage());
                e.printStackTrace();
                afficherAlerte(Alert.AlertType.ERROR, "Erreur Base de données", 
                    "Impossible d'ajouter l'utilisateur.\n" + e.getMessage());
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "Le Code Permanent doit être un nombre.");
        }
    }

    private void gererModification() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord un utilisateur.");
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String profession = comboProfession.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Les informations ne peuvent pas être vides.");
            return;
        }

        selectedUser.setNom(nom);
        selectedUser.setPrenom(prenom);
        selectedUser.setEmail(email);
        selectedUser.setProfession(profession);

        if ("ETUDIANT".equals(profession)) {
            Filiere filiere = comboFiliere.getValue();
            String niveau = comboNiveau.getValue();
            
            if (filiere == null || niveau == null) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs requis", 
                    "Pour un étudiant, la filière et le niveau sont obligatoires.");
                return;
            }
            
            selectedUser.setFiliere_id(filiere.getId());
            selectedUser.setNiveau(niveau);
        } else {
            selectedUser.setFiliere_id(null);
            selectedUser.setNiveau(null);
        }

        // ✅ Gestion de l'exception SQL
        try {
            if (userDAO.updateUser(selectedUser)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour.");
                loadUsersData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour a échoué.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la modification : " + e.getMessage());
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur Base de données", 
                "Impossible de modifier l'utilisateur.\n" + e.getMessage());
        }
    }

    private void gererSuppression() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
            "Voulez-vous vraiment supprimer " + selectedUser.getPrenom() + " " + selectedUser.getNom() + " ?", 
            ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // ✅ Gestion de l'exception SQL
                try {
                    if (userDAO.deleteUser(selectedUser.getId())) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé.");
                        loadUsersData();
                        clearForm();
                    } else {
                        afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La suppression a échoué.");
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
                    e.printStackTrace();
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur Base de données", 
                        "Impossible de supprimer l'utilisateur.\n" + e.getMessage());
                }
            }
        });
    }

    private void clearForm() {
        txtCodePermanent.clear();
        txtCodePermanent.setDisable(false);
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        comboProfession.setValue(null);
        comboFiliere.setValue(null);
        comboNiveau.setValue(null);
        comboFiliere.setVisible(false);
        comboFiliere.setManaged(false);
        comboNiveau.setVisible(false);
        comboNiveau.setManaged(false);
        tableUsers.getSelectionModel().clearSelection();
    }

    private void retourAuTableauBord() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDashboardController dashboardController = new AdminDashboardController();
            Scene scene = new Scene(dashboardController, 1400, 700);
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir au tableau de bord.");
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}