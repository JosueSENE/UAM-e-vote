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
    private Label lblStatus;         // Pour afficher les messages d'état
    private Hyperlink lienMotDePasseOublie; // Lien pour mot de passe oublié

    public LoginView() {
        this.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f2f5, #e8edf3);");
        initView();
    }

    private void initView() {
        // Conteneur principal centré
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxSize(450, 520);
        loginCard.setPadding(new Insets(40));
        loginCard.setStyle("-fx-background-color: white; " +
                          "-fx-background-radius: 20; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 8);");

        // ============== SECTION EN-TÊTE ==============
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);

        // Logo UAM
        ImageView logoView = new ImageView();
        try {
            // Essayer de charger le logo depuis le dossier ressources
            Image logo = new Image(getClass().getResourceAsStream("/ressources/logo_uam.png"));
            if (logo != null && !logo.isError()) {
                logoView.setImage(logo);
                logoView.setFitWidth(180);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
            } else {
                // Logo par défaut si l'image n'est pas trouvée
                logoView = createDefaultLogo();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de charger le logo de l'université : " + e.getMessage());
            logoView = createDefaultLogo();
        }
        
        // Titre
        Label lblTitle = new Label("UAM e-Vote");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 30));
        lblTitle.setStyle("-fx-text-fill: #005088;");

        // Sous-titre
        Label lblSubtitle = new Label("🔐 Portail d'authentification unique");
        lblSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lblSubtitle.setStyle("-fx-text-fill: #777777;");

        headerBox.getChildren().addAll(logoView, lblTitle, lblSubtitle);

        // ============== SÉPARATEUR ==============
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");

        // ============== SECTION FORMULAIRE ==============
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);

        // Champ Email
        VBox emailBox = new VBox(5);
        Label lblEmail = new Label("📧 Adresse Email Institutionnelle");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-text-fill: #444; -fx-font-size: 13px;");
        
        txtEmail = new TextField();
        txtEmail.setPromptText("exemple@uam.edu.sn");
        txtEmail.setPrefHeight(42);
        txtEmail.setStyle("-fx-background-radius: 8; " +
                         "-fx-border-color: #d0d0d0; " +
                         "-fx-border-radius: 8; " +
                         "-fx-padding: 8 12; " +
                         "-fx-font-size: 14px;");
        
        // Ajout d'un tooltip
        Tooltip emailTooltip = new Tooltip("Saisissez votre adresse email institutionnelle (@uam.*)");
        txtEmail.setTooltip(emailTooltip);
        
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        // Champ Secret
        VBox secretBox = new VBox(5);
        lblSecret = new Label("🔑 Mot de passe");
        lblSecret.setStyle("-fx-font-weight: bold; -fx-text-fill: #444; -fx-font-size: 13px;");
        
        txtSecret = new PasswordField();
        txtSecret.setPromptText("Saisissez votre mot de passe");
        txtSecret.setPrefHeight(42);
        txtSecret.setStyle("-fx-background-radius: 8; " +
                          "-fx-border-color: #d0d0d0; " +
                          "-fx-border-radius: 8; " +
                          "-fx-padding: 8 12; " +
                          "-fx-font-size: 14px;");
        
        secretBox.getChildren().addAll(lblSecret, txtSecret);

        // ============== SECTION STATUS ==============
        lblStatus = new Label();
        lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");
        lblStatus.setVisible(false);

        // ============== SECTION ACTIONS ==============
        VBox actionBox = new VBox(12);
        actionBox.setAlignment(Pos.CENTER);

        // Bouton Connexion
        btnConnexion = new Button("Se connecter");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);
        btnConnexion.setPrefHeight(45);
        btnConnexion.setCursor(javafx.scene.Cursor.HAND);
        btnConnexion.setStyle("-fx-background-color: #005088; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-font-size: 15px; " +
                             "-fx-background-radius: 8; " +
                             "-fx-cursor: hand;");
        
        // Effet hover
        btnConnexion.setOnMouseEntered(e -> {
            btnConnexion.setStyle("-fx-background-color: #006699; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-font-size: 15px; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand;");
        });
        btnConnexion.setOnMouseExited(e -> {
            btnConnexion.setStyle("-fx-background-color: #005088; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-font-size: 15px; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand;");
        });

        // Lien Mot de passe oublié
        lienMotDePasseOublie = new Hyperlink("🔓 Mot de passe oublié ?");
        lienMotDePasseOublie.setStyle("-fx-font-size: 12px; -fx-text-fill: #005088;");
        lienMotDePasseOublie.setCursor(javafx.scene.Cursor.HAND);
        
        // Lien pour les nouvelles inscriptions
        Hyperlink lienNouveauCompte = new Hyperlink("📝 Nouveau compte ? Contactez l'administrateur");
        lienNouveauCompte.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        lienNouveauCompte.setCursor(javafx.scene.Cursor.HAND);

        actionBox.getChildren().addAll(btnConnexion, lienMotDePasseOublie);

        // ============== ASSEMBLAGE ==============
        formBox.getChildren().addAll(emailBox, secretBox);
        
        loginCard.getChildren().addAll(
            headerBox,
            separator,
            formBox,
            lblStatus,
            actionBox,
            lienNouveauCompte
        );
        
        this.setCenter(loginCard);
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Crée un logo par défaut si l'image n'est pas disponible
     */
    private ImageView createDefaultLogo() {
        Label defaultLogo = new Label("UAM");
        defaultLogo.setFont(Font.font("System", FontWeight.BOLD, 36));
        defaultLogo.setStyle("-fx-text-fill: #005088; -fx-padding: 10;");
        
        ImageView placeholder = new ImageView();
        // On retourne un ImageView vide, on utilisera un label à la place
        return placeholder;
    }

    /**
     * Affiche un message de statut
     */
    public void setStatusMessage(String message, boolean isError) {
        lblStatus.setText(message);
        lblStatus.setVisible(true);
        if (isError) {
            lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #d32f2f; -fx-padding: 5 0 0 0;");
        } else {
            lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #388e3c; -fx-padding: 5 0 0 0;");
        }
    }

    /**
     * Efface le message de statut
     */
    public void clearStatusMessage() {
        lblStatus.setText("");
        lblStatus.setVisible(false);
    }

    /**
     * Efface tous les champs du formulaire
     */
    public void clearForm() {
        txtEmail.clear();
        txtSecret.clear();
        clearStatusMessage();
    }

    /**
     * Met à jour le label du champ secret
     */
    public void updateSecretLabel(String labelText, String promptText) {
        lblSecret.setText(labelText);
        txtSecret.setPromptText(promptText);
        txtSecret.clear();
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public TextField getTxtEmail() { 
        return txtEmail; 
    }
    
    public PasswordField getTxtSecret() { 
        return txtSecret; 
    }
    
    public Label getLblSecret() { 
        return lblSecret; 
    }
    
    public Button getBtnConnexion() { 
        return btnConnexion; 
    }
    
    public Label getLblStatus() { 
        return lblStatus; 
    }
    
    public Hyperlink getLienMotDePasseOublie() { 
        return lienMotDePasseOublie; 
    }

    // ==========================================
    // MÉTHODES POUR LES TESTS ET DÉBOGAGE
    // ==========================================

    @Override
    public String toString() {
        return "LoginView{" +
                "txtEmail=" + txtEmail.getText() +
                ", txtSecret=" + (txtSecret.getText() != null ? "****" : "null") +
                '}';
    }
}