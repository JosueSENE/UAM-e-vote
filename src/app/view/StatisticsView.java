package app.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StatisticsView extends BorderPane {

    // ==========================================
    // CONSTANTES
    // ==========================================
    private static final String[] KPI_COLORS = {
        "#4CAF50", "#2196F3", "#FF9800", 
        "#4CAF50", "#9C27B0", "#E91E63"
    };
    
    private static final String[] KPI_ICONS = {
        "🗳️", "👥", "📈", "🟢", "📋", "🔓"
    };
    
    private static final String[] KPI_LABELS = {
        "Total Votes", "Électeurs", "Participation", 
        "En Ligne", "Élections", "Ouvertes"
    };
    
    private static final String[] CHART_COLORS = {
        "#4CAF50", "#2196F3", "#FF9800", "#9C27B0", 
        "#E91E63", "#00BCD4", "#FF5722", "#8BC34A"
    };

    // ==========================================
    // COMPOSANTS
    // ==========================================
    private Button btnRetour;
    private Button btnRafraichir;
    private Button btnExporterPDF;
    private Label lblLastUpdate;
    private ProgressIndicator progressIndicator;
    
    private final PieChart participationChart;
    private final BarChart<String, Number> resultatsChart;
    private final LineChart<String, Number> tendanceChart;
    private final PieChart repartitionElecteursChart;
    
    private final Label[] kpiValues;
    private final VBox[] kpiCards;
    
    private final ScrollPane scrollPane;
    
    // Timer pour le rafraîchissement automatique
    private Timeline refreshTimer;
    private ComboBox<Integer> comboIntervalle;

    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

    public StatisticsView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f4f8;");

        // Initialisation des KPI
        this.kpiValues = new Label[6];
        this.kpiCards = new VBox[6];
        for (int i = 0; i < 6; i++) {
            kpiValues[i] = new Label("0");
            kpiValues[i].setStyle("-fx-text-fill: " + KPI_COLORS[i] + ";");
            kpiValues[i].setFont(Font.font("System", FontWeight.BOLD, 28));
            
            // Ajout du click pour voir les détails
            final int index = i;
            kpiValues[i].setOnMouseClicked(e -> afficherDetailsKPI(index));
            kpiValues[i].setCursor(javafx.scene.Cursor.HAND);
            Tooltip.install(kpiValues[i], new Tooltip("Cliquez pour voir les détails"));
        }

        // Initialisation des graphiques
        participationChart = createPieChart();
        resultatsChart = createBarChart();
        tendanceChart = createLineChart();
        repartitionElecteursChart = createPieChart();

        // ==========================================
        // CONSTRUCTION DE L'INTERFACE
        // ==========================================
        
        // 1. ENTÊTE
        HBox header = createHeader();
        this.setTop(header);

        // 2. INDICATEURS RAPIDES (KPI)
        GridPane kpiGrid = createKPIGrid();
        
        // 3. GRAPHIQUES
        GridPane chartsGrid = createChartsGrid();

        // 4. PROGRESS INDICATOR (pour le chargement)
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(50, 50);
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setMinHeight(100);

        // 5. ASSEMBLAGE
        VBox content = new VBox(20);
        content.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().addAll(kpiGrid, new Separator(), chartsGrid, loadingPane);

        scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(Insets.EMPTY);
        
        this.setCenter(scrollPane);

        // 6. DÉMARRAGE DU RAFRAÎCHISSEMENT AUTOMATIQUE
        startAutoRefresh();
    }

    // ==========================================
    // MÉTHODES DE CRÉATION
    // ==========================================

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        // Bouton Retour
        btnRetour = createStyledButton("← Retour", "#6c757d", "#5a6268");
        btnRetour.setPrefWidth(120);

        // Bouton Rafraîchir
        btnRafraichir = createStyledButton("🔄 Rafraîchir", "#17a2b8", "#138496");
        btnRafraichir.setPrefWidth(130);

        // Bouton Exporter PDF
        btnExporterPDF = createStyledButton("📄 Exporter PDF", "#28a745", "#218838");
        btnExporterPDF.setPrefWidth(140);
        Tooltip.install(btnExporterPDF, new Tooltip("Exporter les statistiques en PDF"));

        // Titre
        Label title = new Label("Tableau de Bord Statistique");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1a3a5c;");

        // Espaceur
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Intervalle de rafraîchissement
        Label lblIntervalle = new Label("⏱️ Rafr. :");
        lblIntervalle.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        comboIntervalle = new ComboBox<>();
        comboIntervalle.getItems().addAll(30, 60, 120, 300, 600);
        comboIntervalle.setValue(60);
        comboIntervalle.setPrefWidth(70);
        comboIntervalle.setStyle("-fx-background-radius: 5;");
        comboIntervalle.setOnAction(e -> {
            Integer seconds = comboIntervalle.getValue();
            if (seconds != null) {
                updateRefreshInterval(seconds);
            }
        });

        // Label de dernière mise à jour
        lblLastUpdate = new Label("Dernière mise à jour : " + getCurrentTime());
        lblLastUpdate.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        lblLastUpdate.setId("lastUpdate");

        HBox refreshControls = new HBox(5, lblIntervalle, comboIntervalle);
        refreshControls.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(
            btnRetour, 
            title, 
            spacer, 
            refreshControls,
            lblLastUpdate,
            btnRafraichir,
            btnExporterPDF
        );
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

        String kpiStyle = "-fx-background-color: white; -fx-background-radius: 12; " +
                         "-fx-padding: 15 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 3); " +
                         "-fx-cursor: hand;";

        for (int i = 0; i < 6; i++) {
            VBox kpi = createKPI(KPI_ICONS[i] + " " + KPI_LABELS[i], kpiValues[i], KPI_COLORS[i]);
            kpi.setStyle(kpiStyle);
            kpiCards[i] = kpi;
            
            // Tooltip sur la VBox
            final int index = i;
            Tooltip.install(kpi, new Tooltip("Cliquez pour voir les détails de " + KPI_LABELS[i]));
            kpi.setOnMouseClicked(e -> afficherDetailsKPI(index));
            
            grid.add(kpi, i, 0);
            
            // Colonne responsive
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 6);
            col.setMinWidth(Region.USE_COMPUTED_SIZE);
            grid.getColumnConstraints().add(col);
        }

        return grid;
    }

    private VBox createKPI(String title, Label valueLabel, String color) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(Region.USE_COMPUTED_SIZE);
        vbox.setPrefWidth(Region.USE_COMPUTED_SIZE);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        titleLabel.setStyle("-fx-text-fill: #666;");

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

        VBox cardPie = createCard("📊 Taux de Participation", participationChart, cardStyle);
        VBox cardBar = createCard("🏆 Résultats par Candidat", resultatsChart, cardStyle);
        VBox cardLine = createCard("📈 Tendance des Votes (7 jours)", tendanceChart, cardStyle);
        VBox cardPieElecteurs = createCard("👥 Répartition des Électeurs", repartitionElecteursChart, cardStyle);

        grid.add(cardPie, 0, 0);
        grid.add(cardBar, 1, 0);
        grid.add(cardLine, 0, 1);
        grid.add(cardPieElecteurs, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setMinWidth(Region.USE_COMPUTED_SIZE);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setMinWidth(Region.USE_COMPUTED_SIZE);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private VBox createCard(String title, javafx.scene.Node chart, String style) {
        VBox card = new VBox(10);
        card.setStyle(style);
        card.setAlignment(Pos.CENTER);
        card.setMinHeight(350);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(chart, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #1a3a5c;");
        
        // Rendre les graphiques responsives
        if (chart instanceof Chart) {
            Chart ch = (Chart) chart;
            ch.prefWidthProperty().bind(card.widthProperty().multiply(0.9));
            ch.prefHeightProperty().bind(card.heightProperty().multiply(0.85));
        }
        
        card.getChildren().addAll(titleLabel, chart);
        return card;
    }

    // ==========================================
    // CRÉATION DES GRAPHIQUES
    // ==========================================

    private PieChart createPieChart() {
        PieChart chart = new PieChart();
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setStyle("-fx-font-size: 12px;");
        chart.setAnimated(true);
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
        chart.setLegendVisible(true);
        chart.setAnimated(true);
        chart.setStyle("-fx-font-size: 12px;");
        chart.setCreateSymbols(true);
        return chart;
    }

    // ==========================================
    // MÉTHODES DE MISE À JOUR (APPELÉES PAR LE CONTROLLER)
    // ==========================================

    /**
     * Met à jour les KPI (indicateurs rapides)
     */
    public void updateKPIs(int totalVotes, int totalElecteurs, double tauxParticipation, 
                           int enLigne, int totalElections, int electionsOuvertes) {
        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
        
        kpiValues[0].setText(formatLargeNumber(totalVotes));
        kpiValues[1].setText(formatLargeNumber(totalElecteurs));
        kpiValues[2].setText(String.format(Locale.FRANCE, "%.1f%%", tauxParticipation));
        kpiValues[3].setText(formatLargeNumber(enLigne));
        kpiValues[4].setText(formatLargeNumber(totalElections));
        kpiValues[5].setText(formatLargeNumber(electionsOuvertes));
        
        updateLastUpdateTime();
    }

    /**
     * Met à jour le PieChart de participation (version varargs)
     */
    public void updateParticipationChart(PieChart.Data... data) {
        participationChart.getData().clear();
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                PieChart.Data d = data[i];
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-pie-color: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
                }
                participationChart.getData().add(d);
            }
            ajouterTooltipsPieChart(participationChart);
        }
    }

    /**
     * Met à jour le PieChart de participation (version List)
     */
    public void updateParticipationChart(List<PieChart.Data> data) {
        participationChart.getData().clear();
        if (data != null && !data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                PieChart.Data d = data.get(i);
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-pie-color: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
                }
                participationChart.getData().add(d);
            }
            ajouterTooltipsPieChart(participationChart);
        }
    }

    /**
     * Met à jour le BarChart des résultats
     */
    public void updateResultatsChart(XYChart.Series<String, Number> series) {
        resultatsChart.getData().clear();
        if (series != null && !series.getData().isEmpty()) {
            resultatsChart.getData().add(series);
            ajouterTooltipsBarChart(resultatsChart);
        }
    }

    /**
     * Met à jour le LineChart de tendance
     */
    public void updateTendanceChart(XYChart.Series<String, Number> series) {
        tendanceChart.getData().clear();
        if (series != null && !series.getData().isEmpty()) {
            tendanceChart.getData().add(series);
            ajouterTooltipsLineChart(tendanceChart);
        }
    }

    /**
     * Met à jour le PieChart de répartition (version varargs)
     */
    public void updateRepartitionElecteursChart(PieChart.Data... data) {
        repartitionElecteursChart.getData().clear();
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                PieChart.Data d = data[i];
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-pie-color: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
                }
                repartitionElecteursChart.getData().add(d);
            }
            ajouterTooltipsPieChart(repartitionElecteursChart);
        }
    }

    /**
     * Met à jour le PieChart de répartition (version List)
     */
    public void updateRepartitionElecteursChart(List<PieChart.Data> data) {
        repartitionElecteursChart.getData().clear();
        if (data != null && !data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                PieChart.Data d = data.get(i);
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-pie-color: " + CHART_COLORS[i % CHART_COLORS.length] + ";");
                }
                repartitionElecteursChart.getData().add(d);
            }
            ajouterTooltipsPieChart(repartitionElecteursChart);
        }
    }

    /**
     * Affiche ou cache l'indicateur de chargement
     */
    public void showLoading(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(show);
        }
        if (btnRafraichir != null) {
            btnRafraichir.setDisable(show);
        }
        if (btnExporterPDF != null) {
            btnExporterPDF.setDisable(show);
        }
        if (comboIntervalle != null) {
            comboIntervalle.setDisable(show);
        }
    }

    private void updateLastUpdateTime() {
        if (lblLastUpdate != null) {
            lblLastUpdate.setText("Dernière mise à jour : " + getCurrentTime());
        }
    }

    // ==========================================
    // TOOLTIPS SUR LES GRAPHIQUES
    // ==========================================

    private void ajouterTooltipsPieChart(PieChart chart) {
        if (chart == null) return;
        for (PieChart.Data data : chart.getData()) {
            if (data.getNode() != null) {
                Tooltip tooltip = new Tooltip(
                    data.getName() + "\n" +
                    "Valeur : " + formatLargeNumber((int) data.getPieValue()) + "\n" +
                    "Pourcentage : " + String.format("%.1f%%", getPercentage(chart, data))
                );
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }

    private void ajouterTooltipsBarChart(BarChart<String, Number> chart) {
        if (chart == null) return;
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(
                        data.getXValue() + "\n" +
                        "Voix : " + formatLargeNumber(data.getYValue().intValue())
                    );
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
        }
    }

    private void ajouterTooltipsLineChart(LineChart<String, Number> chart) {
        if (chart == null) return;
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(
                        data.getXValue() + "\n" +
                        "Votes : " + formatLargeNumber(data.getYValue().intValue())
                    );
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
        }
    }

    private double getPercentage(PieChart chart, PieChart.Data data) {
        double total = 0;
        for (PieChart.Data d : chart.getData()) {
            total += d.getPieValue();
        }
        return total > 0 ? (data.getPieValue() / total) * 100 : 0;
    }

    // ==========================================
    // RAFRAÎCHISSEMENT AUTOMATIQUE
    // ==========================================

    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        
        int interval = comboIntervalle.getValue() != null ? comboIntervalle.getValue() : 60;
        
        refreshTimer = new Timeline(new KeyFrame(
            Duration.seconds(interval),
            e -> {
                if (btnRafraichir != null && !btnRafraichir.isDisabled()) {
                    btnRafraichir.fire();
                }
            }
        ));
        refreshTimer.setCycleCount(Animation.INDEFINITE);
        refreshTimer.play();
    }

    private void updateRefreshInterval(int seconds) {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        
        refreshTimer = new Timeline(new KeyFrame(
            Duration.seconds(seconds),
            e -> {
                if (btnRafraichir != null && !btnRafraichir.isDisabled()) {
                    btnRafraichir.fire();
                }
            }
        ));
        refreshTimer.setCycleCount(Animation.INDEFINITE);
        refreshTimer.play();
        
        // Mettre à jour l'affichage
        if (lblLastUpdate != null) {
            lblLastUpdate.setText("Dernière mise à jour : " + getCurrentTime() + " (Intervalle : " + seconds + "s)");
        }
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    // ==========================================
    // AFFICHAGE DES DÉTAILS KPI
    // ==========================================

    private void afficherDetailsKPI(int index) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails - " + KPI_LABELS[index]);
        alert.setHeaderText(KPI_ICONS[index] + " " + KPI_LABELS[index]);
        
        String details = "";
        switch(index) {
            case 0: // Total Votes
                details = "Nombre total de votes enregistrés dans le système.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
            case 1: // Électeurs
                details = "Nombre total d'électeurs inscrits dans le système.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
            case 2: // Participation
                details = "Taux de participation global.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
            case 3: // En Ligne
                details = "Nombre d'utilisateurs actuellement en ligne.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
            case 4: // Élections
                details = "Nombre total d'élections dans le système.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
            case 5: // Ouvertes
                details = "Nombre d'élections actuellement ouvertes.\n\n" +
                         "Valeur actuelle : " + kpiValues[index].getText() + "\n" +
                         "Mise à jour : " + getCurrentTime();
                break;
        }
        
        alert.setContentText(details);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button btn = (Button) dialogPane.lookupButton(buttonType);
            if (btn != null) {
                btn.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        }
        
        alert.showAndWait();
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private String getCurrentTime() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String formatLargeNumber(int number) {
        if (number >= 1_000_000) {
            return String.format(Locale.FRANCE, "%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format(Locale.FRANCE, "%.1fK", number / 1_000.0);
        }
        return String.format(Locale.FRANCE, "%,d", number);
    }

    public void cleanup() {
        stopAutoRefresh();
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

    public Button getBtnExporterPDF() { 
        return btnExporterPDF; 
    }

    public ComboBox<Integer> getComboIntervalle() {
        return comboIntervalle;
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

    public Label[] getKpiValues() {
        return kpiValues;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }
}