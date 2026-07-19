package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatisticsView extends BorderPane {

    private final Button btnRetour;
    private final PieChart participationChart;
    private final BarChart<String, Number> resultatsChart;

    public StatisticsView() {
        this.setPadding(new Insets(25));
        // Conservation de ta couleur de fond thématique
        this.setStyle("-fx-background-color: #cbe2ff;");

        // --- ENTÊTE DE LA PAGE ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        btnRetour = new Button("← Retour");
        btnRetour.setPrefWidth(120);
        btnRetour.setPrefHeight(35);
        String baseStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;";
        String hoverStyle = "-fx-background-color: #5a6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;";
        btnRetour.setStyle(baseStyle);
        btnRetour.setOnMouseEntered(e -> {
            btnRetour.setStyle(hoverStyle);
            btnRetour.setCursor(javafx.scene.Cursor.HAND);
        });
        btnRetour.setOnMouseExited(e -> {
            btnRetour.setStyle(baseStyle);
            btnRetour.setCursor(javafx.scene.Cursor.DEFAULT);
        });

        Label title = new Label("Statistiques des Scrutins en Temps Réel");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #005088;");

        header.getChildren().addAll(btnRetour, title);
        this.setTop(header);

        // --- ZONE DES GRAPHIQUES (Conteneur Grille) ---
        GridPane gridCharts = new GridPane();
        gridCharts.setHgap(20);
        gridCharts.setVgap(20);
        gridCharts.setAlignment(Pos.CENTER);

        // Style commun pour les cartes blanches contenant les graphiques
        String cardStyle = "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; "
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 5);";

        // 1. Configuration du PieChart (Participation)
        VBox cardPie = new VBox(10);
        cardPie.setStyle(cardStyle);
        cardPie.setAlignment(Pos.CENTER);
        
        Label pieTitle = new Label("Taux de Participation Global");
        pieTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        pieTitle.setStyle("-fx-text-fill: #333333;");
        
        participationChart = new PieChart();
        participationChart.setPrefSize(450, 400);
        participationChart.setLabelsVisible(true);
        
        cardPie.getChildren().addAll(pieTitle, participationChart);

        // 2. Configuration du BarChart (Résultats des candidats)
        VBox cardBar = new VBox(10);
        cardBar.setStyle(cardStyle);
        cardBar.setAlignment(Pos.CENTER);
        
        Label barTitle = new Label("Répartition des Suffrages par Candidat");
        barTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        barTitle.setStyle("-fx-text-fill: #333333;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Candidats");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de Voix");
        
        resultatsChart = new BarChart<>(xAxis, yAxis);
        resultatsChart.setPrefSize(550, 400);
        resultatsChart.setLegendVisible(false); // Optionnel : masque la légende si une seule série

        cardBar.getChildren().addAll(barTitle, resultatsChart);

        // Ajout des cartes à la grille (colonne, ligne)
        gridCharts.add(cardPie, 0, 0);
        gridCharts.add(cardBar, 1, 0);

        this.setCenter(gridCharts);
    }

    // Getters pour permettre au Controller d'injecter dynamiquement les données SQL
    public Button getBtnRetour() {
        return btnRetour;
    }

    public PieChart getParticipationChart() {
        return participationChart;
    }

    public BarChart<String, Number> getResultatsChart() {
        return resultatsChart;
    }
}