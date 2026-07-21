package app.view;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class AdminDashboardView extends BorderPane {

    // ==========================================
    // MENU LATERAL (SIDEBAR)
    // ==========================================
    
    private VBox sidebar;
    private Button btnToggleMenu;
    private boolean isMenuVisible = true;
    
    // ==========================================
    // BOUTONS DU MENU (9 ACTIONS)
    // ==========================================
    
    private final Button btnGestionElecteurs;
    private final Button btnGestionEnseignantFiliere;
    private final Button btnGestionAdministrateurs;
    private final Button btnGestionCandidat;
    private final Button btnGestionElection;
    private final Button btnGestionUfr;
    private final Button btnGestionDepartement;
    private final Button btnGestionFiliere;
    private final Button btnStatistiques;
    private final Button btnRetourConnexion;
    private final Button btnRafraichir;

    // ==========================================
    // LABELS DE STATISTIQUES
    // ==========================================
    
    private Label lblTotalEtudiants;
    private Label lblTotalEnseignants;
    private Label lblTotalAdmins;
    private Label lblVotants;
    private Label lblNonVotants;
    private Label lblTotal;

    public AdminDashboardView() {
        this.setPadding(new Insets(0));
        this.setStyle("-fx-background-color: #daecfe;");

        // Initialisation des boutons
        btnGestionElecteurs = new Button();
        btnGestionEnseignantFiliere = new Button();
        btnGestionAdministrateurs = new Button();
        btnGestionCandidat = new Button();
        btnGestionElection = new Button();
        btnGestionUfr = new Button();
        btnGestionDepartement = new Button();
        btnGestionFiliere = new Button();
        btnStatistiques = new Button();
        btnRetourConnexion = new Button();
        btnRafraichir = new Button();

        // ==========================================
        // CRÉATION DU MENU LATERAL
        // ==========================================
        
        createSidebar();

        // ==========================================
        // CRÉATION DU CONTENU PRINCIPAL
        // ==========================================
        
        VBox mainContent = createMainContent();

        // ==========================================
        // ASSEMBLAGE PRINCIPAL
        // ==========================================
        
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(sidebar, mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        
        this.setCenter(mainLayout);
    }

    // ==========================================
    // CRÉATION DU MENU LATERAL
    // ==========================================
    
    private void createSidebar() {
        sidebar = new VBox(8);
        sidebar.setPadding(new Insets(15, 12, 15, 12));
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(280);
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1a237e, #0d1442); " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );

        // ==========================================
        // BOUTON TOGGLE MENU
        // ==========================================
        
        btnToggleMenu = new Button("☰ Menu");
        btnToggleMenu.setMaxWidth(Double.MAX_VALUE);
        btnToggleMenu.setAlignment(Pos.CENTER_LEFT);
        btnToggleMenu.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10 15;"
        );
        btnToggleMenu.setOnMouseEntered(e -> btnToggleMenu.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10 15;"
        ));
        btnToggleMenu.setOnMouseExited(e -> btnToggleMenu.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10 15;"
        ));
        btnToggleMenu.setOnAction(e -> toggleMenu());

        // ==========================================
        // LOGO / TITRE
        // ==========================================
        
        VBox logoBox = new VBox(3);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(5, 0, 10, 0));
        
        Label lblLogo = new Label("📊");
        lblLogo.setFont(Font.font("System", 35));
        
        Label lblTitle = new Label("UAM e-Vote");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTitle.setStyle("-fx-text-fill: white;");
        
        Label lblSubtitle = new Label("Administration");
        lblSubtitle.setFont(Font.font("System", 11));
        lblSubtitle.setStyle("-fx-text-fill: #90a4ae;");
        
        logoBox.getChildren().addAll(lblLogo, lblTitle, lblSubtitle);

        // ==========================================
        // SÉPARATEUR
        // ==========================================
        
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        // ==========================================
        // SECTION : GESTION DES UTILISATEURS
        // ==========================================
        
        Label lblSection1 = new Label("👥 GESTION DES UTILISATEURS");
        lblSection1.setStyle("-fx-text-fill: #78909c; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        // Configuration des boutons du menu
        btnGestionElecteurs.setText("👥  Électeurs");
        btnGestionElecteurs.setTooltip(new Tooltip("Gérer les étudiants"));
        styleSidebarButton(btnGestionElecteurs);
        
        btnGestionEnseignantFiliere.setText("👨‍🏫  Enseignants");
        btnGestionEnseignantFiliere.setTooltip(new Tooltip("Gérer les enseignants"));
        styleSidebarButton(btnGestionEnseignantFiliere);
        
        btnGestionAdministrateurs.setText("👤  Administrateurs");
        btnGestionAdministrateurs.setTooltip(new Tooltip("Gérer les administrateurs"));
        styleSidebarButton(btnGestionAdministrateurs);

        // ==========================================
        // SECTION : GESTION DES ÉLECTIONS
        // ==========================================
        
        Label lblSection2 = new Label("📋 GESTION DES ÉLECTIONS");
        lblSection2.setStyle("-fx-text-fill: #78909c; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        btnGestionCandidat.setText("📋  Candidats");
        btnGestionCandidat.setTooltip(new Tooltip("Gérer les candidats"));
        styleSidebarButton(btnGestionCandidat);
        
        btnGestionElection.setText("🏛️  Scrutins");
        btnGestionElection.setTooltip(new Tooltip("Gérer les élections"));
        styleSidebarButton(btnGestionElection);

        // ==========================================
        // SECTION : GESTION ACADÉMIQUE
        // ==========================================
        
        Label lblSection3 = new Label("🎓 GESTION ACADÉMIQUE");
        lblSection3.setStyle("-fx-text-fill: #78909c; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        btnGestionUfr.setText("🏛️  UFRs");
        btnGestionUfr.setTooltip(new Tooltip("Gérer les UFRs"));
        styleSidebarButton(btnGestionUfr);
        
        btnGestionDepartement.setText("🏛️  Départements");
        btnGestionDepartement.setTooltip(new Tooltip("Gérer les départements"));
        styleSidebarButton(btnGestionDepartement);
        
        btnGestionFiliere.setText("🏛️  Filières");
        btnGestionFiliere.setTooltip(new Tooltip("Gérer les filières"));
        styleSidebarButton(btnGestionFiliere);

        // ==========================================
        // SECTION : STATISTIQUES
        // ==========================================
        
        Label lblSection4 = new Label("📊 STATISTIQUES");
        lblSection4.setStyle("-fx-text-fill: #78909c; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        btnStatistiques.setText("📊  Statistiques");
        btnStatistiques.setTooltip(new Tooltip("Consulter les statistiques"));
        styleSidebarButton(btnStatistiques);

        // ==========================================
        // SÉPARATEUR
        // ==========================================
        
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        // ==========================================
        // BOUTON RAFRAÎCHIR
        // ==========================================
        
        btnRafraichir.setText("🔄  Rafraîchir");
        btnRafraichir.setMaxWidth(Double.MAX_VALUE);
        btnRafraichir.setStyle(
            "-fx-background-color: #005088; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        );
        btnRafraichir.setOnMouseEntered(e -> btnRafraichir.setStyle(
            "-fx-background-color: #003d66; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        ));
        btnRafraichir.setOnMouseExited(e -> btnRafraichir.setStyle(
            "-fx-background-color: #005088; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        ));

        // ==========================================
        // BOUTON DÉCONNEXION
        // ==========================================
        
        btnRetourConnexion.setText("🚪  Déconnexion");
        btnRetourConnexion.setTooltip(new Tooltip("Se déconnecter"));
        btnRetourConnexion.setMaxWidth(Double.MAX_VALUE);
        btnRetourConnexion.setStyle(
            "-fx-background-color: #c0392b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        );
        btnRetourConnexion.setOnMouseEntered(e -> btnRetourConnexion.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        ));
        btnRetourConnexion.setOnMouseExited(e -> btnRetourConnexion.setStyle(
            "-fx-background-color: #c0392b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        ));

        // ==========================================
        // ESPACEUR
        // ==========================================
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ==========================================
        // ASSEMBLAGE DU MENU
        // ==========================================
        
        sidebar.getChildren().addAll(
            btnToggleMenu,
            logoBox,
            separator1,
            lblSection1,
            btnGestionElecteurs,
            btnGestionEnseignantFiliere,
            btnGestionAdministrateurs,
            lblSection2,
            btnGestionCandidat,
            btnGestionElection,
            lblSection3,
            btnGestionUfr,
            btnGestionDepartement,
            btnGestionFiliere,
            lblSection4,
            btnStatistiques,
            separator2,
            spacer,
            btnRafraichir,
            btnRetourConnexion
        );
    }

    // ==========================================
    // STYLE DES BOUTONS DU MENU
    // ==========================================
    
    private void styleSidebarButton(Button button) {
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(8, 15, 8, 15));
        button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #b0bec5; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;"
            );
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
            scaleIn.setFromX(1.0);
            scaleIn.setFromY(1.0);
            scaleIn.setToX(1.03);
            scaleIn.setToY(1.03);
            scaleIn.playFromStart();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #b0bec5; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: normal; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    // ==========================================
    // CRÉATION DU CONTENU PRINCIPAL
    // ==========================================
    
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setMaxWidth(1400);
        content.setAlignment(Pos.TOP_CENTER);

        // ==========================================
        // EN-TÊTE
        // ==========================================
        
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, rgb(210, 67, 67), #c74a2b); " +
            "-fx-background-radius: 16; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(26, 71, 126, 0.3), 15, 0, 0, 4);"
        );
        
        Label title = new Label("📊 Tableau de bord administrateur");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Bienvenue dans votre espace de gestion. Utilisez le menu latéral pour accéder à toutes les fonctionnalités.");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setStyle("-fx-text-fill: #f3f4f4;");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);

        // ==========================================
        // STATISTIQUES - GRID 3x2
        // ==========================================
        
        GridPane statsGrid = createStatisticsGrid();

        content.getChildren().addAll(header, statsGrid);
        return content;
    }

    // ==========================================
    // CRÉATION DES STATISTIQUES
    // ==========================================
    
    private GridPane createStatisticsGrid() {
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(10, 0, 10, 0));
        
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            statsGrid.getColumnConstraints().add(col);
        }

        VBox cardEtudiants = createStatCard("👨‍🎓", "Étudiants", "0", "#3498db", "#e8f0fe");
        VBox cardEnseignants = createStatCard("👨‍🏫", "Enseignants", "0", "#2ecc71", "#e8f8f0");
        VBox cardAdmins = createStatCard("👤", "Administrateurs", "0", "#e67e22", "#fef5e7");
        VBox cardVotants = createStatCard("🗳️", "Votants", "0", "#9b59b6", "#f4ecf7");
        VBox cardNonVotants = createStatCard("❌", "Non Votants", "0", "#e74c3c", "#fdedec");
        VBox cardTotal = createStatCard("📊", "Total Utilisateurs", "0", "#1abc9c", "#e8f8f5");

        lblTotalEtudiants = (Label) ((HBox) cardEtudiants.getChildren().get(0)).getChildren().get(1);
        lblTotalEnseignants = (Label) ((HBox) cardEnseignants.getChildren().get(0)).getChildren().get(1);
        lblTotalAdmins = (Label) ((HBox) cardAdmins.getChildren().get(0)).getChildren().get(1);
        lblVotants = (Label) ((HBox) cardVotants.getChildren().get(0)).getChildren().get(1);
        lblNonVotants = (Label) ((HBox) cardNonVotants.getChildren().get(0)).getChildren().get(1);
        lblTotal = (Label) ((HBox) cardTotal.getChildren().get(0)).getChildren().get(1);

        statsGrid.add(cardEtudiants, 0, 0);
        statsGrid.add(cardEnseignants, 1, 0);
        statsGrid.add(cardAdmins, 2, 0);
        statsGrid.add(cardVotants, 0, 1);
        statsGrid.add(cardNonVotants, 1, 1);
        statsGrid.add(cardTotal, 2, 1);
        
        return statsGrid;
    }

    // ==========================================
    // CARTE STATISTIQUE
    // ==========================================
    
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

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 18));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        topRow.getChildren().addAll(iconLabel, valueLabel);
        
        Label titleLabel = new Label(label);
        titleLabel.setFont(Font.font("System", 13));
        titleLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");
        
        card.getChildren().addAll(topRow, titleLabel);
        return card;
    }

    // ==========================================
    // FONCTION TOGGLE MENU
    // ==========================================
    
    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;
        
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        
        if (isMenuVisible) {
            transition.setToX(0);
            btnToggleMenu.setText("☰ Menu");
            sidebar.setPrefWidth(260);
            sidebar.setMinWidth(200);
        } else {
            transition.setToX(-sidebar.getWidth() + 55);
            btnToggleMenu.setText("☰");
            sidebar.setPrefWidth(55);
            sidebar.setMinWidth(55);
        }
        
        transition.play();
    }

    // ==========================================
    // MISE À JOUR DES STATISTIQUES
    // ==========================================
    
    public void updateStats(int etudiants, int enseignants, int admins, int votants, int nonVotants) {
        lblTotalEtudiants.setText(String.valueOf(etudiants));
        lblTotalEnseignants.setText(String.valueOf(enseignants));
        lblTotalAdmins.setText(String.valueOf(admins));
        lblVotants.setText(String.valueOf(votants));
        lblNonVotants.setText(String.valueOf(nonVotants));
        int total = etudiants + enseignants + admins;
        lblTotal.setText(String.valueOf(total));
    }

    // ==========================================
    // GETTERS
    // ==========================================
    
    public Button getBtnGestionUtilisateurs() { 
        return btnGestionElecteurs; 
    }
    
    public Button getBtnGestionEnseignantFiliere() { 
        return btnGestionEnseignantFiliere; 
    }
    
    public Button getBtnGestionAdministrateurs() { 
        return btnGestionAdministrateurs; 
    }
    
    public Button getBtnGestionCandidats() { 
        return btnGestionCandidat; 
    }
    
    public Button getBtnGestionElections() { 
        return btnGestionElection; 
    }
    
    public Button getBtnGestionUfrs() { 
        return btnGestionUfr; 
    }
    
    public Button getBtnGestionDepartements() { 
        return btnGestionDepartement; 
    }
    
    public Button getBtnGestionFilieres() { 
        return btnGestionFiliere; 
    }
    
    public Button getBtnStatistiques() { 
        return btnStatistiques; 
    }
    
    public Button getBtnRetourConnexion() { 
        return btnRetourConnexion; 
    }
    
    public Button getBtnRafraichir() { 
        return btnRafraichir; 
    }

    // ==========================================
    // GETTERS POUR LES LABELS
    // ==========================================
    
    public Label getLblTotalEtudiants() { 
        return lblTotalEtudiants; 
    }
    
    public Label getLblTotalEnseignants() { 
        return lblTotalEnseignants; 
    }
    
    public Label getLblTotalAdmins() { 
        return lblTotalAdmins; 
    }
    
    public Label getLblVotants() { 
        return lblVotants; 
    }
    
    public Label getLblNonVotants() { 
        return lblNonVotants; 
    }
    
    public Label getLblTotal() { 
        return lblTotal; 
    }
}