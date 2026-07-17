package app.dao;

import app.utils.DBConnection;
import app.model.User;

import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //============ FONCTIONS UTILISÉES DANS L'AUTHENTIFICATION ====================

    // AUTHENTIFICATION SANS RÉPÉTITION :Utilise directement searchUser(email) pour récupérer l'utilisateur et effectue les vérifications.
    
    public User authentificate(String emailSaisi, String passwordSaisi) {
        User u = searchUser(emailSaisi);
        if (u == null) {
            return null;
        }
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (u.getCode_permanent() == codeSaisiInt) {
                    System.out.println("Première connexion validée. En attente de création du mot de passe.");
                    return u; 
                }
            } catch (NumberFormatException e) {
                System.err.println("Erreur du format :");
                e.printStackTrace();
            }
        } 
        // CAS B : Connexion classique (Le mot de passe est déjà configuré)
        else {
            String hashSaisi = PasswordHasher.hashSHA256(passwordSaisi);
            if (u.getPassword().equals(hashSaisi)) {
                System.out.println("Connexion classique validée avec succès.");
                return u;
            }
        }
        return null;
    }

    //  RECHERCHER UN UTILISATEUR PAR SON email (READ)

    public User searchUser (String email){
        String sql = "SELECT * FROM users WHERE email= ?";
        try  (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setCode_permanent(rs.getInt("code_permanent"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password"));
                    u.setProfession(rs.getString("profession"));
                    u.setFiliere_id(rs.getInt("filiere_id"));
                    u.setNiveau(rs.getString("niveau"));                        
                    return u;
                }
            }          
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur "+ email);
            e.printStackTrace();
        }
        return null;
    }

    //  METTRE À JOUR LE MOT DE PASSE (UPDATE)

    public boolean updatePassword (int userId, String password) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, userId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe pour l'ID: " + userId);
            e.printStackTrace();
            return false;
        }
    }
    

    // ===================== CRUD  ==============================

    //  AJOUTER UN UTILISATUR (CREATE)

    public void addUser(User u) throws SQLException{
        String sql = "INSERT INTO users (code_permanent ,nom, prenom, email, profession, filiere_id, niveau)"
        +"VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, u.getCode_permanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            ps.setInt(6,u.getFiliere_id());
            // Gestion des champs optionnels qui peuvent etre NULL
            if (u.getNiveau() != null){ps.setString(7, u.getNiveau());}
            else {ps.setNull(7,  Types.VARCHAR);}

            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion de l'utilisateur "+u.getEmail());
            else System.err.println("Succés : Utilisateur "+u.getEmail()+" ajouter dans la base de données");   
        }catch(SQLException e){
            System.err.println("Erreur lors de l'insertion de l'utilisateur "+u.getEmail());
            e.printStackTrace();
        }
    }

    //  RECUPERER TOUS LES UTILISATEURS (READ)

    public List<User> getAllUsers() throws SQLException{
        List<User> liste = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try ( Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setCode_permanent(rs.getInt("code_permanent"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setProfession(rs.getString("profession"));
                u.setFiliere_id(rs.getInt("filiere_id"));
                u.setNiveau(rs.getString("niveau"));
                liste.add(u);
            } 
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des utilisateurs : ");
            e.printStackTrace();}
        return liste;
    }

    //  MODIFIER UN UTILISATEUR (UPDATE)

    public void updateUser (User u) throws SQLException{
        String sql ="UPDATE users SET code_permanent=?, nom=?, prenom=?, email=?, profession=?, filiere_id=?, niveau=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, u.getCode_permanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            ps.setInt(6,u.getFiliere_id());
            // Gestion des champs optionnels qui peuvent etre NULL
            if (u.getNiveau() != null){ps.setString(7, u.getNiveau());}
            else {ps.setNull(7,  Types.VARCHAR);}
            ps.setInt(8, u.getId());
            
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour de l'utilisateur "+u.getEmail());
            else System.err.println("Succés : Utilisateur "+u.getEmail()+" modifié dans la base de données");

        }catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur "+u.getEmail());
            e.printStackTrace();}
    } 

    //  SUPPRIMER UN UTILISATEUR (DELETE)

    public void deleteUser(int code_permanent) throws SQLException {
        String sql = "DELETE FROM users WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code_permanent);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression de l'utilisateur "+code_permanent);
            else System.err.println("Succes: Utilisateur supprimé dans la base de données ");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur "+code_permanent);
            e.printStackTrace();
        }
    }
    
}
