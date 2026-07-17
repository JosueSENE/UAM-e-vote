package app.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends BorderPane {

    private TextField txtEmail;
    private PasswordField txtSecret; 
    private Label lblSecret;         
    private Button btnConnexion;

    public LoginView() {
        this.setStyle("-fx-background-color: #f0f2f5;");
        initView();
    }

    private void initView() {
        VBox loginCard = new VBox(15);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxSize(400, 450);
        loginCard.setPadding(new Insets(35));
        loginCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // ============== LOGO UAM ==============
        ImageView logoView = new ImageView();
        try{
            Image logo = new Image(getClass().getResourceAsStream("/ressources/logo_uam.png"));
            logoView.setImage(logo);
            // Ajustement de la taille du logo
            logoView.setFitWidth(200);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        }catch (Exception e){
            System.err.println("Impossible de charger le logo de l'université : "+e.getMessage());
        }
        // En-tête
        Label lblTitle = new Label("UAM e-Vote");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        Label lblSubtitle = new Label("Portail d'authentification unique");
        lblSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lblSubtitle.setStyle("-fx-text-fill: #777777;");

        // On regroupe le logo et les textes dans le bloc d'en-tête
        VBox headerBox = new VBox(20); // Un peu d'espace entre les éléments
        headerBox.setAlignment(Pos.CENTER);
        
        // Si le logo a été chargé avec succès, on l'ajoute au-dessus du titre
        if (logoView.getImage() != null) {
            headerBox.getChildren().add(logoView);
        }
        headerBox.getChildren().addAll(lblTitle, lblSubtitle);

        // Champ Email
        VBox emailBox = new VBox(5);
        Label lblEmail = new Label("Adresse Email Institutionnelle");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        txtEmail = new TextField();
        txtEmail.setPromptText("Ex: prenom.nom@uam.edu.sn");
        txtEmail.setPrefHeight(40);
        txtEmail.setStyle("-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        // Champ Secret Unique
        VBox secretBox = new VBox(5);
        lblSecret = new Label("Mot de passe"); 
        lblSecret.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        txtSecret = new PasswordField();
        txtSecret.setPromptText("Saisissez votre mot de passe");
        txtSecret.setPrefHeight(40);
        txtSecret.setStyle("-fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        secretBox.getChildren().addAll(lblSecret, txtSecret);

        // Bouton Connexion
        btnConnexion = new Button("Se connecter");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);
        btnConnexion.setPrefHeight(45);
        btnConnexion.setCursor(javafx.scene.Cursor.HAND);
        btnConnexion.setStyle("-fx-background-color: #005088; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 5;");

        loginCard.getChildren().addAll(headerBox, new Separator(), emailBox, secretBox, btnConnexion);
        this.setCenter(loginCard);
    }

    // Getters pour permettre au Contrôleur d'accéder aux données et aux événements
    public TextField getTxtEmail() { return txtEmail; }
    public PasswordField getTxtSecret() { return txtSecret; }
    public Label getLblSecret() { return lblSecret; }
    public Button getBtnConnexion() { return btnConnexion; }
}