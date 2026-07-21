package app.controller;

import java.util.List;

import app.dao.DepartementDAO;
import app.model.Departement;
import app.view.AdminDepartementView;

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

public class AdminDepartementController extends BorderPane {
    private final AdminDepartementView view;

    private TableView<Departement> tableDepartement;

    private TextField txtSearch;
    private TextField txtId; 
    private TextField txtNom;
    private TextField txtUfrId;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    private DepartementDAO departementDAO;
    private ObservableList<Departement> departementList;

    public AdminDepartementController() {
        this.departementDAO = new DepartementDAO();
        this.departementList = FXCollections.observableArrayList();
        this.view = new AdminDepartementView();
        this.setCenter(this.view);

        // Liaison des composants
        this.tableDepartement = this.view.getTableDepartement();
        this.txtSearch = this.view.getTxtSearch();
        this.txtId = this.view.getTxtId(); 
        this.txtNom = this.view.getTxtNom();
        this.txtUfrId = this.view.getTxtUfrId();
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

        loadDepartementsData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    private void loadDepartementsData() {
        List<Departement> list = departementDAO.getAllDepartements();
        departementList.setAll(list); 
    }

    private void setupSelectionListener() {
        tableDepartement.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getId())); 
                txtNom.setText(newSelection.getNom());
                txtUfrId.setText(String.valueOf(newSelection.getUfr_id()));
            }
        });
    }

    private void setupRealtimeSearch() {
        FilteredList<Departement> filteredData = new FilteredList<>(departementList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(departement -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (departement.getNom() != null && departement.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(departement.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(departement.getUfr_id()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false; 
            });
        });

        SortedList<Departement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableDepartement.comparatorProperty());
        
        tableDepartement.setItems(sortedData);
    }

    private void gererAjout() {
        String nom = txtNom.getText().trim();
        String strUfrId = txtUfrId.getText().trim();

        if (nom.isEmpty() || strUfrId.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs du formulaire.");
            return;
        }

        try {
            int ufr_id = Integer.parseInt(strUfrId);
            Departement d = new Departement();
            d.setNom(nom);
            d.setUfr_id(ufr_id);

            if (departementDAO.addDepartement(d)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le département a été ajouté avec succès.");
                loadDepartementsData(); 
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le département.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'ID de l'UFR doit être un nombre.");
        }
    }

    private void gererModification() {
        Departement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
        if (selectedDepartement == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord un département dans la table.");
            return;
        }

        String nom = txtNom.getText().trim();
        String strUfrId = txtUfrId.getText().trim();

        if (nom.isEmpty() || strUfrId.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Les informations ne peuvent pas être vides.");
            return;
        }

        try {
            int ufr_id = Integer.parseInt(strUfrId);
            selectedDepartement.setNom(nom);
            selectedDepartement.setUfr_id(ufr_id);

            if (departementDAO.updateDepartement(selectedDepartement)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Département mis à jour.");
                loadDepartementsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
            }
        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'ID de l'UFR doit être un nombre.");
        }
    }

    private void gererSuppression() {
        Departement selectedDepartement = tableDepartement.getSelectionModel().getSelectedItem();
        if (selectedDepartement == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez le département à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce département ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (departementDAO.deleteDepartement(selectedDepartement.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Département supprimé.");
                    loadDepartementsData();
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
        txtUfrId.clear(); 
        tableDepartement.getSelectionModel().clearSelection();
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