package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Ajuste l'adresse, le port (ici 3306 par défaut) et les identifiants si nécessaire
    private static final String URL = "jdbc:mysql://localhost:3306/uam_evote?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; // Laisse vide ou mets ton mot de passe MySQL

    public static Connection getConnection() throws SQLException {
        try {
            // Force le chargement du pilote JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable : " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}