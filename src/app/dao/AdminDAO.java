package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Admin;
import app.model.User;
import app.utils.DBConnection;
import app.utils.PasswordHasher;

public class AdminDAO {

    // ==========================================
    // AUTHENTIFICATION
    // ==========================================

    /**
     * Authentifie un administrateur
     * Les admins sont dans la table users avec role = 'ADMIN'
     */
    public Admin authentificate(String emailSaisi, String passwordSaisi) {
        Admin a = searchAdmin(emailSaisi);
        if (a == null) {
            return null;
        }
        
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (a.getPassword() == null || a.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (a.getCodePermanent() == codeSaisiInt) {
                    System.out.println("✅ Première connexion admin validée. En attente de création du mot de passe.");
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
                System.out.println("✅ Connexion admin classique validée avec succès.");
                return a;
            }
        }
        return null;
    }

    // ==========================================
    // RECHERCHE
    // ==========================================

    /**
     * Recherche un administrateur par son email dans la table users
     */
    public Admin searchAdmin(String email) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.email = ? AND u.role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }          
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'administrateur: " + email);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un administrateur par son ID
     */
    public Admin getAdminById(int id) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.id = ? AND u.role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'admin par ID: " + id);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un administrateur par son code permanent
     */
    public Admin getAdminByCodePermanent(int codePermanent) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.code_permanent = ? AND u.role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codePermanent);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'admin par code permanent: " + codePermanent);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un administrateur par son login
     */
    public Admin getAdminByLogin(String login) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.login = ? AND u.role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'admin par login: " + login);
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // GESTION DU MOT DE PASSE
    // ==========================================

    /**
     * Met à jour le mot de passe d'un administrateur
     */
    public boolean updatePassword(int adminId, String password) {
        String sql = "UPDATE users SET password = ? WHERE id = ? AND role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, adminId);        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du mot de passe pour l'admin id: " + adminId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si un administrateur a déjà un mot de passe
     */
    public boolean hasPassword(int adminId) {
        String sql = "SELECT password FROM users WHERE id = ? AND role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
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

    // ==========================================
    // CRUD ADMINISTRATEURS
    // ==========================================

    /**
     * Ajoute un administrateur dans la table users avec role = 'ADMIN'
     */
    public boolean addAdmin(Admin a) {
        String sql = "INSERT INTO users (code_permanent, nom, prenom, email, login, password, role, ufr_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'ADMIN', ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, a.getCodePermanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());
            
            // Générer un login automatique si non fourni
            if (a.getLogin() == null || a.getLogin().trim().isEmpty()) {
                a.setLogin(generateLogin(a.getNom(), a.getPrenom()));
            }
            ps.setString(5, a.getLogin());
            
            // Mot de passe null pour première connexion
            ps.setString(6, null);
            
            // Gestion de l'UFR (peut être null)
            if (a.getUfrId() != null && a.getUfrId() > 0) {
                ps.setInt(7, a.getUfrId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        a.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Succès : Administrateur " + a.getEmail() + " ajouté.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'administrateur: " + a.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Récupère tous les administrateurs
     */
    public List<Admin> getAllAdmins() {
        List<Admin> liste = new ArrayList<>();
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.role = 'ADMIN' " +
                     "ORDER BY u.id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Admin a = mapResultSetToAdmin(rs);
                liste.add(a);
            } 
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des administrateurs");
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère les administrateurs d'un UFR spécifique
     */
    public List<Admin> getAdminsByUfr(int ufrId) {
        List<Admin> liste = new ArrayList<>();
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.role = 'ADMIN' AND u.ufr_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Admin a = mapResultSetToAdmin(rs);
                    liste.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des admins par UFR");
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Met à jour un administrateur
     */
    public boolean updateAdmin(Admin a) {
        String sql = "UPDATE users SET code_permanent = ?, nom = ?, prenom = ?, email = ?, login = ?, ufr_id = ? " +
                     "WHERE id = ? AND role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, a.getCodePermanent());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setString(4, a.getEmail());
            ps.setString(5, a.getLogin());
            
            if (a.getUfrId() != null && a.getUfrId() > 0) {
                ps.setInt(6, a.getUfrId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, a.getId());
            
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

    /**
     * Supprime un administrateur par son ID
     */
    public boolean deleteAdmin(int adminId) {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : L'administrateur ID " + adminId + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'administrateur ID: " + adminId);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime un administrateur par son code permanent
     */
    public boolean deleteAdminByCodePermanent(int codePermanent) {
        String sql = "DELETE FROM users WHERE code_permanent = ? AND role = 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codePermanent);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : L'administrateur au code " + codePermanent + " est supprimé.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'administrateur au code: " + codePermanent);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre total d'administrateurs
     */
    public int getTotalAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        
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
     * Récupère le nombre d'administrateurs par UFR
     */
    public int getAdminsCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN' AND ufr_id = ?";
        
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

    // ==========================================
    // MÉTHODES DE VÉRIFICATION
    // ==========================================

    /**
     * Vérifie si un email existe déjà pour un administrateur
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Vérifie si un code permanent existe déjà pour un administrateur
     */
    public boolean codePermanentExists(int code) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE code_permanent = ? AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Vérifie si un login existe déjà pour un administrateur
     */
    public boolean loginExists(String login) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE login = ? AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==========================================
    // MÉTHODES DE CONVERSION
    // ==========================================

    /**
     * Convertit un User en Admin
     */
    public Admin convertUserToAdmin(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return null;
        }
        
        Admin admin = new Admin();
        admin.setId(user.getId());
        admin.setCodePermanent(user.getCodePermanent());
        admin.setNom(user.getNom());
        admin.setPrenom(user.getPrenom());
        admin.setEmail(user.getEmail());
        admin.setLogin(user.getLogin());
        admin.setPassword(user.getPassword());
        admin.setRole(user.getRole());
        admin.setUfrId(user.getUfrId());
        admin.setUfrNom(user.getUfrNom());
        
        return admin;
    }

    /**
     * Convertit un Admin en User
     */
    public User convertAdminToUser(Admin admin) {
        if (admin == null) {
            return null;
        }
        
        User user = new User();
        user.setId(admin.getId());
        user.setCodePermanent(admin.getCodePermanent());
        user.setNom(admin.getNom());
        user.setPrenom(admin.getPrenom());
        user.setEmail(admin.getEmail());
        user.setLogin(admin.getLogin());
        user.setPassword(admin.getPassword());
        user.setRole("ADMIN");
        user.setUfrId(admin.getUfrId());
        user.setUfrNom(admin.getUfrNom());
        
        return user;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Génère un login à partir du nom et prénom
     */
    private String generateLogin(String nom, String prenom) {
        String base = (nom + "." + prenom).toLowerCase();
        // Supprimer les accents et caractères spéciaux
        base = base.replaceAll("[éèêë]", "e")
                   .replaceAll("[àâä]", "a")
                   .replaceAll("[îï]", "i")
                   .replaceAll("[ôö]", "o")
                   .replaceAll("[ùûü]", "u")
                   .replaceAll("[ç]", "c")
                   .replaceAll("[^a-z.]", "");
        
        // Vérifier si le login existe déjà, si oui ajouter un numéro
        String login = base;
        int counter = 1;
        try {
            while (loginExists(login)) {
                login = base + counter;
                counter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return login;
    }

    // ==========================================
    // MAPPING
    // ==========================================

    /**
     * Convertit un ResultSet en objet Admin
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setCodePermanent(rs.getInt("code_permanent"));
        a.setNom(rs.getString("nom"));
        a.setPrenom(rs.getString("prenom"));
        a.setEmail(rs.getString("email"));
        a.setLogin(rs.getString("login"));
        a.setPassword(rs.getString("password"));
        a.setRole(rs.getString("role"));
        
        // Gestion de l'UFR
        int ufrId = rs.getInt("ufr_id");
        if (!rs.wasNull()) {
            a.setUfrId(ufrId);
        }
        a.setUfrNom(rs.getString("ufr_nom"));
        
        return a;
    }
}