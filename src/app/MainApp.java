package app;

import java.sql.Connection;

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
            Scene scene = new Scene(loginView, 850, 600);
            
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

    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        launch(args);
    }
}