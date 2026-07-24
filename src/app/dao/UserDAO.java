package app.dao;

import app.utils.DBConnection;
import app.model.User;
import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //============================================
    // AUTHENTIFICATION SANS RÉPÉTITION
    //============================================

    public User authentificate(String emailSaisi, String passwordSaisi) {
        User u = searchUserByEmail(emailSaisi);
        if (u == null) {
            return null;
        }
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (u.getCodePermanent() == codeSaisiInt) {
                    System.out.println("✅ Première connexion validée. En attente de création du mot de passe.");
                    return u; 
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur du format du code permanent lors de l'authentification");
                e.printStackTrace();
            }
        } 
        // CAS B : Connexion classique (Le mot de passe est déjà configuré)
        else {
            String hashSaisi = PasswordHasher.hashSHA256(passwordSaisi);
            if (u.getPassword().equals(hashSaisi)) {
                System.out.println("✅ Connexion classique validée avec succès.");
                return u;
            }
        }
        return null;
    }

    //============================================
    // RECHERCHER UN UTILISATEUR PAR SON EMAIL
    //============================================

    public User searchUserByEmail(String email) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON d.ufr_id = uf.id " +
                    "WHERE u.email = ? AND u.role != 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }          
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'utilisateur " + email);
            e.printStackTrace();
        }
        return null;
    }

    //============================================
    // RECHERCHER UN UTILISATEUR PAR SON ID
    //============================================

    public User searchUserById(int id) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'utilisateur par ID: " + id);
            e.printStackTrace();
        }
        return null;
    }

    //============================================
    // RECHERCHER UN UTILISATEUR PAR SON NOM
    //============================================

    public User searchUserByName(String nom) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.nom = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'utilisateur " + nom);
            e.printStackTrace();
        }
        return null;
    }

    //==================================================
    // RECHERCHER UN UTILISATEUR PAR SON CODE PERMANENT
    //==================================================

    public User searchUserByCodePermanent(int codePermanent) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codePermanent);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'utilisateur par code permanent: " + codePermanent);
            e.printStackTrace();
        }
        return null;
    }

    //==================================================
    // METTRE À JOUR LE MOT DE PASSE (UPDATE)
    //==================================================

    public boolean updatePassword(int userId, String password) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, userId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du mot de passe pour l'ID: " + userId);
            e.printStackTrace();
            return false;
        }
    }
    
    //==================================================
    // AJOUTER UN UTILISATEUR
    //==================================================

    public boolean addUser(User u) {
        // Correction de l'espace de concaténation avant VALUES
        String sql = "INSERT INTO users (code_permanent, nom, prenom, email, profession, filiere_id, niveau) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";      
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getCodePermanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            if (u.getFiliereId() > 0) { ps.setInt(6, u.getFiliereId()); } 
            else {ps.setNull(6, Types.INTEGER); }
            if (u.getNiveau() != null) { ps.setString(7, u.getNiveau()); }
            else { ps.setNull(7, Types.VARCHAR); }
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Utilisateur " + u.getEmail() + " ajouté dans la base de données");
                return true;
            }   
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'utilisateur " + u.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    //==================================================
    // RÉCUPÉRER TOUS LES UTILISATEURS
    //==================================================

    public List<User> getAllUsers() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "ORDER BY u.id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                liste.add(u);
            } 
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES UTILISATEURS PAR UFR
    //==================================================

    public List<User> getUsersByUfr(int ufrId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.ufr_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs par UFR");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES UTILISATEURS PAR DÉPARTEMENT
    //==================================================

    public List<User> getUsersByDepartement(int departementId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.departement_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs par UFR");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES UTILISATEURS PAR FILIÈRE
    //==================================================

    public List<User> getUsersByFiliere(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.filiere_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des utilisateurs par UFR");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER TOUS LES ETUDIANTS
    //==================================================

    public List<User> getAllEtudiants() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.role = 'ETUDIANT' " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                liste.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR NIVEAU ET FILIÈRE
    //==================================================

    public List<User> getEtudiantsByFiliereAndNiveau(int filiereId, String niveau) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.role = 'ETUDIANT' AND u.filiere_id = ? AND u.niveau = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par filière et niveau");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR NIVEAU ET DÉPARTEMENT
    //==================================================

    public List<User> getEtudiantsByDepartementAndNiveau(int departementId, String niveau) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.role = 'ETUDIANT' AND u.departement_id = ? AND u.niveau = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par département et niveau");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR NIVEAU ET UFR
    //==================================================

    public List<User> getEtudiantsByUfrAndNiveau(int ufrId, String niveau) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.role = 'ETUDIANT' AND u.ufr_id = ? AND u.niveau = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par ufr et niveau");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR NIVEAU
    //==================================================

    public List<User> getEtudiantsByNiveau(String niveau) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ETUDIANT' AND u.niveau = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par niveau");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR FILIÈRE
    //==================================================

    public List<User> getEtudiantsByFiliere(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ETUDIANT' AND u.filiere_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par filière");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR DEPARTEMENT
    //==================================================

    public List<User> getEtudiantsByDepartement(int departementId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ETUDIANT' AND u.departement_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par département");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ETUDIANTS PAR UFR
    //==================================================

    public List<User> getEtudiantsByUfr(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ETUDIANT' AND u.ufr_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des étudiants par ufr");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER TOUS LES ENSEIGNANTS
    //==================================================

    public List<User> getAllEnseignants() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.role = 'ENSEIGNANT' " +
                    "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                liste.add(u);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ENSEIGNANTS PAR FILIÈRE
    //==================================================

    public List<User> getEnseignantsByFiliere(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ENSEIGNANT' AND u.filiere_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par filière ");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ENSEIGNANTS PAR DEPARTEMENT
    //==================================================

    public List<User> getEnseignantsByDepartement(int departementId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ENSEIGNANT' AND u.departement_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par dddépartement");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // RÉCUPÉRER LES ENSEIGNANTS PAR UFR
    //==================================================

    public List<User> getEnseignantsByUfr(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.profession = 'ENSEIGNANT' AND u.ufr_id = ? " +
                    "ORDER BY u.nom, u.prenom";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    liste.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par ufr");
            e.printStackTrace();
        }
        return liste;
    }

    //==================================================
    // MODIFIER UN UTILISATEUR (UPDATE)
    //==================================================

    public boolean updateUser(User u) {
        String sql = "UPDATE users SET code_permanent = ?, nom = ?, prenom = ?, email = ?, profession = ?, filiere_id = ?, niveau = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getCodePermanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            if (u.getFiliereId() > 0) {ps.setInt(6, u.getFiliereId());} 
            else {ps.setNull(6, Types.INTEGER);}
            if (u.getNiveau() != null) { ps.setString(7, u.getNiveau()); }
            else { ps.setNull(7, Types.VARCHAR); }
            ps.setInt(8, u.getId());
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Utilisateur " + u.getEmail() + " modifié dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur " + u.getEmail());
            e.printStackTrace();
        }
        return false;
    } 

   // SUPPRIMER UN UTILISATEUR (DELETE via id)
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Utilisateur ID n° " + id + " supprimé de la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur ID " + id);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    public boolean emailExists(String email) throws SQLException { 
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean codePermanentExists(long code) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre total d'utilisateurs 
     */
    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users ";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {  
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par UFR
     */
    public int getUsersCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par département
     */
    public int getUsersCountByDepartement(int departementId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE departement_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par filière
     */
    public int getUsersCountByFiliere(int filiereId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par niveau
     */
    public int getUsersCountByNiveau(String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE niveau = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par UFR et niveau
     */
    public int getUsersCountByUfrAndNiveau(int ufrId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par département et niveau
     */
    public int getUsersCountByDepartementAndNiveau(int departementId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'utilisateurs par filière et niveau
     */
    public int getUsersCountByFiliereAndNiveau(int filiereId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ? AND niveau = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiants
     */
    public int getTotalEtudiants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE profession = 'ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiants par UFR
     */
    public int getEtudiantCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND profession = 'ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiants par département
     */
    public int getEtudiantCountByDepartement(int departementId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE departement_id = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiants par filière
     */
    public int getEtudiantCountByFiliere(int filiereId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiant par niveau
     */
    public int getEtudiantCountByNiveau(String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE niveau = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiant par UFR et niveau
     */
    public int getEtudiantCountByUfrAndNiveau(int ufrId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiant par département et niveau
     */
    public int getEtudiantCountByDepartementAndNiveau(int departementId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'étudiant par filière et niveau
     */
    public int getEtudiantCountByFiliereAndNiveau(int filiereId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ? AND niveau = ? AND profession='ETUDIANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    /**
     * Récupère le nombre d'enseignants
     */
    public int getTotalEnseignants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE profession = 'ENSEIGNANT'"; 
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par UFR
     */
    public int getEnseignantCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND profession = 'ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par département
     */
    public int getEnseignantCountByDepartement(int departementId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE departement_id = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par filière
     */
    public int getEnseignantCountByFiliere(int filiereId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par niveau
     */
    public int getEnseignantCountByNiveau(String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE niveau = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par UFR et niveau
     */
    public int getEnseignantCountByUfrAndNiveau(int ufrId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par département et niveau
     */
    public int getEnseignantCountByDepartementAndNiveau(int departementId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND niveau = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'enseignant par filière et niveau
     */
    public int getEnseignantCountByFiliereAndNiveau(int filiereId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE filiere_id = ? AND niveau = ? AND profession='ENSEIGNANT'";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            ps.setString(2, niveau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ==========================================
    // MAPPING
    // ==========================================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setCodePermanent(rs.getInt("code_permanent"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setProfession(rs.getString("profession"));
        int filiereId = rs.getInt("filiere_id");
        if (!rs.wasNull()) {
            u.setFiliereId(filiereId);
        }
        return u;
    }
}
