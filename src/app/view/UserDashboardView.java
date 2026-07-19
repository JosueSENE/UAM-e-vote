package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class UserDashboardView extends BorderPane {

    private final Button btnVoter;
    private final Button btnResult;
    private final Button btnCandidater;
    private final Button btnStatistiques;
    private final Button btnRetourConnexion;
    
    public UserDashboardView() {
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f4f7fb 0%, #eef3f7 100%);");

        VBox content = new VBox(22);
        content.setMaxWidth(1500);  // Limiter la largeur maximale du contenu
        content.setAlignment(Pos.TOP_CENTER);

        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Tableau de bord utilisateur");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #0f172a;");

        Label subtitle = new Label("Bienvenue dans votre espace électeur . Votez, Candidatez, Visualiser les statistiques  en quelques clics.");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setStyle("-fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(20); // Espacement horizontal entre les cartes
        actionsGrid.setVgap(20);


        btnVoter= createActionButton("Voter", "Votez", "👤", "#2563eb");
        btnCandidater = createActionButton("Candidature", "Déposer ma candidature", "🏛️", "#10b981");
        btnResult = createActionButton("Résultats", "Les resultats des scrutins", "🏛️", "#f59e0b");
        btnStatistiques = createActionButton("Statistiques", "Consulter les indicateurs", "📊", "#fc3815");
        btnRetourConnexion = createActionButton("Retour à la connexion", "Revenir à l'écran d'authentification", "↩", "#6b7280");

        actionsGrid.add(createActionCard(btnVoter, "Voter", "Votez", "👤", "#2563eb"), 0, 0);
        actionsGrid.add(createActionCard(btnCandidater, "Candidature", "Ajouter,Déposer ma candidature", "🏛️", "#10b981"), 1, 0);
        actionsGrid.add(createActionCard(btnResult, "Résultats", "Les resultats des scrutins", "🏛️", "#f59e0b"), 2, 0);
        actionsGrid.add(createActionCard(btnStatistiques, "Statistiques", "Consulter les indicateurs", "📊", "#f59e0b"), 3, 0);
        actionsGrid.add(createActionCard(btnRetourConnexion, "Retour à la connexion", "Revenir à l'écran d'authentification", "↩", "#6b7280"), 2, 1);

        content.getChildren().addAll(header, actionsGrid);
        this.setCenter(content);
    }

    private VBox createActionCard(Button actionButton, String title, String description, String icon, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setPrefWidth(320);
        card.setPrefHeight(180);
        card.setAlignment(Pos.CENTER_LEFT);

        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4); -fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 18;";
        String hoverStyle = "-fx-background-color: #f8fafc; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.18), 14, 0, 0, 8); -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 18;";
        card.setStyle(defaultStyle);
        card.setCursor(javafx.scene.Cursor.HAND);

        // 1. DÉCLARATION INITIALE DES COMPOSANTS VISUELS
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 30));
        iconLabel.setStyle("-fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #111827;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System", 13));
        descLabel.setStyle("-fx-text-fill: #6b7280;");

        // Bouton de la carte
        actionButton.setPrefWidth(220);
        actionButton.setPrefHeight(42);

        // 2. CONFIGURATION DES ANIMATIONS
        ScaleTransition hoverScale = new ScaleTransition(Duration.millis(140), card);
        hoverScale.setFromX(1.0);
        hoverScale.setFromY(1.0);
        hoverScale.setToX(1.02);
        hoverScale.setToY(1.02);

        // 3. ATTACHEMENT DES ÉVÉNEMENTS
        card.setOnMouseEntered(e -> {
            card.setStyle(hoverStyle);
            actionButton.setCursor(javafx.scene.Cursor.HAND);
            iconLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 32px;");
            titleLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            hoverScale.playFromStart();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(defaultStyle);
            actionButton.setCursor(javafx.scene.Cursor.DEFAULT);
            iconLabel.setStyle("-fx-text-fill: " + color + ";");
            titleLabel.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold;");
            hoverScale.stop();
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        // Rendre toute la carte cliquable
        card.setOnMouseClicked(e -> {
            // Déclenche l'événement du bouton associé à cette carte
            actionButton.fire(); 
        });

        card.getChildren().addAll(iconLabel, titleLabel, descLabel, actionButton);
        return card;
    }
    
    private Button createActionButton(String text, String tooltip, String icon, String color) {
        Button button = new Button(icon + "  " + text);
        button.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        button.setOnMouseEntered(e -> button.setCursor(javafx.scene.Cursor.HAND));
        button.setOnMouseExited(e -> button.setCursor(javafx.scene.Cursor.DEFAULT));
        return button;
    }

    public Button getBtnVoter() { return btnVoter; }
    public Button getBtnCandidater() { return btnCandidater; }
    public Button getBtnResults() { return btnResult; }
    public Button getBtnStatistiques() { return btnStatistiques; }
    public Button getBtnRetourConnexion() { return btnRetourConnexion; }

}
