package app;

import java.sql.Connection;
import java.sql.SQLException;

import app.controller.LoginController;
import app.utils.DBConnection;
import app.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Instancier la VUE (Purement graphique)
            LoginView loginView = new LoginView();
            
            // 2. Instancier le CONTRÔLEUR en lui injectant la vue
            new LoginController(loginView);
            
            // 3. Associer la vue à la scène
            Scene scene = new Scene(loginView, 900, 600);
            
            // 4. Configurer le Stage
            primaryStage.setTitle("UAM e-Vote - Portail d'Authentification");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Erreur de lancement MVC : " + e.getMessage());
            e.printStackTrace();
        }
    }
                                                                                                                                         //ndiaye.pape@uam.edu.sn      AdminSecure26 
    public static void main(String[] args) {
        System.out.println("============= Démarrage de UAM e-Vote =============\n");
        System.out.println("Vérification de la connexion à la base de données...");

        try (Connection conn = DBConnection.getConnection()) { // Tente d'ouvrir et ferme automatiquement
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion à la base de données réussie !");
                // La connexion est bonne, on lance l'application JavaFX
                launch(args);
            } else {
                System.err.println("❌ Échec : La connexion retournée est nulle ou fermée.");
                System.exit(1); // Arrête le programme
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur critique : Impossible de joindre la base de données.");
            System.err.println("Détails de l'erreur : " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Arrête le programme pour éviter les crashs en cascade dans l'IHM
        }
    }

}