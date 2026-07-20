package app.dao;

import app.utils.DBConnection;
import app.model.User;
import app.model.Filiere;
import app.utils.PasswordHasher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private static final String TABLE_USERS = "users";
    private static final String TABLE_ADMINS = "admin";

    // ==========================================
    // AUTHENTIFICATION
    // ==========================================

    public Optional<User> authentifierUser(String email, String password) throws SQLException {  // ✅ throws SQLException
        User u = searchUserByEmail(email);
        if (u == null) {
            return Optional.empty();
        }

        if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            try {
                long codeSaisi = Long.parseLong(password);
                if (u.getCode_permanent() == codeSaisi) {
                    System.out.println("✅ Première connexion validée pour : " + email);
                    return Optional.of(u);
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Format de code permanent invalide pour : " + email);
            }
            return Optional.empty();
        }

        String hashSaisi = PasswordHasher.hashSHA256(password);
        if (u.getPassword().equals(hashSaisi)) {
            System.out.println("✅ Connexion classique validée pour : " + email);
            return Optional.of(u);
        }

        return Optional.empty();
    }

    public Optional<User> authentifierAdmin(String email, String password) throws SQLException {  // ✅ throws SQLException
        User admin = searchAdminByEmail(email);
        
        if (admin != null) {
            String hashSaisi = PasswordHasher.hashSHA256(password);
            if (admin.getPassword().equals(hashSaisi)) {
                System.out.println("✅ Admin authentifié : " + email);
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    // ==========================================
    // RECHERCHE
    // ==========================================

    public User searchUserByEmail(String email) throws SQLException {  // ✅ throws SQLException
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public User searchAdminByEmail(String email) throws SQLException {  // ✅ throws SQLException
        String sql = "SELECT * FROM admin WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User admin = new User();
                    admin.setId(rs.getInt("id"));
                    admin.setCode_permanent(rs.getLong("code_permanent"));
                    admin.setNom(rs.getString("nom"));
                    admin.setPrenom(rs.getString("prenom"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password"));
                    admin.setProfession("ADMIN");
                    admin.setFiliere_id(null);
                    admin.setNiveau(null);
                    return admin;
                }
            }
        }
        return null;
    }

    // ==========================================
    // CRUD UTILISATEURS
    // ==========================================

    public List<User> getAllUsers() throws SQLException {  // ✅ throws SQLException
        List<User> liste = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                liste.add(mapResultSetToUser(rs));
            }
        }
        return liste;
    }

    public boolean addUser(User u) throws SQLException {  // ✅ throws SQLException
        String sql = "INSERT INTO users (code_permanent, nom, prenom, email, profession, filiere_id, niveau) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, u.getCode_permanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            
            Integer filiereId = u.getFiliere_id();
            if (filiereId != null && filiereId > 0) {
                ps.setInt(6, filiereId);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            
            if (u.getNiveau() != null && !u.getNiveau().isEmpty()) {
                ps.setString(7, u.getNiveau());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(User u) throws SQLException {  // ✅ throws SQLException
        String sql = "UPDATE users SET code_permanent = ?, nom = ?, prenom = ?, email = ?, "
                   + "profession = ?, filiere_id = ?, niveau = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, u.getCode_permanent());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getProfession());
            
            Integer filiereId = u.getFiliere_id();
            if (filiereId != null && filiereId > 0) {
                ps.setInt(6, filiereId);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            
            if (u.getNiveau() != null && !u.getNiveau().isEmpty()) {
                ps.setString(7, u.getNiveau());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            
            ps.setInt(8, u.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {  // ✅ throws SQLException
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // CRUD ADMINISTRATEURS
    // ==========================================

    public List<User> getAllAdmins() throws SQLException {  // ✅ throws SQLException
        List<User> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin ORDER BY id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User admin = new User();
                admin.setId(rs.getInt("id"));
                admin.setCode_permanent(rs.getLong("code_permanent"));
                admin.setNom(rs.getString("nom"));
                admin.setPrenom(rs.getString("prenom"));
                admin.setEmail(rs.getString("email"));
                admin.setPassword(rs.getString("password"));
                admin.setProfession("ADMIN");
                admin.setFiliere_id(null);
                admin.setNiveau(null);
                admins.add(admin);
            }
        }
        return admins;
    }

    public boolean addAdmin(User admin, String password) throws SQLException {  // ✅ throws SQLException
        String sql = "INSERT INTO admin (nom, prenom, email, code_permanent, password) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, admin.getNom());
            ps.setString(2, admin.getPrenom());
            ps.setString(3, admin.getEmail());
            ps.setLong(4, admin.getCode_permanent());
            ps.setString(5, PasswordHasher.hashSHA256(password));
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAdmin(int adminId) throws SQLException {  // ✅ throws SQLException
        String sql = "DELETE FROM admin WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // FILIERES
    // ==========================================

    public List<Filiere> getAllFilieres() throws SQLException {  // ✅ throws SQLException
        List<Filiere> filieres = new ArrayList<>();
        String sql = "SELECT id, nom FROM filieres ORDER BY nom";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Filiere f = new Filiere();
                f.setId(rs.getInt("id"));
                f.setNom(rs.getString("nom"));
                filieres.add(f);
            }
        }
        return filieres;
    }

    public String getFiliereNameById(int id) throws SQLException {  // ✅ throws SQLException
        String sql = "SELECT nom FROM filieres WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        }
        return null;
    }

    public Integer getFiliereIdByName(String nom) throws SQLException {  // ✅ throws SQLException
        String sql = "SELECT id FROM filieres WHERE nom = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nom);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    // ==========================================
    // GESTION MOT DE PASSE
    // ==========================================

    public boolean updateUserPassword(int userId, String password) throws SQLException {  // ✅ throws SQLException
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateAdminPassword(int adminId, String password) throws SQLException {  // ✅ throws SQLException
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String hash = PasswordHasher.hashSHA256(password);
            ps.setString(1, hash);
            ps.setInt(2, adminId);
            
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setCode_permanent(rs.getLong("code_permanent"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setProfession(rs.getString("profession"));
        
        int filId = rs.getInt("filiere_id");
        u.setFiliere_id(rs.wasNull() ? null : filId);
        
        u.setNiveau(rs.getString("niveau"));
        return u;
    }

    public boolean emailExists(String email) throws SQLException {  // ✅ throws SQLException
        String sql = "SELECT 1 FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean codePermanentExists(long code) throws SQLException {  // ✅ throws SQLException
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
     String sql = "SELECT COUNT(*) FROM users";
     
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

 /**
  * Récupère le nombre total de votes
  */
 public int getTotalVotes() throws SQLException {
     String sql = "SELECT COUNT(*) FROM votes";
     
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
  * Récupère le nombre d'élections
  */
 public int getTotalElections() throws SQLException {
     String sql = "SELECT COUNT(*) FROM elections";
     
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
  * Récupère le nombre d'utilisateurs connectés (en ligne)
  * Note : Nécessite une table de sessions ou un champ "last_activity"
  */
 public int getUsersEnLigne() throws SQLException {
     // Si vous avez une table de sessions
     String sql = "SELECT COUNT(DISTINCT user_id) FROM sessions WHERE date_expiration > NOW()";
     
     // OU si vous avez un champ "last_activity" dans users
     // String sql = "SELECT COUNT(*) FROM users WHERE last_activity > DATE_SUB(NOW(), INTERVAL 5 MINUTE)";
     
     try (Connection conn = DBConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
         
         if (rs.next()) {
             return rs.getInt(1);
         }
     } catch (SQLException e) {
         // Si la table n'existe pas encore, retourner 0
         System.err.println("⚠️ Table de sessions non trouvée, retour 0");
         return 0;
     }
     return 0;
 }

 /**
  * Récupère le nombre d'utilisateurs hors ligne
  */
 public int getUsersHorsLigne() throws SQLException {
     int total = getTotalUsers();
     int enLigne = getUsersEnLigne();
     return total - enLigne;
 }

 /**
  * Récupère le nombre de votants (utilisateurs ayant voté)
  */
 public int getVotants() throws SQLException {
     String sql = "SELECT COUNT(DISTINCT utilisateur_id) FROM votes";
     
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
  * Récupère le nombre de non-votants
  */
 public int getNonVotants() throws SQLException {
     int total = getTotalUsers();
     int votants = getVotants();
     return total - votants;
 }

 /**
  * Récupère toutes les statistiques en une seule fois (optimisation)
  */
 public DashboardStats getDashboardStats() throws SQLException {
     DashboardStats stats = new DashboardStats();
     
     try (Connection conn = DBConnection.getConnection()) {
         // Nombre total d'utilisateurs
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
             if (rs.next()) stats.totalUsers = rs.getInt(1);
         }
         
         // Nombre d'étudiants
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE profession = 'ETUDIANT'")) {
             if (rs.next()) stats.totalEtudiants = rs.getInt(1);
         }
         
         // Nombre d'enseignants
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE profession = 'ENSEIGNANT'")) {
             if (rs.next()) stats.totalEnseignants = rs.getInt(1);
         }
         
         // Nombre d'administrateurs
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM admin")) {
             if (rs.next()) stats.totalAdmins = rs.getInt(1);
         }
         
         // Nombre de votes
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM votes")) {
             if (rs.next()) stats.totalVotes = rs.getInt(1);
         }
         
         // Nombre d'élections
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM elections")) {
             if (rs.next()) stats.totalElections = rs.getInt(1);
         }
         
         // Votants (utilisateurs ayant voté)
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT COUNT(DISTINCT utilisateur_id) FROM votes")) {
             if (rs.next()) stats.votants = rs.getInt(1);
         }
         
         // Non-votants
         stats.nonVotants = stats.totalUsers - stats.votants;
         
         // En ligne / Hors ligne (si table sessions existe)
         try {
             try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery("SELECT COUNT(DISTINCT user_id) FROM sessions WHERE date_expiration > NOW()")) {
                 if (rs.next()) stats.enLigne = rs.getInt(1);
             }
             stats.horsLigne = stats.totalUsers - stats.enLigne;
         } catch (SQLException e) {
             // Table sessions n'existe pas
             stats.enLigne = 0;
             stats.horsLigne = stats.totalUsers;
         }
     }
     
     return stats;
 }

 /**
  * Classe interne pour les statistiques du dashboard
  */
 public static class DashboardStats {
     public int totalUsers;
     public int totalEtudiants;
     public int totalEnseignants;
     public int totalAdmins;
     public int totalVotes;
     public int totalElections;
     public int votants;
     public int nonVotants;
     public int enLigne;
     public int horsLigne;
     
     @Override
     public String toString() {
         return "DashboardStats{" +
                 "totalUsers=" + totalUsers +
                 ", totalEtudiants=" + totalEtudiants +
                 ", totalEnseignants=" + totalEnseignants +
                 ", totalAdmins=" + totalAdmins +
                 ", totalVotes=" + totalVotes +
                 ", totalElections=" + totalElections +
                 ", votants=" + votants +
                 ", nonVotants=" + nonVotants +
                 ", enLigne=" + enLigne +
                 ", horsLigne=" + horsLigne +
                 '}';
     }
 }
}