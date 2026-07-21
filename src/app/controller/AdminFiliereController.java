package app.controller;

import java.util.List;

import app.dao.FiliereDAO;
import app.model.Filiere;
import app.view.AdminFiliereView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminFiliereController extends BorderPane {
    private final AdminFiliereView view;

    private TableView<Filiere> tableFiliere;

    private TextField txtSearch;
    private TextField txtId; 
    private TextField txtNom;
    private TextField txtDepartementId;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    private FiliereDAO filiereDAO;
    private ObservableList<Filiere> filiereList;

    public AdminFiliereController() {
        this.filiereDAO = new FiliereDAO();
        this.filiereList = FXCollections.observableArrayList();
        this.view = new AdminFiliereView();
        this.setCenter(this.view);

        // Liaison des composants
        this.tableFiliere = this.view.getTableFiliere();
        this.txtSearch = this.view.getTxtSearch();
        this.txtId = this.view.getTxtId();
        this.txtNom = this.view.getTxtNom();
        this.txtDepartementId = this.view.getTxtDepartementId();
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

        loadFilieresData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    private void loadFilieresData() {
        List<Filiere> list = filiereDAO.getAllFilieres();
        filiereList.setAll(list); 
    }

    private void setupSelectionListener() {
        tableFiliere.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getId())); 
                txtNom.setText(newSelection.getNom());
                txtDepartementId.setText(String.valueOf(newSelection.getDepartement_id()));
            }
        });
    }

    private void setupRealtimeSearch() {
        FilteredList<Filiere> filteredData = new FilteredList<>(filiereList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(filiere -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (filiere.getNom() != null && filiere.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(filiere.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(filiere.getDepartement_id()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (filiere.getDepartementNom() != null && filiere.getDepartementNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; 
            });
        });

        SortedList<Filiere> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableFiliere.comparatorProperty());
        
        tableFiliere.setItems(sortedData);
    }

    private void gererAjout() {
        String nom = txtNom.getText().trim();
        String strDeptId = txtDepartementId.getText().trim();

        if (nom.isEmpty() || strDeptId.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        try {
            int dept_id = Integer.parseInt(strDeptId);
            Filiere f = new Filiere();
            f.setNom(nom);
            f.setDepartement_id(dept_id);

            if (filiereDAO.addFiliere(f)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "La filière a été ajoutée avec succès.");
                loadFilieresData(); 
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la filière.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'ID du département doit être un nombre.");
        }
    }

    private void gererModification() {
        Filiere selectedFiliere = tableFiliere.getSelectionModel().getSelectedItem();
        if (selectedFiliere == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord une filière dans la table.");
            return;
        }

        String nom = txtNom.getText().trim();
        String strDeptId = txtDepartementId.getText().trim();

        if (nom.isEmpty() || strDeptId.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Les informations ne peuvent pas être vides.");
            return;
        }

        try {
            int dept_id = Integer.parseInt(strDeptId);
            selectedFiliere.setNom(nom);
            selectedFiliere.setDepartement_id(dept_id);

            if (filiereDAO.updateFiliere(selectedFiliere)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Filière mise à jour.");
                loadFilieresData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
            }
        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'ID du département doit être un nombre.");
        }
    }

    private void gererSuppression() {
        Filiere selectedFiliere = tableFiliere.getSelectionModel().getSelectedItem();
        if (selectedFiliere == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez la filière à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette filière ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (filiereDAO.deleteFiliere(selectedFiliere.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Filière supprimée.");
                    loadFilieresData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    private void clearForm() {
        txtId.clear();
        txtNom.clear();
        txtDepartementId.clear(); 
        tableFiliere.getSelectionModel().clearSelection();
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