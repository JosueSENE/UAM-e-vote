package app.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import app.dao.ElectionDAO;
import app.model.Election;
import app.view.AdminElectionView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminElectionController extends BorderPane {
    private final AdminElectionView view;

    private TableView<Election> tableElections;

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

    private ElectionDAO electionDAO;
    private ObservableList<Election> electionList;

    public AdminElectionController() {
        this.electionDAO = new ElectionDAO();
        this.electionList = FXCollections.observableArrayList();
        this.view = new AdminElectionView();
        this.setCenter(this.view);

        // Liaison des composants
        this.tableElections = this.view.getTableElections();
        this.txtSearch = this.view.getTxtSearch();
        this.txtId = this.view.getTxtId();
        this.txtTitre = this.view.getTxtTitre();
        this.txtType = this.view.getTxtType();
        this.txtDebut = this.view.getTxtDebut();
        this.txtFin = this.view.getTxtFin();

        this.cbTypeCible = this.view.getCbTypeCible();
        this.txtCibleId = this.view.getTxtCibleId();
        this.cbNiveau = this.view.getCbNiveau();
        this.cbProfession = this.view.getCbProfession();

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

        loadElectionsData();
        setupSelectionListener();
        setupRealtimeSearch();
    }

    private void loadElectionsData() {
        List<Election> list = electionDAO.getAllElections();
        electionList.setAll(list);
    }

    private void setupSelectionListener() {
        tableElections.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getId()));
                txtTitre.setText(newSelection.getTitre());
                txtType.setText(newSelection.getTypeElection());

                txtDebut.setValue(newSelection.getDateDebut() != null ? newSelection.getDateDebut().toLocalDate() : null);
                txtFin.setValue(newSelection.getDateFin() != null ? newSelection.getDateFin().toLocalDate() : null);

                if (newSelection.getCible_ufr_id() != null) {
                    cbTypeCible.setValue("UFR");
                    txtCibleId.setText(String.valueOf(newSelection.getCible_ufr_id()));
                } else if (newSelection.getCible_departement_id() != null) {
                    cbTypeCible.setValue("DEPARTEMENT");
                    txtCibleId.setText(String.valueOf(newSelection.getCible_departement_id()));
                } else if (newSelection.getCible_filiere_id() != null) {
                    cbTypeCible.setValue("FILIERE");
                    txtCibleId.setText(String.valueOf(newSelection.getCible_filiere_id()));
                } else {
                    cbTypeCible.setValue("TOUS");
                    txtCibleId.clear();
                }

                cbNiveau.setValue(newSelection.getCible_niveau());
                cbProfession.setValue(newSelection.getCible_profession());
            }
        });
    }

    private void setupRealtimeSearch() {
        FilteredList<Election> filteredData = new FilteredList<>(electionList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(election -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (election.getTitre() != null && election.getTitre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (election.getTypeElection() != null && election.getTypeElection().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (election.getCible_profession() != null && election.getCible_profession().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(election.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Election> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableElections.comparatorProperty());

        tableElections.setItems(sortedData);
    }

    private void gererAjout() {
        String titre = txtTitre.getText().trim();
        String type = txtType.getText().trim();
        LocalDate dDebut = txtDebut.getValue();
        LocalDate dFin = txtFin.getValue();

        if (titre.isEmpty() || type.isEmpty() || dDebut == null || dFin == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir au minimum le Titre, le Type, la Date Début et la Date Fin.");
            return;
        }

        LocalDateTime dtDebut = dDebut.atTime(0, 0);
        LocalDateTime dtFin = dFin.atTime(23, 59);

        if (dtFin.isBefore(dtDebut)) {
            afficherAlerte(Alert.AlertType.ERROR, "Dates invalides", "La date de fin doit être postérieure à la date de début.");
            return;
        }

        try {
            Election el = new Election();
            el.setTitre(titre);
            el.setTypeElection(type);
            el.setDateDebut(dtDebut);
            el.setDateFin(dtFin);

            appliquerCibleAElection(el);

            String niv = cbNiveau.getValue();
            el.setCible_niveau((niv != null && !niv.trim().isEmpty()) ? niv : null);

            String prof = cbProfession.getValue();
            el.setCible_profession((prof != null && !prof.trim().isEmpty()) ? prof : null);

            if (electionDAO.addElection(el)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'élection a été ajoutée avec succès.");
                loadElectionsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'élection.");
            }

        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'identifiant de la cible doit être un nombre valide.");
        }
    }

    private void gererModification() {
        Election selectedElection = tableElections.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord une élection dans la table.");
            return;
        }

        String titre = txtTitre.getText().trim();
        String type = txtType.getText().trim();
        LocalDate dDebut = txtDebut.getValue();
        LocalDate dFin = txtFin.getValue();

        if (titre.isEmpty() || type.isEmpty() || dDebut == null || dFin == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Les informations de base ne peuvent pas être vides.");
            return;
        }

        LocalDateTime dtDebut = dDebut.atTime(0, 0);
        LocalDateTime dtFin = dFin.atTime(23, 59);

        if (dtFin.isBefore(dtDebut)) {
            afficherAlerte(Alert.AlertType.ERROR, "Dates invalides", "La date de fin doit être postérieure à la date de début.");
            return;
        }

        try {
            selectedElection.setTitre(titre);
            selectedElection.setTypeElection(type);
            selectedElection.setDateDebut(dtDebut);
            selectedElection.setDateFin(dtFin);

            appliquerCibleAElection(selectedElection);

            String niv = cbNiveau.getValue();
            selectedElection.setCible_niveau((niv != null && !niv.trim().isEmpty()) ? niv : null);

            String prof = cbProfession.getValue();
            selectedElection.setCible_profession((prof != null && !prof.trim().isEmpty()) ? prof : null);

            if (electionDAO.updateElection(selectedElection)) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Élection mise à jour.");
                loadElectionsData();
                clearForm();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour en base de données a échoué.");
            }
        } catch (NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Format invalide", "L'identifiant de la cible doit être un nombre valide.");
        }
    }

    private void gererSuppression() {
        Election selectedElection = tableElections.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez l'élection à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette élection ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (electionDAO.deleteElection(selectedElection.getId())) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Élection supprimée.");
                    loadElectionsData();
                    clearForm();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    private void appliquerCibleAElection(Election el) throws NumberFormatException {
        el.setCible_ufr_id(null);
        el.setCible_departement_id(null);
        el.setCible_filiere_id(null);

        String typeCible = cbTypeCible.getValue();
        if (typeCible == null || typeCible.equals("TOUS")) {
            return;
        }

        String rawId = txtCibleId.getText().trim();
        if (rawId.isEmpty()) {
            throw new NumberFormatException("ID Cible obligatoire");
        }

        int idCible = Integer.parseInt(rawId);
        switch (typeCible) {
            case "UFR" -> el.setCible_ufr_id(idCible);
            case "DEPARTEMENT" -> el.setCible_departement_id(idCible);
            case "FILIERE" -> el.setCible_filiere_id(idCible);
        }
    }

    private void clearForm() {
        txtId.clear();
        txtTitre.clear();
        txtType.clear();
        txtDebut.setValue(null);
        txtFin.setValue(null);
        cbTypeCible.setValue("TOUS");
        txtCibleId.clear();
        cbNiveau.setValue(null);
        cbProfession.setValue(null);
        tableElections.getSelectionModel().clearSelection();
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