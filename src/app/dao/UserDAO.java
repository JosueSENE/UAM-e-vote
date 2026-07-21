package app.dao;

import app.utils.DBConnection;
import app.model.User;
import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ==========================================
    // AUTHENTIFICATION
    // ==========================================

    public User authentificate(String emailSaisi, String passwordSaisi) {
        User u = searchUser(emailSaisi);
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

    // ==========================================
    // RECHERCHE
    // ==========================================

    public User searchUser(String email) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                     "LEFT JOIN departements d ON f.departement_id = d.id " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
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

    public User searchUserByLogin(String login) {
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                     "LEFT JOIN departements d ON f.departement_id = d.id " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.login = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'utilisateur par login: " + login);
            e.printStackTrace();
        }
        return null;
    }

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

    // ==========================================
    // GESTION DU MOT DE PASSE
    // ==========================================

    public boolean updatePassword(int userId, String password) {
        String sql = "UPDATE users SET password = ? WHERE id = ? AND role != 'ADMIN'";
        
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

    public boolean hasPassword(int userId) {
        String sql = "SELECT password FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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
    // CRUD
    // ==========================================

    public boolean addUser(User u) {
        String sql = "INSERT INTO users (code_permanent, nom, prenom, email, login, password, role, filiere_id, niveau, ufr_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, u.getCodePermanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getLogin());
            ps.setString(6, u.getPassword());
            ps.setString(7, u.getRole());
            
            if (u.getFiliereId() != null && u.getFiliereId() > 0) {
                ps.setInt(8, u.getFiliereId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            
            if (u.getNiveau() != null && !u.getNiveau().isEmpty()) {
                ps.setString(9, u.getNiveau());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }
            
            if (u.getUfrId() != null && u.getUfrId() > 0) {
                ps.setInt(10, u.getUfrId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        u.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Succès : Utilisateur " + u.getEmail() + " ajouté dans la base de données");
                return true;
            }   
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'utilisateur " + u.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                     "LEFT JOIN departements d ON f.departement_id = d.id " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.role != 'ADMIN' " +
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

    public List<User> getUsersByUfr(int ufrId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                     "LEFT JOIN departements d ON f.departement_id = d.id " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.ufr_id = ? AND u.role != 'ADMIN' " +
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

    public boolean updateUser(User u) {
        String sql = "UPDATE users SET code_permanent = ?, nom = ?, prenom = ?, email = ?, login = ?, " +
                     "role = ?, filiere_id = ?, niveau = ?, ufr_id = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, u.getCodePermanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getLogin());
            ps.setString(6, u.getRole());
            
            if (u.getFiliereId() != null && u.getFiliereId() > 0) {
                ps.setInt(7, u.getFiliereId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            
            if (u.getNiveau() != null && !u.getNiveau().isEmpty()) {
                ps.setString(8, u.getNiveau());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }
            
            if (u.getUfrId() != null && u.getUfrId() > 0) {
                ps.setInt(9, u.getUfrId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            
            ps.setInt(10, u.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Utilisateur " + u.getEmail() + " modifié dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'utilisateur " + u.getEmail());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ? AND role != 'ADMIN'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Utilisateur ID n° " + id + " supprimé de la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur ID " + id);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // MÉTHODES DE VÉRIFICATION
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

    public boolean codePermanentExists(int code) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE code_permanent = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean loginExists(String login) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE login = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==========================================
    // MÉTHODES DE RÉFÉRENTIEL
    // ==========================================

    public Integer getUfrIdByNom(String ufrNom) {
        String sql = "SELECT id FROM ufr WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ufrNom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUfrNomById(Integer ufrId) {
        if (ufrId == null) return null;
        String sql = "SELECT nom FROM ufr WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getFiliereIdByNom(String filiereNom) {
        String sql = "SELECT id FROM filieres WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filiereNom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFiliereNomById(Integer filiereId) {
        if (filiereId == null) return null;
        String sql = "SELECT nom FROM filieres WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getFilieresByUfrNom(String ufrNom) {
        List<String> filieres = new ArrayList<>();
        String sql = "SELECT f.nom FROM filieres f " +
                     "JOIN departements d ON f.departement_id = d.id " +
                     "JOIN ufr u ON d.ufr_id = u.id " +
                     "WHERE u.nom = ? ORDER BY f.nom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ufrNom);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filieres.add(rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filieres;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre total d'utilisateurs (exclut les admins)
     */
    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role != 'ADMIN'";
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
     * Récupère le nombre d'étudiants
     */
    public int getTotalEtudiants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ETUDIANT'";
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
     * Récupère le nombre d'enseignants
     */
    public int getTotalEnseignants() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ENSEIGNANT'"; 
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
     * Récupère le nombre d'étudiants par filière et niveau
     */
    public int getEtudiantsCountByFiliereAndNiveau(int filiereId, String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ETUDIANT' AND filiere_id = ? AND niveau = ?";
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
     * Récupère le nombre d'utilisateurs par UFR
     */
    public int getUsersCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE ufr_id = ? AND role != 'ADMIN'";
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
     * Récupère le nombre d'étudiants par niveau
     */
    public int getEtudiantsCountByNiveau(String niveau) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ETUDIANT' AND niveau = ?";
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
        u.setLogin(rs.getString("login"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        
        int filiereId = rs.getInt("filiere_id");
        if (!rs.wasNull()) {
            u.setFiliereId(filiereId);
        }
        
        u.setNiveau(rs.getString("niveau"));
        
        int ufrId = rs.getInt("ufr_id");
        if (!rs.wasNull()) {
            u.setUfrId(ufrId);
        }
        
        u.setFiliereNom(rs.getString("filiere_nom"));
        u.setDepartementNom(rs.getString("departement_nom"));
        u.setUfrNom(rs.getString("ufr_nom"));
        
        return u;
    }
}