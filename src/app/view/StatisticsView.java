package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatisticsView extends BorderPane {

    // Boutons - Initialisés dans le constructeur
    private Button btnRetour;      // ✅ Plus final
    private Button btnRafraichir;  // ✅ Plus final
    
    // Graphiques
    private final PieChart participationChart;
    private final BarChart<String, Number> resultatsChart;
    private final LineChart<String, Number> tendanceChart;
    private final PieChart repartitionElecteursChart;
    
    // Indicateurs (statistiques rapides)
    private final Label lblTotalVotes;
    private final Label lblTotalElecteurs;
    private final Label lblTauxParticipation;
    private final Label lblEnLigne;
    private final Label lblTotalElections;
    private final Label lblElectionsOuvertes;
    
    // Conteneur principal avec scroll
    private final ScrollPane scrollPane;

    public StatisticsView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f4f8;");

        // Initialisation des labels
        lblTotalVotes = new Label("0");
        lblTotalElecteurs = new Label("0");
        lblTauxParticipation = new Label("0%");
        lblEnLigne = new Label("0");
        lblTotalElections = new Label("0");
        lblElectionsOuvertes = new Label("0");

        // Initialisation des graphiques
        participationChart = createPieChart();
        resultatsChart = createBarChart();
        tendanceChart = createLineChart();
        repartitionElecteursChart = createPieChart();

        // ==========================================
        // 1. ENTÊTE
        // ==========================================
        HBox header = createHeader();
        this.setTop(header);

        // ==========================================
        // 2. INDICATEURS RAPIDES (KPI)
        // ==========================================
        GridPane kpiGrid = createKPIGrid();
        
        // ==========================================
        // 3. GRAPHIQUES
        // ==========================================
        GridPane chartsGrid = createChartsGrid();

        // ==========================================
        // 4. ASSEMBLAGE
        // ==========================================
        VBox content = new VBox(20);
        content.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().addAll(kpiGrid, new Separator(), chartsGrid);

        // ScrollPane pour rendre tout scrollable
        scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(Insets.EMPTY);
        
        this.setCenter(scrollPane);
    }

    // ==========================================
    // MÉTHODES DE CONSTRUCTION
    // ==========================================

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        // Bouton Retour - Initialisé ici
        btnRetour = createStyledButton("← Retour", "#6c757d", "#5a6268");
        btnRetour.setPrefWidth(120);

        // Bouton Rafraîchir - Initialisé ici
        btnRafraichir = createStyledButton("🔄 Rafraîchir", "#17a2b8", "#138496");
        btnRafraichir.setPrefWidth(130);

        // Titre
        Label title = new Label("Tableau de Bord Statistique");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1a3a5c;");

        // Espaceur pour pousser les boutons à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(btnRetour, title, spacer, btnRafraichir);
        return header;
    }

    private Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button btn = new Button(text);
        String baseStyle = "-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 15;";
        String hoverStyle = "-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 15;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setCursor(javafx.scene.Cursor.HAND);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            btn.setCursor(javafx.scene.Cursor.DEFAULT);
        });
        return btn;
    }

    private GridPane createKPIGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 10, 0));
        grid.setAlignment(Pos.CENTER);

        // Style commun pour les KPI
        String kpiStyle = "-fx-background-color: white; -fx-background-radius: 12; " +
                         "-fx-padding: 15 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 3);";

        // Création des KPI
        VBox kpi1 = createKPI("🗳️ Total Votes", lblTotalVotes, "#4CAF50");
        kpi1.setStyle(kpiStyle);
        
        VBox kpi2 = createKPI("👥 Électeurs", lblTotalElecteurs, "#2196F3");
        kpi2.setStyle(kpiStyle);
        
        VBox kpi3 = createKPI("📈 Participation", lblTauxParticipation, "#FF9800");
        kpi3.setStyle(kpiStyle);
        
        VBox kpi4 = createKPI("🟢 En Ligne", lblEnLigne, "#4CAF50");
        kpi4.setStyle(kpiStyle);
        
        VBox kpi5 = createKPI("📋 Élections", lblTotalElections, "#9C27B0");
        kpi5.setStyle(kpiStyle);
        
        VBox kpi6 = createKPI("🔓 Ouvertes", lblElectionsOuvertes, "#E91E63");
        kpi6.setStyle(kpiStyle);

        // Ajout à la grille
        grid.add(kpi1, 0, 0);
        grid.add(kpi2, 1, 0);
        grid.add(kpi3, 2, 0);
        grid.add(kpi4, 3, 0);
        grid.add(kpi5, 4, 0);
        grid.add(kpi6, 5, 0);

        // Pour rendre la grille responsive
        for (int i = 0; i < 6; i++) {
            javafx.scene.layout.ColumnConstraints col = new javafx.scene.layout.ColumnConstraints();
            col.setPercentWidth(100.0 / 6);
            grid.getColumnConstraints().add(col);
        }

        return grid;
    }

    private VBox createKPI(String title, Label valueLabel, String color) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(140);
        vbox.setPrefWidth(160);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        titleLabel.setStyle("-fx-text-fill: #666;");

        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        vbox.getChildren().addAll(titleLabel, valueLabel);
        return vbox;
    }

    private GridPane createChartsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        String cardStyle = "-fx-background-color: white; -fx-background-radius: 15; " +
                          "-fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 5);";

        // 1. PieChart - Participation Global
        VBox cardPie = createCard(
            "📊 Taux de Participation Global",
            participationChart,
            cardStyle
        );

        // 2. BarChart - Résultats des Candidats
        VBox cardBar = createCard(
            "🏆 Résultats par Candidat",
            resultatsChart,
            cardStyle
        );

        // 3. LineChart - Tendance des Votes
        VBox cardLine = createCard(
            "📈 Tendance des Votes (7 jours)",
            tendanceChart,
            cardStyle
        );

        // 4. PieChart - Répartition des Électeurs
        VBox cardPieElecteurs = createCard(
            "👥 Répartition des Électeurs",
            repartitionElecteursChart,
            cardStyle
        );

        // Ajout à la grille
        grid.add(cardPie, 0, 0);
        grid.add(cardBar, 1, 0);
        grid.add(cardLine, 0, 1);
        grid.add(cardPieElecteurs, 1, 1);

        // Pour rendre la grille responsive
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints();
        col1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createCard(String title, javafx.scene.Node chart, String style) {
        VBox card = new VBox(10);
        card.setStyle(style);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(500);
        card.setMinHeight(350);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #1a3a5c;");
        
        card.getChildren().addAll(titleLabel, chart);
        return card;
    }

    // ==========================================
    // CRÉATION DES GRAPHIQUES
    // ==========================================

    private PieChart createPieChart() {
        PieChart chart = new PieChart();
        chart.setPrefSize(400, 320);
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setStyle("-fx-font-size: 12px;");
        return chart;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Candidats");
        xAxis.setStyle("-fx-font-size: 12px;");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de Voix");
        yAxis.setStyle("-fx-font-size: 12px;");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setPrefSize(450, 320);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setStyle("-fx-font-size: 12px;");
        
        return chart;
    }

    private LineChart<String, Number> createLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Jours");
        xAxis.setStyle("-fx-font-size: 12px;");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de Votes");
        yAxis.setStyle("-fx-font-size: 12px;");
        
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setPrefSize(450, 320);
        chart.setLegendVisible(true);
        chart.setAnimated(true);
        chart.setStyle("-fx-font-size: 12px;");
        chart.setCreateSymbols(true);
        
        return chart;
    }

    // ==========================================
    // MÉTHODES DE MISE À JOUR POUR LE CONTROLLER
    // ==========================================

    /**
     * Met à jour les KPI (indicateurs rapides)
     */
    public void updateKPIs(int totalVotes, int totalElecteurs, double tauxParticipation, 
                           int enLigne, int totalElections, int electionsOuvertes) {
        lblTotalVotes.setText(String.format("%,d", totalVotes));
        lblTotalElecteurs.setText(String.format("%,d", totalElecteurs));
        lblTauxParticipation.setText(String.format("%.1f%%", tauxParticipation));
        lblEnLigne.setText(String.format("%,d", enLigne));
        lblTotalElections.setText(String.format("%,d", totalElections));
        lblElectionsOuvertes.setText(String.format("%,d", electionsOuvertes));
    }

    /**
     * Met à jour le PieChart de participation
     */
    public void updateParticipationChart(PieChart.Data... data) {
        participationChart.getData().clear();
        for (PieChart.Data d : data) {
            participationChart.getData().add(d);
        }
    }

    /**
     * Met à jour le BarChart des résultats
     */
    public void updateResultatsChart(XYChart.Series<String, Number> series) {
        resultatsChart.getData().clear();
        resultatsChart.getData().add(series);
    }

    /**
     * Met à jour le LineChart de tendance
     */
    public void updateTendanceChart(XYChart.Series<String, Number> series) {
        tendanceChart.getData().clear();
        tendanceChart.getData().add(series);
    }

    /**
     * Met à jour le PieChart de répartition des électeurs
     */
    public void updateRepartitionElecteursChart(PieChart.Data... data) {
        repartitionElecteursChart.getData().clear();
        for (PieChart.Data d : data) {
            repartitionElecteursChart.getData().add(d);
        }
    }

    /**
     * Version simplifiée pour le PieChart avec une liste
     */
    public void updateParticipationChart(java.util.List<PieChart.Data> data) {
        participationChart.getData().clear();
        participationChart.getData().addAll(data);
    }

    public void updateRepartitionElecteursChart(java.util.List<PieChart.Data> data) {
        repartitionElecteursChart.getData().clear();
        repartitionElecteursChart.getData().addAll(data);
    }

    // ==========================================
    // GETTERS POUR LE CONTROLLER
    // ==========================================

    public Button getBtnRetour() {
        return btnRetour;
    }

    public Button getBtnRafraichir() {
        return btnRafraichir;
    }

    public PieChart getParticipationChart() {
        return participationChart;
    }

    public BarChart<String, Number> getResultatsChart() {
        return resultatsChart;
    }

    public LineChart<String, Number> getTendanceChart() {
        return tendanceChart;
    }

    public PieChart getRepartitionElecteursChart() {
        return repartitionElecteursChart;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}