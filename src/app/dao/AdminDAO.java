package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Admin;
import app.utils.DBConnection;
import app.utils.PasswordHasher;

public class AdminDAO {

    //============ FONCTIONS UTILISÉES DANS L'AUTHENTIFICATION ====================
    
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
                System.err.println("Erreur du format du code permanent.");
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

    public Admin searchAdmin(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setCode_permanent(rs.getInt("code_permanent"));
                    a.setNom(rs.getString("nom"));
                    a.setPrenom(rs.getString("prenom"));
                    a.setEmail(rs.getString("email"));  
                    a.setPassword(rs.getString("password"));                      
                    return a;
                }
            }          
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'administrateur: " + email);
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int adminId, String password) {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, adminId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe pour l'id: " + adminId);
            e.printStackTrace();
            return false;
        }
    }

    // ===================== CRUD  ==============================

    // AJOUTER UN ADMINISTRATEUR (Retourne true si l'ajout a réussi)

    public boolean addAdmin(Admin a) {
        String sql = "INSERT INTO admin (code_permanent, nom, prenom, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getCode_permanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());

            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Administrateur " + a.getEmail() + " ajouté.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'administrateur: " + a.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    // RECUPERER TOUS LES ADMINISTRATEURS

    public List<Admin> getAllAdmins() {
        List<Admin> liste = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Admin a = new Admin();
                a.setId(rs.getInt("id"));
                a.setCode_permanent(rs.getInt("code_permanent"));
                a.setNom(rs.getString("nom"));
                a.setPrenom(rs.getString("prenom"));
                a.setEmail(rs.getString("email"));
                a.setPassword(rs.getString("password")); 
                liste.add(a);
            } 
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des administrateurs");
            e.printStackTrace();
        }
        return liste;
    }

    // MODIFIER UN ADMINISTRATEUR (Retourne true si la modification a réussi)

    public boolean updateAdmin(Admin a) {
        String sql = "UPDATE admin SET code_permanent = ?, nom = ?, prenom = ?, email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getCode_permanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());
            ps.setInt(5, a.getId()); // Note : c'est le 5ème paramètre, ton ancien code avait un '8' qui provoquait un bug !
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Données de l'administrateur n° " + a.getId() + " modifiées.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'administrateur: " + a.getEmail());
            e.printStackTrace();
        }
        return false;
    } 

    // SUPPRIMER UN ADMINISTRATEUR (Retourne true si la suppression a réussi)

    public boolean deleteAdmin(int code_permanent) {
        String sql = "DELETE FROM admin WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code_permanent);
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : L'administrateur au code " + code_permanent + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'administrateur au code: " + code_permanent);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre d'administrateurs
     */
    public int getTotalAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM admin";
        
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) { 
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}