package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Admin;
import app.utils.DBConnection;
import app.utils.PasswordHasher;

public class AdminDAO {

    //============ FONCTIONS UTILISÉES DANS L'AUTHENTIFICATION ====================

    // AUTHENTIFICATION SANS RÉPÉTITION :Utilise directement searchUser(email) pour récupérer l'utilisateur et effectue les vérifications.
    
    public Admin authentificate(String emailSaisi, String passwordSaisi) {
        Admin a = searchAdmin(emailSaisi);
        if (a == null) {
            return null;
        }
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (a.getPassword() == null || a.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (a.getCode_permanent() == codeSaisiInt) {
                    System.out.println("Première connexion validée. En attente de création du mot de passe.");
                    return a; 
                }
            } catch (NumberFormatException e) {
                System.err.println("Erreur du format :");
                e.printStackTrace();
            }
        } 
        // CAS B : Connexion classique (Le mot de passe est déjà configuré)
        else {
            String hashSaisi = PasswordHasher.hashSHA256(passwordSaisi);
            if (a.getPassword().equals(hashSaisi)) {
                System.out.println("Connexion classique validée avec succès.");
                return a;
            }
        }
        return null;
    }

    //  RECHERCHER UN ADMIN PAR SON email (READ)

    public Admin searchAdmin (String email){
        String sql = "SELECT * FROM admin WHERE email= ?";
        try  (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setCode_permanent(rs.getInt("code_permanent"));
                    a.setNom(rs.getString("nom"));
                    a.setPrenom(rs.getString("prenom"));
                    a.setEmail(rs.getString("email"));                        
                    return a;
                }
            }          
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'administrateur "+ email);
            e.printStackTrace();
        }
        return null;
    }

    //  METTRE À JOUR LE MOT DE PASSE (UPDATE)

    public boolean updatePassword (int adminId, String password) {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, adminId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe pour l'administrateur: " + adminId);
            e.printStackTrace();
            return false;
        }
    }

    // ===================== CRUD  ==============================

    //  AJOUTER UN ADMINISTRATEUR (CREATE)

    public void addAdmin (Admin a) throws SQLException{
        String sql = "INSERT INTO admin (code_permanent ,nom, prenom, email) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, a.getCode_permanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());

            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion de l'administrateur "+a.getEmail());
            else System.err.println("Succés : Administrateur "+a.getEmail()+" ajouté dans la base de données");
            
        }catch(SQLException e){
            System.err.println("Erreur lors de l'insertion de l'administrateur "+a.getEmail());
            e.printStackTrace();
        }
    }

    //  RECUPERER TOUS LES ADMINISTRATEURS (READ)

    public List<Admin> getAllAdmins() throws SQLException{
        List<Admin> liste = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY id DESC";
        try ( Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Admin a = new Admin();
                a.setId(rs.getInt("id"));
                a.setCode_permanent(rs.getInt("code_permanent"));
                a.setNom(rs.getString("nom"));
                a.setPrenom(rs.getString("prenom"));
                a.setEmail(rs.getString("email"));
                liste.add(a);
            } 
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des administrateurs ");
            e.printStackTrace();
        }
        return liste;
    }

        //  MODIFIER UN ADMINISTRATEUR (UPDATE)

    public void updateAdmin (Admin a) throws SQLException{
        String sql ="UPDATE admin SET code_permanent=?, nom=?, prenom=?, email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, a.getCode_permanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());
            ps.setInt(8, a.getId());
            
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour de l'administrateur n° "+a.getId());
            else System.err.println("Succés : Les données de l''administrateur n° "+a.getId()+" sont modifiées dans la base de données");

        }catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'administrateur "+a.getEmail());
            e.printStackTrace();
        }
    } 


    //  SUPPRIMER UN ADMINISTRATEUR (DELETE)

    public void deleteAdmin(int code_permanent) throws SQLException {
        String sql = "DELETE FROM admin WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code_permanent);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression");
            else System.err.println("Succes: L'administrateur possedant le code permanent :"+code_permanent+" est supprimé dans la base de données ");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'administrateur :(code permananent :"+code_permanent+")");
            e.printStackTrace();
        }
    }
    
}
