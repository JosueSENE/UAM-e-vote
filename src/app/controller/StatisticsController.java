package app.controller;

import app.view.StatisticsView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StatisticsController extends BorderPane {

    private final StatisticsView view;

    public StatisticsController() {
        this.view = new StatisticsView();
        this.setCenter(this.view);
        
        // Configuration des boutons et chargement des statistiques
        this.view.getBtnRetour().setOnAction(e -> retourAuTableauBord());
        chargerDonneesStatistiques();
    }

    /**
     * Centralise le chargement et l'affichage des graphiques requis
     */
    private void chargerDonneesStatistiques() {
        recupererParticipationGlobale();
        recupererResultatsCandidats();
    }

    /**
     * Alimente le PieChart de la vue avec le taux de participation
     */
    private void recupererParticipationGlobale() {
        int totalEmargements = 850; 
        int totalAbstentions = 150;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Votants (" + totalEmargements + ")", totalEmargements),
            new PieChart.Data("Abstention (" + totalAbstentions + ")", totalAbstentions)
        );

        view.getParticipationChart().setData(pieData);
    }

    /**
     * Alimente le BarChart de la vue avec les voix par candidat
     */
    private void recupererResultatsCandidats() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Suffrages exprimés");

        // Simulation de données de candidats pour l'UAM
        series.getData().add(new XYChart.Data<>("Liste A (Alioune)", 340));
        series.getData().add(new XYChart.Data<>("Liste B (Mariama)", 290));
        series.getData().add(new XYChart.Data<>("Liste C (Ousmane)", 170));
        series.getData().add(new XYChart.Data<>("Bulletins Blancs", 50));

        // Nettoyage et injection de la série dans le BarChart
        view.getResultatsChart().getData().clear();
        view.getResultatsChart().getData().add(series);
    }

    private void retourAuTableauBord() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDashboardController dashboardController = new AdminDashboardController();
            
            // Fixé à 1400x700 pour correspondre aux autres vues de l'administration
            Scene scene = new Scene(dashboardController, 1400, 700);
            
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers le tableau de bord.");
            e.printStackTrace();
        }
    }
}