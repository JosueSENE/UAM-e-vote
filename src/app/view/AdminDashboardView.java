package app.view;

import app.controller.LoginController;
import app.model.Admin;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminDashboardView extends BorderPane {

    // ==========================================
    // MENU LATERAL (SIDEBAR)
    // ==========================================
    
    private VBox sidebar;
    private VBox sidebarContent;
    private ScrollPane sidebarScroll;
    private Button btnToggleMenu;
    private boolean isMenuVisible = true;
    private static final double MENU_WIDTH = 230;
    private static final double MENU_COLLAPSED_WIDTH = 60;
    
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

    // ==========================================
    // PROFIL ADMINISTRATEUR
    // ==========================================
    
    private HBox profileBox;
    private Label lblAdminName;
    private Label lblAdminEmail;
    private HBox headerContainer;
    private HBox mainLayout;

    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

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
        
        mainLayout = new HBox();
        mainLayout.getChildren().addAll(sidebar, mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        
        this.setCenter(mainLayout);
    }

    // ==========================================
    // CRÉATION DU MENU LATERAL AVEC SCROLL
    // ==========================================
    
    private void createSidebar() {
        // Conteneur principal du menu
        sidebar = new VBox(0);
        sidebar.setPrefWidth(MENU_WIDTH);
        sidebar.setMinWidth(MENU_COLLAPSED_WIDTH);
        sidebar.setMaxWidth(MENU_WIDTH);
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1a237e, #0d1442);"
        );

        // ==========================================
        // BOUTON TOGGLE MENU (FIXE EN HAUT)
        // ==========================================
        
        btnToggleMenu = new Button("☰ Menu");
        btnToggleMenu.setMaxWidth(Double.MAX_VALUE);
        btnToggleMenu.setAlignment(Pos.CENTER_LEFT);
        btnToggleMenu.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15;"
        );
        btnToggleMenu.setOnMouseEntered(e -> btnToggleMenu.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 0; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15;"
        ));
        btnToggleMenu.setOnMouseExited(e -> btnToggleMenu.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15;"
        ));
        btnToggleMenu.setOnAction(e -> toggleMenu());

        // ==========================================
        // LOGO / TITRE (FIXE EN HAUT)
        // ==========================================
        
        VBox logoBox = new VBox(2);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(8, 0, 10, 0));
        logoBox.setStyle("-fx-background-color: rgba(255,255,255,0.03);");
        logoBox.setMinHeight(70);
        
        Label lblLogo = new Label("📊");
        lblLogo.setFont(Font.font("System", 28));
        lblLogo.setId("logoLabel");
        
        Label lblTitle = new Label("UAM e-Vote");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitle.setStyle("-fx-text-fill: white;");
        lblTitle.setId("titleLabel");
        
        Label lblSubtitle = new Label("Administration");
        lblSubtitle.setFont(Font.font("System", 10));
        lblSubtitle.setStyle("-fx-text-fill: #90a4ae;");
        lblSubtitle.setId("subtitleLabel");
        
        logoBox.getChildren().addAll(lblLogo, lblTitle, lblSubtitle);

        // ==========================================
        // SÉPARATEUR
        // ==========================================
        
        Separator separatorTop = new Separator();
        separatorTop.setStyle("-fx-background-color: rgba(255,255,255,0.08);");

        // ==========================================
        // CONTENU SCROLLABLE DU MENU
        // ==========================================
        
        sidebarContent = new VBox(5);
        sidebarContent.setPadding(new Insets(5, 10, 10, 10));
        
        // ==========================================
        // SECTION : GESTION DES UTILISATEURS
        // ==========================================
        
        Label lblSection1 = new Label("👥");
        lblSection1.setStyle("-fx-text-fill: #78909c; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 8 0 3 0;");
        lblSection1.setId("section1");
        lblSection1.setAlignment(Pos.CENTER);
        
        btnGestionElecteurs.setText("👥 Électeurs");
        btnGestionElecteurs.setTooltip(new Tooltip("Gérer les étudiants"));
        styleSidebarButton(btnGestionElecteurs);
        btnGestionElecteurs.setId("btnElecteurs");
        
        btnGestionEnseignantFiliere.setText("👨‍🏫 Enseignants");
        btnGestionEnseignantFiliere.setTooltip(new Tooltip("Gérer les enseignants"));
        styleSidebarButton(btnGestionEnseignantFiliere);
        btnGestionEnseignantFiliere.setId("btnEnseignants");
        
        btnGestionAdministrateurs.setText("👤 Administrateurs");
        btnGestionAdministrateurs.setTooltip(new Tooltip("Gérer les administrateurs"));
        styleSidebarButton(btnGestionAdministrateurs);
        btnGestionAdministrateurs.setId("btnAdmins");

        // ==========================================
        // SECTION : GESTION DES ÉLECTIONS
        // ==========================================
        
        Label lblSection2 = new Label("📋");
        lblSection2.setStyle("-fx-text-fill: #78909c; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 8 0 3 0;");
        lblSection2.setId("section2");
        lblSection2.setAlignment(Pos.CENTER);
        
        btnGestionCandidat.setText("📋 Candidats");
        btnGestionCandidat.setTooltip(new Tooltip("Gérer les candidats"));
        styleSidebarButton(btnGestionCandidat);
        btnGestionCandidat.setId("btnCandidats");
        
        btnGestionElection.setText("🏛️ Scrutins");
        btnGestionElection.setTooltip(new Tooltip("Gérer les élections"));
        styleSidebarButton(btnGestionElection);
        btnGestionElection.setId("btnElections");

        // ==========================================
        // SECTION : GESTION ACADÉMIQUE
        // ==========================================
        
        Label lblSection3 = new Label("🎓");
        lblSection3.setStyle("-fx-text-fill: #78909c; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 8 0 3 0;");
        lblSection3.setId("section3");
        lblSection3.setAlignment(Pos.CENTER);
        
        btnGestionUfr.setText("🏛️ UFRs");
        btnGestionUfr.setTooltip(new Tooltip("Gérer les UFRs"));
        styleSidebarButton(btnGestionUfr);
        btnGestionUfr.setId("btnUfrs");
        
        btnGestionDepartement.setText("🏛️ Départements");
        btnGestionDepartement.setTooltip(new Tooltip("Gérer les départements"));
        styleSidebarButton(btnGestionDepartement);
        btnGestionDepartement.setId("btnDepartements");
        
        btnGestionFiliere.setText("🏛️ Filières");
        btnGestionFiliere.setTooltip(new Tooltip("Gérer les filières"));
        styleSidebarButton(btnGestionFiliere);
        btnGestionFiliere.setId("btnFilieres");

        // ==========================================
        // SECTION : STATISTIQUES
        // ==========================================
        
        Label lblSection4 = new Label("📊");
        lblSection4.setStyle("-fx-text-fill: #78909c; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 8 0 3 0;");
        lblSection4.setId("section4");
        lblSection4.setAlignment(Pos.CENTER);
        
        btnStatistiques.setText("📊 Statistiques");
        btnStatistiques.setTooltip(new Tooltip("Consulter les statistiques"));
        styleSidebarButton(btnStatistiques);
        btnStatistiques.setId("btnStats");

        // ==========================================
        // SÉPARATEUR
        // ==========================================
        
        Separator separatorBottom = new Separator();
        separatorBottom.setStyle("-fx-background-color: rgba(255,255,255,0.08);");

        // ==========================================
        // BOUTON RAFRAÎCHIR
        // ==========================================
        
        btnRafraichir.setText("🔄 Rafraîchir");
        btnRafraichir.setMaxWidth(Double.MAX_VALUE);
        btnRafraichir.setStyle(
            "-fx-background-color: #005088; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        );
        btnRafraichir.setOnMouseEntered(e -> btnRafraichir.setStyle(
            "-fx-background-color: #003d66; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        ));
        btnRafraichir.setOnMouseExited(e -> btnRafraichir.setStyle(
            "-fx-background-color: #005088; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        ));
        btnRafraichir.setId("btnRafraichir");

        // ==========================================
        // BOUTON DÉCONNEXION
        // ==========================================
        
        btnRetourConnexion.setText("🚪 Déconnexion");
        btnRetourConnexion.setTooltip(new Tooltip("Se déconnecter"));
        btnRetourConnexion.setMaxWidth(Double.MAX_VALUE);
        btnRetourConnexion.setStyle(
            "-fx-background-color: #c0392b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        );
        btnRetourConnexion.setOnMouseEntered(e -> btnRetourConnexion.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        ));
        btnRetourConnexion.setOnMouseExited(e -> btnRetourConnexion.setStyle(
            "-fx-background-color: #c0392b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 6; " +
            "-fx-cursor: hand;"
        ));
        btnRetourConnexion.setId("btnDeconnexion");

        // ==========================================
        // ESPACEUR POUR POUSSER VERS LE BAS
        // ==========================================
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ==========================================
        // ASSEMBLAGE DU CONTENU SCROLLABLE
        // ==========================================
        
        sidebarContent.getChildren().addAll(
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
            spacer,
            separatorBottom,
            btnRafraichir,
            btnRetourConnexion
        );

        // ==========================================
        // SCROLLPANE POUR LE MENU
        // ==========================================
        
        sidebarScroll = new ScrollPane(sidebarContent);
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 0;"
        );
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // ==========================================
        // ASSEMBLAGE FINAL DU MENU
        // ==========================================
        
        sidebar.getChildren().addAll(
            btnToggleMenu,
            logoBox,
            separatorTop,
            sidebarScroll
        );
        VBox.setVgrow(sidebarScroll, Priority.ALWAYS);
    }

    // ==========================================
    // STYLE DES BOUTONS DU MENU
    // ==========================================
    
    private void styleSidebarButton(Button button) {
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(5, 12, 5, 12));
        button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #b0bec5; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: normal; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand;"
            );
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(120), button);
            scaleIn.setFromX(1.0);
            scaleIn.setFromY(1.0);
            scaleIn.setToX(1.02);
            scaleIn.setToY(1.02);
            scaleIn.playFromStart();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #b0bec5; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: normal; " +
                "-fx-background-radius: 4; " +
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
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setMaxWidth(1400);
        content.setAlignment(Pos.TOP_CENTER);

        // ==========================================
        // EN-TÊTE AVEC PROFIL ADMIN
        // ==========================================
        
        headerContainer = createHeaderWithProfile();

        // ==========================================
        // STATISTIQUES - GRID 3x2
        // ==========================================
        
        GridPane statsGrid = createStatisticsGrid();

        content.getChildren().addAll(headerContainer, statsGrid);
        return content;
    }

    // ==========================================
    // CRÉATION DE L'EN-TÊTE AVEC PROFIL
    // ==========================================
    
    private HBox createHeaderWithProfile() {
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setSpacing(15);
        headerContainer.setPadding(new Insets(10, 20, 10, 20));
        headerContainer.setStyle(
            "-fx-background-color: linear-gradient(to right, #1a237e, #283593); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(26, 71, 126, 0.3), 12, 0, 0, 4);"
        );
        
        // Titre à gauche
        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📊 Tableau de bord administrateur");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Bienvenue dans votre espace de gestion. Utilisez le menu latéral pour accéder à toutes les fonctionnalités.");
        subtitle.setFont(Font.font("System", 11));
        subtitle.setStyle("-fx-text-fill: #b0bec5;");
        subtitle.setWrapText(true);
        
        titleBox.getChildren().addAll(title, subtitle);
        
        // Espaceur
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // ==========================================
        // PROFIL ADMINISTRATEUR (À DROITE)
        // ==========================================
        
        profileBox = createProfileBox();
        
        headerContainer.getChildren().addAll(titleBox, spacer, profileBox);
        return headerContainer;
    }

    // ==========================================
    // CRÉATION DU PROFIL ADMINISTRATEUR
    // ==========================================
    
    private HBox createProfileBox() {
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_RIGHT);
        profileBox.setPadding(new Insets(5, 0, 5, 0));
        profileBox.setStyle("-fx-cursor: hand;");
        
        // Avatar circulaire avec initiales ou icône
        Circle avatarCircle = new Circle(22);
        avatarCircle.setFill(Color.web("#4CAF50"));
        avatarCircle.setStroke(Color.WHITE);
        avatarCircle.setStrokeWidth(2);
        
        // Image ou label pour l'avatar
        Label avatarLabel = new Label("👤");
        avatarLabel.setFont(Font.font("System", 18));
        avatarLabel.setStyle("-fx-text-fill: white;");
        
        StackPane avatarPane = new StackPane(avatarCircle, avatarLabel);
        
        // Informations de l'admin
        VBox infoBox = new VBox(1);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        lblAdminName = new Label("Administrateur");
        lblAdminName.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblAdminName.setStyle("-fx-text-fill: white;");
        
        lblAdminEmail = new Label("admin@uam.edu.sn");
        lblAdminEmail.setFont(Font.font("System", 10));
        lblAdminEmail.setStyle("-fx-text-fill: #90a4ae;");
        
        infoBox.getChildren().addAll(lblAdminName, lblAdminEmail);
        
        // Flèche vers le bas pour indiquer le menu
        Label arrowLabel = new Label("▼");
        arrowLabel.setFont(Font.font("System", 10));
        arrowLabel.setStyle("-fx-text-fill: #90a4ae;");
        
        profileBox.getChildren().addAll(avatarPane, infoBox, arrowLabel);
        
        // Effet au survol
        profileBox.setOnMouseEntered(e -> {
            profileBox.setStyle(
                "-fx-cursor: hand; " +
                "-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 5;"
            );
        });
        profileBox.setOnMouseExited(e -> {
            profileBox.setStyle("-fx-cursor: hand; -fx-padding: 5;");
        });
        
        // Click pour ouvrir le menu de profil
        profileBox.setOnMouseClicked(e -> afficherMenuProfil());
        
        return profileBox;
    }

    // ==========================================
    // MENU DE PROFIL (POPUP) - CORRIGÉ
    // ==========================================
    
    private void afficherMenuProfil() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem itemProfil = new MenuItem("👤 Mon Profil");
        itemProfil.setStyle("-fx-font-size: 12px; -fx-padding: 8 15;");
        itemProfil.setOnAction(e -> afficherDetailsProfil());
        
        MenuItem itemParametres = new MenuItem("⚙️ Paramètres");
        itemParametres.setStyle("-fx-font-size: 12px; -fx-padding: 8 15;");
        itemParametres.setOnAction(e -> afficherParametres());
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        
        MenuItem itemDeconnexion = new MenuItem("🚪 Déconnexion");
        itemDeconnexion.setStyle("-fx-font-size: 12px; -fx-padding: 8 15; -fx-text-fill: #e74c3c;");
        // ✅ CORRECTION : Appel direct à la méthode de déconnexion
        itemDeconnexion.setOnAction(e -> {
            // Demander confirmation avant de se déconnecter
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Déconnexion");
            confirm.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
            confirm.setContentText("Vous serez redirigé vers la page de connexion.");
            
            // Personnalisation des boutons
            DialogPane dialogPane = confirm.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white;");
            
            Button yesButton = (Button) dialogPane.lookupButton(ButtonType.YES);
            Button noButton = (Button) dialogPane.lookupButton(ButtonType.NO);
            
            if (yesButton != null) {
                yesButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            if (noButton != null) {
                noButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // ✅ Déclencher la déconnexion
                    if (btnRetourConnexion != null) {
                        btnRetourConnexion.fire();
                    } else {
                        // Fallback : redirection directe
                        retournerConnexion();
                    }
                }
            });
        });
        
        contextMenu.getItems().addAll(itemProfil, itemParametres, separator, itemDeconnexion);
        contextMenu.show(profileBox, 
            profileBox.localToScreen(0, profileBox.getHeight()).getX(),
            profileBox.localToScreen(0, profileBox.getHeight()).getY()
        );
    }

    // ==========================================
    // MÉTHODE DE RETOUR À LA CONNEXION (FALLBACK)
    // ==========================================
    
    private void retournerConnexion() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);
            Scene scene = new Scene(loginController, 1400, 700);
            stage.setTitle("UAM e-Vote - Connexion");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de revenir à la page de connexion : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ==========================================
    // AFFICHAGE DES DÉTAILS DU PROFIL
    // ==========================================
    
    private void afficherDetailsProfil() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("👤 Mon Profil");
        alert.setHeaderText("Informations de l'administrateur");
        
        String details = "📋 **Informations personnelles**\n\n" +
                         "👤 Nom : " + lblAdminName.getText() + "\n" +
                         "📧 Email : " + lblAdminEmail.getText() + "\n" +
                         "🔑 Rôle : Administrateur\n\n" +
                         "📊 **Statistiques**\n" +
                         "🕐 Dernière connexion : " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
                         "💻 Session active depuis : " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        
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

    private void afficherParametres() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("⚙️ Paramètres");
        alert.setHeaderText("Paramètres du compte");
        alert.setContentText("Fonctionnalité en cours de développement...");
        alert.showAndWait();
    }

    // ==========================================
    // MISE À JOUR DES INFORMATIONS DU PROFIL
    // ==========================================
    
    public void setAdminInfo(String nom, String prenom, String email) {
        if (lblAdminName != null) {
            lblAdminName.setText(prenom + " " + nom);
        }
        if (lblAdminEmail != null) {
            lblAdminEmail.setText(email);
        }
    }

    public void setAdminInfo(Admin admin) {
        if (admin != null) {
            setAdminInfo(admin.getNom(), admin.getPrenom(), admin.getEmail());
        }
    }

    // ==========================================
    // CRÉATION DES STATISTIQUES
    // ==========================================
    
    private GridPane createStatisticsGrid() {
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(12);
        statsGrid.setVgap(12);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(5, 0, 5, 0));
        
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
        VBox card = new VBox(2);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setPrefWidth(160);
        card.setPrefHeight(70);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 6, 0, 0, 2);"
        );

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 16));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        topRow.getChildren().addAll(iconLabel, valueLabel);
        
        Label titleLabel = new Label(label);
        titleLabel.setFont(Font.font("System", 11));
        titleLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");
        
        card.getChildren().addAll(topRow, titleLabel);
        return card;
    }

    // ==========================================
    // FONCTION TOGGLE MENU
    // ==========================================
    
    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;
        
        if (isMenuVisible) {
            // AGRANDIR LE MENU
            sidebar.setPrefWidth(MENU_WIDTH);
            sidebar.setMinWidth(MENU_WIDTH);
            sidebar.setMaxWidth(MENU_WIDTH);
            
            TranslateTransition expand = new TranslateTransition(Duration.millis(300), sidebar);
            expand.setToX(0);
            expand.play();
            
            btnToggleMenu.setText("☰ Menu");
            showFullMenu(true);
            
        } else {
            // RÉDUIRE LE MENU
            sidebar.setPrefWidth(MENU_COLLAPSED_WIDTH);
            sidebar.setMinWidth(MENU_COLLAPSED_WIDTH);
            sidebar.setMaxWidth(MENU_COLLAPSED_WIDTH);
            
            TranslateTransition collapse = new TranslateTransition(Duration.millis(300), sidebar);
            collapse.setToX(0);
            collapse.play();
            
            btnToggleMenu.setText("☰");
            showFullMenu(false);
        }
    }

    private void showFullMenu(boolean show) {
        // Gérer les sections (labels)
        String[] sectionIds = {"section1", "section2", "section3", "section4"};
        for (String id : sectionIds) {
            Label lbl = (Label) sidebar.lookup("#" + id);
            if (lbl != null) {
                if (show) {
                    switch(id) {
                        case "section1": lbl.setText("👥 UTILISATEURS"); break;
                        case "section2": lbl.setText("📋 ÉLECTIONS"); break;
                        case "section3": lbl.setText("🎓 ACADÉMIQUE"); break;
                        case "section4": lbl.setText("📊 STATISTIQUES"); break;
                    }
                    lbl.setAlignment(Pos.CENTER_LEFT);
                } else {
                    switch(id) {
                        case "section1": lbl.setText("👥"); break;
                        case "section2": lbl.setText("📋"); break;
                        case "section3": lbl.setText("🎓"); break;
                        case "section4": lbl.setText("📊"); break;
                    }
                    lbl.setAlignment(Pos.CENTER);
                }
                lbl.setVisible(true);
                lbl.setManaged(true);
            }
        }

        // Gérer les boutons
        String[] buttonIds = {
            "btnElecteurs", "btnEnseignants", "btnAdmins",
            "btnCandidats", "btnElections",
            "btnUfrs", "btnDepartements", "btnFilieres",
            "btnStats", "btnRafraichir", "btnDeconnexion"
        };
        
        for (String id : buttonIds) {
            Button btn = (Button) sidebar.lookup("#" + id);
            if (btn != null) {
                if (show) {
                    switch(id) {
                        case "btnElecteurs": btn.setText("👥 Électeurs"); break;
                        case "btnEnseignants": btn.setText("👨‍🏫 Enseignants"); break;
                        case "btnAdmins": btn.setText("👤 Administrateurs"); break;
                        case "btnCandidats": btn.setText("📋 Candidats"); break;
                        case "btnElections": btn.setText("🏛️ Scrutins"); break;
                        case "btnUfrs": btn.setText("🏛️ UFRs"); break;
                        case "btnDepartements": btn.setText("🏛️ Départements"); break;
                        case "btnFilieres": btn.setText("🏛️ Filières"); break;
                        case "btnStats": btn.setText("📊 Statistiques"); break;
                        case "btnRafraichir": btn.setText("🔄 Rafraîchir"); break;
                        case "btnDeconnexion": btn.setText("🚪 Déconnexion"); break;
                    }
                    btn.setAlignment(Pos.CENTER_LEFT);
                } else {
                    switch(id) {
                        case "btnElecteurs": btn.setText("👥"); break;
                        case "btnEnseignants": btn.setText("👨‍🏫"); break;
                        case "btnAdmins": btn.setText("👤"); break;
                        case "btnCandidats": btn.setText("📋"); break;
                        case "btnElections": btn.setText("🏛️"); break;
                        case "btnUfrs": btn.setText("🏛️"); break;
                        case "btnDepartements": btn.setText("🏛️"); break;
                        case "btnFilieres": btn.setText("🏛️"); break;
                        case "btnStats": btn.setText("📊"); break;
                        case "btnRafraichir": btn.setText("🔄"); break;
                        case "btnDeconnexion": btn.setText("🚪"); break;
                    }
                    btn.setAlignment(Pos.CENTER);
                }
                btn.setVisible(true);
                btn.setManaged(true);
            }
        }

        // Gérer le logo
        Label logoLabel = (Label) sidebar.lookup("#logoLabel");
        Label titleLabel = (Label) sidebar.lookup("#titleLabel");
        Label subtitleLabel = (Label) sidebar.lookup("#subtitleLabel");
        
        if (logoLabel != null) {
            logoLabel.setVisible(true);
            logoLabel.setManaged(true);
        }
        
        if (titleLabel != null) {
            titleLabel.setVisible(show);
            titleLabel.setManaged(show);
        }
        
        if (subtitleLabel != null) {
            subtitleLabel.setVisible(show);
            subtitleLabel.setManaged(show);
        }
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

    public HBox getProfileBox() {
        return profileBox;
    }

    public Label getLblAdminName() {
        return lblAdminName;
    }

    public Label getLblAdminEmail() {
        return lblAdminEmail;
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