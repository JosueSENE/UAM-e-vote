package app.controller;

import app.dao.UserDAO;
import app.model.User;
import app.view.AdminView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.SQLException;
import java.util.List;

public class AdminController {

    private AdminView view;
    private UserDAO userDAO;
    private ObservableList<User> userList;

    public AdminController(AdminView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();

        // Initialisation et liaison
        initListeners();
        loadUsersData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    /**
     * Liaison des actions des boutons de la vue
     */
    private void initListeners() {
        view.getBtnAjouter().setOnAction(e -> gererAjout());
        view.getBtnModifier().setOnAction(e -> gererModification());
        view.getBtnSupprimer().setOnAction(e -> gererSuppression());
        view.getBtnVider().setOnAction(e -> clearForm());
    }

    private void loadUsersData() {
        try {
            List<User> list = userDAO.getAllUsers();
            userList.setAll(list); 
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de base de données", 
                "Impossible de récupérer la liste des utilisateurs : " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        view.getTableUsers().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                view.getTxtCodePermanent().setText(String.valueOf(newSelection.getCode_permanent()));
                view.getTxtCodePermanent().setDisable(true); // Bloque la modification de l'identifiant unique
                view.getTxtNom().setText(newSelection.getNom());
                view.getTxtPrenom().setText(newSelection.getPrenom());
                view.getTxtEmail().setText(newSelection.getEmail());
                view.getComboProfession().setValue(newSelection.getProfession()); 
            }
        });
    }

    private void setupRealtimeSearch() {
        FilteredList<User> filteredData = new FilteredList<>(userList, p -> true);

        view.getTxtSearch().textProperty().addListener((observable, oldValue, newValue) -> {
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
        sortedData.comparatorProperty().bind(view.getTableUsers().comparatorProperty());
        
        view.getTableUsers().setItems(sortedData);
    }

    private void gererAjout() {
        String codePermanentStr = view.getTxtCodePermanent().getText().trim();
        String nom = view.getTxtNom().getText().trim();
        String prenom = view.getTxtPrenom().getText().trim();
        String email = view.getTxtEmail().getText().trim();
        String profession = view.getComboProfession().getValue(); 

        if (codePermanentStr.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        if (codePermanentStr.length() != 6) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Le Code Permanent doit être composé de 6 chiffres exactement (ex: 501699).");
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

            try {
                userDAO.addUser(u); // Appel direct de la méthode void
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'utilisateur a été ajouté avec succès.");
                loadUsersData(); 
                clearForm();
            } catch (Exception ex) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur. Vérifiez les données ou le Code Permanent.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "Le Code Permanent doit obligatoirement être un nombre.");
        }
    }

    private void gererModification() {
        User selectedUser = view.getTableUsers().getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord un utilisateur dans la table.");
            return;
        }

        String nom = view.getTxtNom().getText().trim();
        String prenom = view.getTxtPrenom().getText().trim();
        String email = view.getTxtEmail().getText().trim();
        String profession = view.getComboProfession().getValue(); 

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || profession == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Les informations ne peuvent pas être vides.");
            return;
        }

        selectedUser.setNom(nom);
        selectedUser.setPrenom(prenom);
        selectedUser.setEmail(email);
        selectedUser.setProfession(profession); 

        try {
            userDAO.updateUser(selectedUser); // Appel de la méthode void
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour.");
            loadUsersData();
            clearForm();
        } catch (Exception ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
        }
    }

    private void gererSuppression() {
        User selectedUser = view.getTableUsers().getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    userDAO.deleteUser(selectedUser.getId()); // Appel de la méthode void
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé.");
                    loadUsersData();
                    clearForm();
                } catch (Exception ex) {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    private void clearForm() {
        view.getTxtCodePermanent().clear();
        view.getTxtCodePermanent().setDisable(false);
        view.getTxtNom().clear();
        view.getTxtPrenom().clear();
        view.getTxtEmail().clear();
        view.getComboProfession().setValue(null); 
        view.getTableUsers().getSelectionModel().clearSelection();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}