package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminManagementView extends BorderPane {

    private final Button btnRetour;

    public AdminManagementView() {
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #f4f7fb;");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);
        content.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        Label title = new Label("Gestion des administrations");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #005088;");

        Label subtitle = new Label("Cette page sera complétée prochainement.");
        subtitle.setStyle("-fx-text-fill: #666666;");

        btnRetour = new Button("← Retour au tableau de bord");
        btnRetour.setPrefWidth(260);
        btnRetour.setPrefHeight(45);
        btnRetour.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnRetour.setOnMouseEntered(e -> btnRetour.setCursor(javafx.scene.Cursor.HAND));
        btnRetour.setOnMouseExited(e -> btnRetour.setCursor(javafx.scene.Cursor.DEFAULT));

        content.getChildren().addAll(title, subtitle, btnRetour);
        this.setCenter(content);
    }

    public Button getBtnRetour() {
        return btnRetour;
    }
}
