package app.controller;

import java.util.List;

import app.dao.UfrDAO;
import app.model.Ufr;
import app.view.AdminUfrView;
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

public class AdminUfrController extends BorderPane {

    private final AdminUfrView view;

    // Éléments de la Table
    private TableView<Ufr> tableUfr;

    // Éléments du Formulaire de droite
    private TextField txtSearch;
    private TextField txtId; 
    private TextField txtNom;

    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnVider;
    private Button btnRetour;

    // Données et DAO
    private UfrDAO ufrDAO;
    private ObservableList<Ufr> ufrList;

    public AdminUfrController() {
        this.ufrDAO = new UfrDAO();
        this.ufrList = FXCollections.observableArrayList();
        this.view = new AdminUfrView();
        this.setCenter(this.view);

        // Liaison des composants graphiques
        this.tableUfr = this.view.getTableUfr();
        this.txtSearch = this.view.getTxtSearch();
        this.txtId = this.view.getTxtId();
        this.txtNom = this.view.getTxtNom();
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

        loadUfrData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    private void loadUfrData() {
        List<Ufr> list = ufrDAO.getAllUfr();
        ufrList.setAll(list); 
    }

    private void setupSelectionListener() {
        tableUfr.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getId())); 
                txtNom.setText(newSelection.getNom());
            }
        });
    }

    private void setupRealtimeSearch() {
        FilteredList<Ufr> filteredData = new FilteredList<>(ufrList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(ufr -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (ufr.getNom() != null && ufr.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(ufr.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false; 
            });
        });

        SortedList<Ufr> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUfr.comparatorProperty());
        
        tableUfr.setItems(sortedData);
    }

    private void gererAjout() {
        String nom = txtNom.getText().trim();

        if (nom.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez renseigner le nom de l'UFR.");
            return;
        }

        try {
            Ufr u = new Ufr();
            u.setNom(nom);

            if (ufrDAO.addUfr(u)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'UFR a été ajoutée avec succès.");
                loadUfrData(); 
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'UFR. Vérifiez qu'elle n'existe pas déjà.");
            }

        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout.");
        }
    }

    private void gererModification() {
        Ufr selectedUfr = tableUfr.getSelectionModel().getSelectedItem();
        if (selectedUfr == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord une UFR dans la table.");
            return;
        }

        String nom = txtNom.getText().trim();

        if (nom.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Le nom de l'UFR ne peut pas être vide.");
            return;
        }

        selectedUfr.setNom(nom);

        if (ufrDAO.updateUfr(selectedUfr)) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "UFR mise à jour.");
            loadUfrData();
            clearForm();
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
        }
    }

    private void gererSuppression() {
        Ufr selectedUfr = tableUfr.getSelectionModel().getSelectedItem();
        if (selectedUfr == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'UFR à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette UFR ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (ufrDAO.deleteUfr(selectedUfr.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "UFR supprimée.");
                    loadUfrData();
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
        tableUfr.getSelectionModel().clearSelection();
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