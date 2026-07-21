package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class UserDashboardView extends BorderPane {

    private final Button btnVoter;
    private final Button btnCandidater;
    private final Button btnResult;
    private final Button btnStatistiques;
    private final Button btnRetourConnexion;
    
    public UserDashboardView() {
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f4f7fb 0%, #eef3f7 100%);");

        VBox content = new VBox(22);
        content.setMaxWidth(1500);
        content.setAlignment(Pos.TOP_CENTER);

        // ===== HEADER =====
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📊 Tableau de bord utilisateur");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #0f172a;");

        Label subtitle = new Label("Bienvenue dans votre espace électeur. Votez, Candidatez, Visualisez les statistiques en quelques clics.");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setStyle("-fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        // ===== GRILLE DES ACTIONS =====
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(20);
        actionsGrid.setVgap(20);
        actionsGrid.setAlignment(Pos.CENTER);

        // Création des boutons
        btnVoter = createActionButton("Voter", "Participer aux élections en cours", "🗳️", "#2563eb");
        btnCandidater = createActionButton("Candidature", "Déposer ma candidature", "📝", "#10b981");
        btnResult = createActionButton("Résultats", "Consulter les résultats des scrutins", "🏆", "#f59e0b");
        btnStatistiques = createActionButton("Statistiques", "Consulter les indicateurs de participation", "📊", "#8b5cf6");
        btnRetourConnexion = createActionButton("Déconnexion", "Revenir à l'écran d'authentification", "↩", "#6b7280");

        // Ajout des cartes dans la grille (2 colonnes pour une meilleure présentation)
        actionsGrid.add(createActionCard(btnVoter, "Voter", "Participer aux élections en cours", "🗳️", "#2563eb"), 0, 0);
        actionsGrid.add(createActionCard(btnCandidater, "Candidature", "Déposer ma candidature", "📝", "#10b981"), 1, 0);
        actionsGrid.add(createActionCard(btnResult, "Résultats", "Consulter les résultats des scrutins", "🏆", "#f59e0b"), 0, 1);
        actionsGrid.add(createActionCard(btnStatistiques, "Statistiques", "Consulter les indicateurs de participation", "📊", "#8b5cf6"), 1, 1);
        actionsGrid.add(createActionCard(btnRetourConnexion, "Déconnexion", "Revenir à l'écran d'authentification", "↩", "#6b7280"), 0, 2);

        // Centrer la dernière carte
        GridPane.setColumnSpan(btnRetourConnexion.getParent() != null ? 
            actionsGrid.getChildren().get(actionsGrid.getChildren().size() - 1) : null, 2);
        GridPane.setHalignment(actionsGrid.getChildren().get(actionsGrid.getChildren().size() - 1), 
            javafx.geometry.HPos.CENTER);

        content.getChildren().addAll(header, actionsGrid);
        this.setCenter(content);
    }

    // ==========================================
    // CRÉATION D'UNE CARTE D'ACTION
    // ==========================================

    private VBox createActionCard(Button actionButton, String title, String description, String icon, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setPrefWidth(300);
        card.setPrefHeight(200);
        card.setAlignment(Pos.CENTER_LEFT);

        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 18; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4); " +
                              "-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 18;";
        
        String hoverStyle = "-fx-background-color: #f8fafc; -fx-background-radius: 18; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.18), 14, 0, 0, 8); " +
                            "-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 18;";
        
        card.setStyle(defaultStyle);
        card.setCursor(javafx.scene.Cursor.HAND);

        // Icône
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 34));
        iconLabel.setStyle("-fx-text-fill: " + color + ";");

        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        titleLabel.setStyle("-fx-text-fill: #111827;");

        // Description
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System", 13));
        descLabel.setStyle("-fx-text-fill: #6b7280;");
        descLabel.setMaxWidth(240);

        // Bouton
        actionButton.setPrefWidth(220);
        actionButton.setPrefHeight(38);
        actionButton.setStyle("-fx-background-color: " + color + "; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-font-size: 14px; " +
                             "-fx-background-radius: 10; " +
                             "-fx-cursor: hand;");

        // Effet hover sur le bouton
        actionButton.setOnMouseEntered(e -> {
            actionButton.setStyle("-fx-background-color: " + darkenColor(color) + "; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-background-radius: 10; " +
                                 "-fx-cursor: hand;");
        });
        actionButton.setOnMouseExited(e -> {
            actionButton.setStyle("-fx-background-color: " + color + "; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-background-radius: 10; " +
                                 "-fx-cursor: hand;");
        });

        // Animation
        ScaleTransition hoverScale = new ScaleTransition(Duration.millis(140), card);
        hoverScale.setFromX(1.0);
        hoverScale.setFromY(1.0);
        hoverScale.setToX(1.03);
        hoverScale.setToY(1.03);

        // Événements de la carte
        card.setOnMouseEntered(e -> {
            card.setStyle(hoverStyle);
            iconLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 36px;");
            titleLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 18px;");
            hoverScale.playFromStart();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(defaultStyle);
            iconLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 34px;");
            titleLabel.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 17px;");
            hoverScale.stop();
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        // Rendre toute la carte cliquable
        card.setOnMouseClicked(e -> actionButton.fire());

        card.getChildren().addAll(iconLabel, titleLabel, descLabel, actionButton);
        return card;
    }
    
    // ==========================================
    // CRÉATION D'UN BOUTON
    // ==========================================

    private Button createActionButton(String text, String tooltipText, String icon, String color) {
        Button button = new Button(icon + "  " + text);
        button.setTooltip(new Tooltip(tooltipText));
        button.setStyle("-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-font-weight: bold; " +
                       "-fx-font-size: 14px; " +
                       "-fx-background-radius: 10; " +
                       "-fx-cursor: hand;");
        
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + darkenColor(color) + "; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-background-radius: 10; " +
                           "-fx-cursor: hand;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-background-radius: 10; " +
                           "-fx-cursor: hand;");
        });
        
        return button;
    }

    // ==========================================
    // MÉTHODE UTILITAIRE POUR ASSOMBRIR UNE COULEUR
    // ==========================================

    private String darkenColor(String color) {
        // Assombrir les couleurs hexadécimales pour l'effet hover
        switch (color) {
            case "#2563eb": return "#1d4ed8"; // Bleu
            case "#10b981": return "#059669"; // Vert
            case "#f59e0b": return "#d97706"; // Orange
            case "#8b5cf6": return "#7c3aed"; // Violet
            case "#6b7280": return "#4b5563"; // Gris
            default: return color;
        }
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public Button getBtnVoter() { 
        return btnVoter; 
    }
    
    public Button getBtnCandidater() { 
        return btnCandidater; 
    }
    
    public Button getBtnResults() { 
        return btnResult; 
    }
    
    public Button getBtnStatistiques() { 
        return btnStatistiques; 
    }
    
    public Button getBtnRetourConnexion() { 
        return btnRetourConnexion; 
    }
}