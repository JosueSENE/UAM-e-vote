package app.controller;

import app.dao.UserDAO;
import app.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AdminController extends BorderPane {

    // Éléments de la Table
    private TableView<User> tableUsers;
    private TableColumn<User, Integer> colCodePermanent; // Modifié : colMatricule -> colCodePermanent
    private TableColumn<User, String> colNom;
    private TableColumn<User, String> colPrenom;
    private TableColumn<User, String> colEmail;
    private TableColumn<User, String> colProfession; 

    // Éléments du Formulaire de droite
    private TextField txtSearch;
    private TextField txtCodePermanent; // Modifié : txtMatricule -> txtCodePermanent
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtEmail;
    private ComboBox<String> comboProfession; 

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;

    // Données et DAO
    private UserDAO userDAO;
    private ObservableList<User> userList;

    public AdminController() {
        this.userDAO = new UserDAO();
        // 1. Initialiser la liste unique globale dès le départ
        this.userList = FXCollections.observableArrayList();
        
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa;");

        initLeftTableSection();
        initRightFormSection();
        
        // 2. Charger les données (remplit la liste)
        loadUsersData();
        setupSelectionListener();
        
        // 3. Configurer et lier la recherche (gère le setItems final)
        setupRealtimeSearch();
    }

    /**
     * Partie Gauche : Titre, Recherche en temps réel et Table des utilisateurs
     */
    private void initLeftTableSection() {
        VBox leftBox = new VBox(15);
        leftBox.setPadding(new Insets(10));
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        // Barre supérieure (Titre à gauche, Recherche à droite)
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

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

        headerBox.getChildren().addAll(lblTitle, spacer, searchBox);

        // Configuration de la TableView
        tableUsers = new TableView<>();
        tableUsers.setPlaceholder(new Label("Aucun contenu dans la table"));
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableUsers.setPrefHeight(550);

        // Association des colonnes avec les attributs du modèle User
        colCodePermanent = new TableColumn<>("Code Permanent");
        colCodePermanent.setCellValueFactory(new PropertyValueFactory<>("codePermanent")); // Lié à codePermanent de l'objet User

        colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colProfession = new TableColumn<>("Profession");
        colProfession.setCellValueFactory(new PropertyValueFactory<>("profession"));

        tableUsers.getColumns().addAll(colCodePermanent, colNom, colPrenom, colEmail, colProfession);

        leftBox.getChildren().addAll(headerBox, tableUsers);
        this.setCenter(leftBox);
    }

    /**
     * Partie Droite : Formulaire d'Informations Utilisateur et Boutons d'Action
     */
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

        // Champs de saisie du Code Permanent
        txtCodePermanent = new TextField();
        txtCodePermanent.setPromptText("Ex: 501699");

        // Forcer la saisie de chiffres uniquement et limiter à 6 caractères
        txtCodePermanent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCodePermanent.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtCodePermanent.getText().length() > 6) {
                String s = txtCodePermanent.getText().substring(0, 6);
                txtCodePermanent.setText(s);
            }
        });

        VBox fieldCodePermanent = createFormField("Code Permanent (Sera haché si création) :", txtCodePermanent);

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

        // Grille des boutons d'actions
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

        // Disposition des boutons dans la grille
        gridButtons.add(btnAjouter, 0, 0);
        gridButtons.add(btnModifier, 1, 0);
        gridButtons.add(btnSupprimer, 0, 1);
        gridButtons.add(btnVider, 1, 1);

        // Forcer les colonnes de la grille à prendre 50% de largeur chacune
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridButtons.getColumnConstraints().addAll(col1, col2);

        // Actions des boutons
        btnAjouter.setOnAction(e -> gererAjout());
        btnModifier.setOnAction(e -> gererModification());
        btnSupprimer.setOnAction(e -> gererSuppression());
        btnVider.setOnAction(e -> clearForm());

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

    /**
     * Met à jour le CONTENU de la liste existante sans casser les liaisons du filtre
     */
    private void loadUsersData() {
        List<User> list = userDAO.getAllUsers();
        userList.setAll(list); 
    }

    /**
     * Écouteur de sélection : remplit le formulaire lorsqu'on clique sur une ligne de la table
     */
    private void setupSelectionListener() {
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtCodePermanent.setText(String.valueOf(newSelection.getCodePermanent()));
                txtCodePermanent.setDisable(true); // Empêche de modifier la clé primaire
                txtNom.setText(newSelection.getNom());
                txtPrenom.setText(newSelection.getPrenom());
                txtEmail.setText(newSelection.getEmail());
                comboProfession.setValue(newSelection.getProfession()); 
            }
        });
    }

    /**
     * Recherche dynamique en temps réel robuste et sécurisée
     */
    private void setupRealtimeSearch() {
        FilteredList<User> filteredData = new FilteredList<>(userList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                // Sécurisation contre les valeurs 'null' en base de données
                if (user.getNom() != null && user.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(user.getCodePermanent()).contains(lowerCaseFilter)) {
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

    /**
     * Logique d'ajout d'un utilisateur
     */
    private void gererAjout() {
        String codePermanentStr = txtCodePermanent.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String profession = comboProfession.getValue(); 

        // 1. Vérification des champs vides
        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        // 2. Contrôle strict de la longueur (exactement 6 chiffres)
        if (codePermanentStr.length() != 6) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Le Code Permanent doit être composé de 6 chiffres exactement (ex: 501699).");
            return;
        }

        try {
            int codePermanent = Integer.parseInt(codePermanentStr);

            User u = new User();
            u.setCodePermanent(codePermanent);
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setEmail(email);
            u.setProfession(profession); 

            if (userDAO.addUser(u)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'utilisateur a été ajouté avec succès.");
                loadUsersData(); 
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur. Vérifiez que le Code Permanent n'est pas déjà enregistré.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "Le Code Permanent doit obligatoirement être un nombre.");
        }
    }

    /**
     * Logique de modification
     */
    private void gererModification() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord un utilisateur dans la table.");
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

        if (userDAO.modifierUser(selectedUser)) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour.");
            loadUsersData();
            clearForm();
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
        }
    }

    /**
     * Logique de suppression
     */
    private void gererSuppression() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (userDAO.supprimerUser(selectedUser.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé.");
                    loadUsersData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La suppression a échoué.");
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
        tableUsers.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}