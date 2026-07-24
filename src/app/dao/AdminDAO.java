package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Admin;
import app.utils.DBConnection;
import app.utils.PasswordHasher;

public class AdminDAO {

    // =====================
    // AUTHENTIFICATION
    // =====================
    
    public Admin authentificate(String emailSaisi, String passwordSaisi) {
        Admin a = searchAdminByEmail(emailSaisi);
        if (a == null) {
            return null;
        }
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (a.getPassword() == null || a.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (a.getCodePermanent() == codeSaisiInt) {
                    System.out.println("✅ Première connexion validée. En attente de création du mot de passe.");
                    return a; 
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur du format du code permanent.");
            }
        } 
        // CAS B : Connexion classique (Le mot de passe est déjà configuré)
        else {
            String hashSaisi = PasswordHasher.hashSHA256(passwordSaisi);
            if (a.getPassword().equals(hashSaisi)) {
                System.out.println("✅ Connexion classique validée avec succès.");
                return a;
            }
        }
        return null;
    }

    // ==========================================
    // RECHERCHE
    // ==========================================

    public Admin searchAdminByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setCodePermanent(rs.getInt("code_permanent"));
                    a.setNom(rs.getString("nom"));
                    a.setPrenom(rs.getString("prenom"));
                    a.setEmail(rs.getString("email"));  
                    a.setPassword(rs.getString("password"));                      
                    return a;
                }
            }          
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'administrateur: " + email);
            e.printStackTrace();
        }
        return null;
    }

    public Admin searchAdminById(int id) {
        String sql = "SELECT * FROM admin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setCodePermanent(rs.getInt("code_permanent"));
                    a.setNom(rs.getString("nom"));
                    a.setPrenom(rs.getString("prenom"));
                    a.setEmail(rs.getString("email"));  
                    a.setPassword(rs.getString("password"));                      
                    return a;
                }
            }          
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'administrateur par l'ID : " + id);
            e.printStackTrace();
        }
        return null;
    }

    public Admin searchAdminByCodePermanent(String codePermananet) {
        String sql = "SELECT * FROM admin WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setString(1, codePermananet);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setCodePermanent(rs.getInt("code_permanent"));
                    a.setNom(rs.getString("nom"));
                    a.setPrenom(rs.getString("prenom"));
                    a.setEmail(rs.getString("email"));  
                    a.setPassword(rs.getString("password"));                      
                    return a;
                }
            }          
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'administrateur par le code permanent : " + codePermananet);
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // GESTION DU MOT DE PASSE
    // ==========================================


    public boolean updatePassword(int adminId, String password) {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, adminId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du mot de passe pour l'id: " + adminId);
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasPassword(int id) {
        String sql = "SELECT password FROM admin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    return password != null && !password.trim().isEmpty();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //=============================================================
    // AJOUTER UN ADMINISTRATEUR (Retourne true si l'ajout a réussi)
    //=============================================================

    public boolean addAdmin(Admin a) {
        String sql = "INSERT INTO admin (code_permanent, nom, prenom, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getCodePermanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());

            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Administrateur " + a.getEmail() + " ajouté.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'administrateur: " + a.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    //=============================================================
    // RECUPERER TOUS LES ADMINISTRATEURS
    //=============================================================

    public List<Admin> getAllAdmins() {
        List<Admin> liste = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = pd.executeQuery()) {
            while (rs.next()) {
                Admin a = new Admin();
                a.setId(rs.getInt("id"));
                a.setCodePermanent(rs.getInt("code_permanent"));
                a.setNom(rs.getString("nom"));
                a.setPrenom(rs.getString("prenom"));
                a.setEmail(rs.getString("email"));
                a.setPassword(rs.getString("password")); 
                liste.add(a);
            } 
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des administrateurs");
            e.printStackTrace();
        }
        return liste;
    }

    //=============================================================
    // MODIFIER UN ADMINISTRATEUR (Retourne true si la modification a réussi)
    //=============================================================

    public boolean updateAdmin(Admin a) {
        String sql = "UPDATE admin SET code_permanent = ?, nom = ?, prenom = ?, email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getCodePermanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());
            ps.setInt(5, a.getId()); // Note : c'est le 5ème paramètre, ton ancien code avait un '8' qui provoquait un bug !
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Données de l'administrateur n° " + a.getId() + " modifiées.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'administrateur: " + a.getEmail());
            e.printStackTrace();
        }
        return false;
    } 

    // ==========================================
    // MÉTHODES DE SUPPRESSION
    // ==========================================

    public boolean deleteAdminById(int Id) {
        String sql = "DELETE FROM admin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Id);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : L'administrateur n° " + Id + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'administrateur au code: " + Id);
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAdminByEmail(String email) {
        String sql = "DELETE FROM admin WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : L'administrateur d'email " + email + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'administrateur d'email : " + email);
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAdminByCodePermanent(int code_permanent) {
        String sql = "DELETE FROM admin WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code_permanent);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : L'administrateur au code " + code_permanent + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'administrateur au code: " + code_permanent);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    public int getTotalAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM admin";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) { 
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ==========================================
    // MÉTHODES DE VÉRIFICATION
    // ==========================================

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM admin WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean codePermanentExists(int code) throws SQLException {
        String sql = "SELECT 1 FROM admin WHERE code_permanent = ? AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    
}