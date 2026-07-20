package app.view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class AdminDashboardView extends BorderPane {

    private final Button btnGestionElecteurs;
    private final Button btnGestionAdministrateurs;
    private final Button btnStatistiques;
    private final Button btnRetourConnexion;
    
    private Label lblTotalEtudiants;
    private Label lblTotalEnseignants;
    private Label lblTotalAdmins;
    private Label lblVotants;
    private Label lblNonVotants;
    private Label lblEnLigne;
    private Label lblHorsLigne;
    private Label lblTotal;

    public AdminDashboardView() {
        this.setPadding(new Insets(25));
        this.setStyle("-fx-background-color: #daecfe;");

        VBox content = new VBox(20);
        content.setMaxWidth(1400);
        content.setAlignment(Pos.TOP_CENTER);

        // EN-TÊTE
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, rgb(210, 67, 67), #c74a2b); " +
            "-fx-background-radius: 16; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(26, 71, 126, 0.3), 15, 0, 0, 4);"
        );
        
        Label title = new Label("Tableau de bord administrateur");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Bienvenue dans votre espace de gestion. Gérez les utilisateurs, suivez les statistiques et administrez la plateforme en toute simplicité.");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setStyle("-fx-text-fill: #f3f4f4;");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);

        // STATISTIQUES - GRID 4x2
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(10, 0, 10, 0));
        
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            statsGrid.getColumnConstraints().add(col);
        }

        // CRÉATION DES CARTES 
        VBox cardEtudiants = createStatCard("👤", "Étudiants", "0", "#0673aded", "#dddee3");
        VBox cardEnseignants = createStatCard("👤", "Enseignants", "0", "#0673aded", "#dbdce1");
        VBox cardAdmins = createStatCard("👤", "Administrateurs", "0", "#0673aded", "#dfdfe2");
        VBox cardVotants = createStatCard("📨", "Votants", "0", "#0673aded", "#d7d8dc");
        VBox cardNonVotants = createStatCard("📨", "Non Votants", "0", "#0673aded", "#d8d9dd");
        VBox cardEnLigne = createStatCard("🟢", "En Ligne", "0", "#0673aded", "#d7d8da");
        VBox cardHorsLigne = createStatCard("🔴", "Hors Ligne", "0", "#0673aded", "#dbdbde");
        VBox cardTotal = createStatCard("➕", "Total Utilisateurs", "0", "#0673aded", "#d2d2d4");

        // RÉCUPÉRATION DES LABELS (bien positionnés)
        lblTotalEtudiants = (Label) ((HBox) cardEtudiants.getChildren().get(0)).getChildren().get(1);
        lblTotalEnseignants = (Label) ((HBox) cardEnseignants.getChildren().get(0)).getChildren().get(1);
        lblTotalAdmins = (Label) ((HBox) cardAdmins.getChildren().get(0)).getChildren().get(1);
        lblVotants = (Label) ((HBox) cardVotants.getChildren().get(0)).getChildren().get(1);
        lblNonVotants = (Label) ((HBox) cardNonVotants.getChildren().get(0)).getChildren().get(1);
        lblEnLigne = (Label) ((HBox) cardEnLigne.getChildren().get(0)).getChildren().get(1);
        lblHorsLigne = (Label) ((HBox) cardHorsLigne.getChildren().get(0)).getChildren().get(1);
        lblTotal = (Label) ((HBox) cardTotal.getChildren().get(0)).getChildren().get(1);

        // AJOUT DANS LA GRILLE
        statsGrid.add(cardEtudiants, 0, 0);
        statsGrid.add(cardEnseignants, 1, 0);
        statsGrid.add(cardAdmins, 2, 0);
        statsGrid.add(cardVotants, 3, 0);
        statsGrid.add(cardNonVotants, 0, 1);
        statsGrid.add(cardEnLigne, 1, 1);
        statsGrid.add(cardHorsLigne, 2, 1);
        statsGrid.add(cardTotal, 3, 1);
        // SECTION ACTIONS
        HBox actionsHeader = new HBox();
        actionsHeader.setAlignment(Pos.CENTER_LEFT);
        actionsHeader.setPadding(new Insets(20, 0, 10, 0));
        
        Label lblActions = new Label("⚡ Actions rapides");
        lblActions.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblActions.setStyle("-fx-text-fill: #1a237e;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblHint = new Label("Sélectionnez une action pour gérer la plateforme");
        lblHint.setFont(Font.font("System", 13));
        lblHint.setStyle("-fx-text-fill: #64748b;");
        
        actionsHeader.getChildren().addAll(lblActions, spacer, lblHint);

        // Actions Grid
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(20);
        actionsGrid.setVgap(20);
        actionsGrid.setAlignment(Pos.CENTER);
        actionsGrid.setPadding(new Insets(0, 0, 10, 0));
        
        ColumnConstraints actionCol1 = new ColumnConstraints();
        actionCol1.setPercentWidth(50);
        ColumnConstraints actionCol2 = new ColumnConstraints();
        actionCol2.setPercentWidth(50);
        actionsGrid.getColumnConstraints().addAll(actionCol1, actionCol2);

        btnGestionElecteurs = new Button("👤 Gérer les électeurs");
        btnGestionAdministrateurs = new Button("👤 Gérer les administrateurs");
        btnStatistiques = new Button("📊 Voir les statistiques");
        btnRetourConnexion = new Button("↩️ Se déconnecter");

        actionsGrid.add(createActionCard(btnGestionElecteurs, 
            "👤 Gestion des électeurs",
            "Ajouter, modifier, supprimer et rechercher des électeurs",
            "#cc4c2c"), 0, 0);
            
        actionsGrid.add(createActionCard(btnGestionAdministrateurs,
            "👤 Gestion des administrateurs",
            "Ajouter ou supprimer des administrateurs de la plateforme",
            "#cc4c2c"), 1, 0);
            
        actionsGrid.add(createActionCard(btnStatistiques,
            "📊 Statistiques",
            "Consulter les indicateurs et rapports détaillés",
            "#cc4c2c"), 0, 1);
            
        actionsGrid.add(createActionCard(btnRetourConnexion,
            "↩️ Retour à la connexion",
            "Revenir à l'écran d'authentification",
            "#cc4c2c"), 1, 1);

        content.getChildren().addAll(header, statsGrid, actionsHeader, actionsGrid);
        this.setCenter(content);
    }
    // CARTE STATISTIQUE
    
    private VBox createStatCard(String icon, String label, String value, String color, String bgColor) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setPrefWidth(180);
        card.setPrefHeight(85);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 14; " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 14; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 6, 0, 0, 2);"
        );

        //  Ligne 1 
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 18));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        topRow.getChildren().addAll(iconLabel, valueLabel);
        
        //  Ligne 2 
        Label titleLabel = new Label(label);
        titleLabel.setFont(Font.font("System", 13));
        titleLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");
        
        card.getChildren().addAll(topRow, titleLabel);
        return card;
    }

    // CARTE ACTION
    private VBox createActionCard(Button actionButton, String title, String description, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22, 24, 22, 24));
        card.setPrefWidth(360);
        card.setPrefHeight(160);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 4);"
        );
        card.setCursor(javafx.scene.Cursor.HAND);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        titleLabel.setStyle("-fx-text-fill: #111827;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System", 13));
        descLabel.setStyle("-fx-text-fill: #6b7280;");
        descLabel.setMaxWidth(320);

        actionButton.setPrefWidth(200);
        actionButton.setPrefHeight(36);
        actionButton.setTooltip(new Tooltip("Cliquez pour accéder"));
        actionButton.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 16;"
        );

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(180), card);
        scaleIn.setFromX(1.0);
        scaleIn.setFromY(1.0);
        scaleIn.setToX(1.02);
        scaleIn.setToY(1.02);

        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 18, 0, 0, 6); " +
                "-fx-border-color: " + color + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 16;"
            );
            titleLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            scaleIn.playFromStart();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 4);"
            );
            titleLabel.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold;");
        });

        card.setOnMouseClicked(e -> actionButton.fire());

        card.getChildren().addAll(titleLabel, descLabel, actionButton);
        return card;
    }

    // MISE À JOUR DES STATISTIQUES
    
    public void updateStats(int etudiants, int enseignants, int admins, 
                            int votants, int nonVotants, int enLigne, int horsLigne) {
        lblTotalEtudiants.setText(String.valueOf(etudiants));
        lblTotalEnseignants.setText(String.valueOf(enseignants));
        lblTotalAdmins.setText(String.valueOf(admins));
        lblVotants.setText(String.valueOf(votants));
        lblNonVotants.setText(String.valueOf(nonVotants));
        lblEnLigne.setText(String.valueOf(enLigne));
        lblHorsLigne.setText(String.valueOf(horsLigne));
        
        int total = etudiants + enseignants + admins;
        lblTotal.setText(String.valueOf(total));
    }

    // GETTERS
    public Button getBtnGestionUtilisateurs() {
        return btnGestionElecteurs;
    }

    public Button getBtnGestionAdministrateurs() {
        return btnGestionAdministrateurs;
    }

    public Button getBtnStatistiques() {
        return btnStatistiques;
    }

    public Button getBtnRetourConnexion() {
        return btnRetourConnexion;
    }
}