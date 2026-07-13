package app.utils;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection cn = DBConnection.getConnection()){
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Chargement pilote réussi");
            System.out.println("Connexion OK : "+ cn.getCatalog());
        } catch (Exception e){
            System.out.println("Connexion échouée : "+e.getMessage());
        }
    }
}
