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
    private ComboBox<String> comboProfession; 

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    // Données et DAO
    private UserDAO userDAO;
    private ObservableList<User> userList;

    public AdminUsersController() {
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        this.view = new AdminUsersView();
        this.setCenter(this.view);

        // Liaison des composants graphiques
        this.tableUsers = this.view.getTableUsers();
        this.txtSearch = this.view.getTxtSearch();
        this.txtCodePermanent = this.view.getTxtCodePermanent();
        this.txtNom = this.view.getTxtNom();
        this.txtPrenom = this.view.getTxtPrenom();
        this.txtEmail = this.view.getTxtEmail();
        this.comboProfession = this.view.getComboProfession();
        this.btnAjouter = this.view.getBtnAjouter();
        this.btnModifier = this.view.getBtnModifier();
        this.btnSupprimer = this.view.getBtnSupprimer();
        this.btnVider = this.view.getBtnVider();
        this.btnRetour = this.view.getBtnRetour();

        // Événements
        this.btnAjouter.setOnAction(e -> gererAjout());
        this.btnModifier.setOnAction(e -> gererModification());
        this.btnSupprimer.setOnAction(e -> gererSuppression());
        this.btnVider.setOnAction(e -> clearForm());
        this.btnRetour.setOnAction(e -> retourAuTableauBord());

        loadUsersData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    private void loadUsersData() {
        List<User> list = userDAO.getAllUsers();
        userList.setAll(list); 
    }

    private void setupSelectionListener() {
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtCodePermanent.setText(String.valueOf(newSelection.getCode_permanent())); 
                view.setCodePermanentEditable(false); // Désactive la modification du code permanent
                txtNom.setText(newSelection.getNom());
                txtPrenom.setText(newSelection.getPrenom());
                txtEmail.setText(newSelection.getEmail());
                comboProfession.setValue(newSelection.getProfession()); 
            }
        });
    }

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

    private void gererAjout() {
        String codePermanentStr = txtCodePermanent.getText().trim();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String profession = comboProfession.getValue(); 

        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        if (codePermanentStr.length() != 6) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Le Code Permanent doit être composé de 6 chiffres exactement (ex: 501699).");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "L'adresse email saisie n'est pas valide.");
            return;
        }

        try {
            int codePermanent = Integer.parseInt(codePermanentStr);

            User u = new User();
            u.setCode_permanent(codePermanent);
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

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "L'adresse email saisie n'est pas valide.");
            return;
        }

        selectedUser.setNom(nom);
        selectedUser.setPrenom(prenom);
        selectedUser.setEmail(email);
        selectedUser.setProfession(profession); 

        if (userDAO.updateUser(selectedUser)) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour.");
            loadUsersData();
            clearForm();
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
        }
    }

    private void gererSuppression() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (userDAO.deleteUser(selectedUser.getId())) {
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
        tableUsers.getSelectionModel().clearSelection();
        view.clearFormFields();
    }

    private void retourAuTableauBord() {
        // Logique pour réafficher le Dashboard de l'admin
        System.out.println("Retour au tableau de bord...");
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}