package app.dao;

import app.model.User;
import app.model.Enseignant_filieres;
import app.utils.DBConnection;
import app.utils.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO {

    private final UserDAO userDAO;
    private final EnseignantFiliereDAO enseignantFiliereDAO;

    public EnseignantDAO() {
        this.userDAO = new UserDAO();
        this.enseignantFiliereDAO = new EnseignantFiliereDAO();
    }

    // ==========================================
    // AUTHENTIFICATION
    // ==========================================

    /**
     * Authentifie un enseignant
     */
    public User authentificate(String emailSaisi, String passwordSaisi) {
        User u = searchEnseignantByEmail(emailSaisi);
        if (u == null) {
            return null;
        }
        
        // CAS A : Première connexion (Le mot de passe en base est vide ou NULL)
        if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            try {
                int codeSaisiInt = Integer.parseInt(passwordSaisi);
                if (u.getCodePermanent() == codeSaisiInt) {
                    System.out.println("✅ Première connexion enseignant validée. En attente de création du mot de passe.");
                    return u; 
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur du format du code permanent.");
            }
        } 
        // CAS B : Connexion classique
        else {
            String hashSaisi = PasswordHasher.hashSHA256(passwordSaisi);
            if (u.getPassword().equals(hashSaisi)) {
                System.out.println("✅ Connexion enseignant classique validée avec succès.");
                return u;
            }
        }
        return null;
    }

    // ==========================================
    // RECHERCHE
    // ==========================================

    /**
     * Recherche un enseignant par son email
     */
    public User searchEnseignantByEmail(String email) {
        String sql = "SELECT u.*, uf.nom as ufr_nom, d.nom as departement_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "LEFT JOIN departements d ON d.id = ? " + // Sera remplacé par une sous-requête
                     "WHERE u.email = ? AND u.role = 'ENSEIGNANT'";
        
        // Version corrigée avec sous-requête pour le département
        sql = "SELECT u.*, uf.nom as ufr_nom, " +
              "(SELECT d.nom FROM departements d WHERE d.id = uf.id) as departement_nom " +
              "FROM users u " +
              "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
              "WHERE u.email = ? AND u.role = 'ENSEIGNANT'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    // Charger les filières de l'enseignant
                    chargerFilieresEnseignant(enseignant);
                    return enseignant;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'enseignant: " + email);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un enseignant par son ID
     */
    public User getEnseignantById(int id) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.id = ? AND u.role = 'ENSEIGNANT'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    return enseignant;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'enseignant par ID: " + id);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un enseignant par son login
     */
    public User getEnseignantByLogin(String login) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.login = ? AND u.role = 'ENSEIGNANT'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    return enseignant;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'enseignant par login: " + login);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un enseignant par son code permanent
     */
    public User getEnseignantByCodePermanent(int codePermanent) {
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.code_permanent = ? AND u.role = 'ENSEIGNANT'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codePermanent);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    return enseignant;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'enseignant par code permanent: " + codePermanent);
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // RÉCUPÉRATION DES ENSEIGNANTS
    // ==========================================

    /**
     * Récupère tous les enseignants avec leurs filières
     */
    public List<User> getAllEnseignants() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.role = 'ENSEIGNANT' " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User enseignant = mapResultSetToEnseignant(rs);
                chargerFilieresEnseignant(enseignant);
                liste.add(enseignant);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants");
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère les enseignants d'un UFR spécifique
     */
    public List<User> getEnseignantsByUfr(int ufrId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "WHERE u.role = 'ENSEIGNANT' AND u.ufr_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    liste.add(enseignant);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par UFR");
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère les enseignants d'un département spécifique
     */
    public List<User> getEnseignantsByDepartement(int departementId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT DISTINCT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "JOIN enseignant_filieres ef ON u.id = ef.enseignant_id " +
                     "JOIN filieres f ON ef.filiere_id = f.id " +
                     "WHERE u.role = 'ENSEIGNANT' AND f.departement_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    liste.add(enseignant);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par département");
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Récupère les enseignants d'une filière spécifique
     */
    public List<User> getEnseignantsByFiliere(int filiereId) {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT u.*, uf.nom as ufr_nom " +
                     "FROM users u " +
                     "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                     "JOIN enseignant_filieres ef ON u.id = ef.enseignant_id " +
                     "WHERE u.role = 'ENSEIGNANT' AND ef.filiere_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filiereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User enseignant = mapResultSetToEnseignant(rs);
                    chargerFilieresEnseignant(enseignant);
                    liste.add(enseignant);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants par filière");
            e.printStackTrace();
        }
        return liste;
    }

    // ==========================================
    // CRUD COMPLET AVEC GESTION DES FILIÈRES
    // ==========================================

    /**
     * Ajoute un enseignant avec ses filières associées
     */
    public boolean addEnseignant(User enseignant) {
        // Vérifications préalables
        try {
            if (userDAO.emailExists(enseignant.getEmail())) {
                System.err.println("❌ L'email existe déjà: " + enseignant.getEmail());
                return false;
            }
            if (userDAO.codePermanentExists(enseignant.getCodePermanent())) {
                System.err.println("❌ Le code permanent existe déjà: " + enseignant.getCodePermanent());
                return false;
            }
            if (userDAO.loginExists(enseignant.getLogin())) {
                System.err.println("❌ Le login existe déjà: " + enseignant.getLogin());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Ajouter l'enseignant dans la table users
        enseignant.setRole("ENSEIGNANT");
        if (!userDAO.addUser(enseignant)) {
            return false;
        }

        // Ajouter les associations avec les filières
        if (enseignant.getFilieresList() != null && !enseignant.getFilieresList().isEmpty()) {
            String[] filieresNoms = enseignant.getFilieresList().split(",\\s*");
            for (String filiereNom : filieresNoms) {
                Integer filiereId = userDAO.getFiliereIdByNom(filiereNom.trim());
                if (filiereId != null) {
                    Enseignant_filieres ef = new Enseignant_filieres();
                    ef.setEnseignant_id(enseignant.getId());
                    ef.setFiliere_id(filiereId);
                    enseignantFiliereDAO.addEnseignantFilieres(ef);
                } else {
                    System.err.println("⚠️ Filière non trouvée: " + filiereNom);
                }
            }
        }

        System.out.println("✅ Enseignant ajouté avec succès avec ses filières");
        return true;
    }

    /**
     * Met à jour un enseignant et ses filières
     */
    public boolean updateEnseignant(User enseignant) {
        // Mettre à jour l'enseignant dans la table users
        if (!userDAO.updateUser(enseignant)) {
            return false;
        }

        // Supprimer toutes les associations existantes
        List<Enseignant_filieres> associations = enseignantFiliereDAO.getAllEnseignantFilieres();
        for (Enseignant_filieres ef : associations) {
            if (ef.getEnseignant_id() == enseignant.getId()) {
                enseignantFiliereDAO.deleteEnseignantFiliere(ef.getEnseignant_id(), ef.getFiliere_id());
            }
        }

        // Ajouter les nouvelles associations
        if (enseignant.getFilieresList() != null && !enseignant.getFilieresList().isEmpty()) {
            String[] filieresNoms = enseignant.getFilieresList().split(",\\s*");
            for (String filiereNom : filieresNoms) {
                Integer filiereId = userDAO.getFiliereIdByNom(filiereNom.trim());
                if (filiereId != null) {
                    Enseignant_filieres ef = new Enseignant_filieres();
                    ef.setEnseignant_id(enseignant.getId());
                    ef.setFiliere_id(filiereId);
                    enseignantFiliereDAO.addEnseignantFilieres(ef);
                }
            }
        }

        System.out.println("✅ Enseignant mis à jour avec succès");
        return true;
    }

    /**
     * Supprime un enseignant et ses associations
     */
    public boolean deleteEnseignant(int enseignantId) {
        // Supprimer les associations
        List<Enseignant_filieres> associations = enseignantFiliereDAO.getAllEnseignantFilieres();
        for (Enseignant_filieres ef : associations) {
            if (ef.getEnseignant_id() == enseignantId) {
                enseignantFiliereDAO.deleteEnseignantFiliere(ef.getEnseignant_id(), ef.getFiliere_id());
            }
        }

        // Supprimer l'enseignant
        return userDAO.deleteUser(enseignantId);
    }

    // ==========================================
    // GESTION DU MOT DE PASSE
    // ==========================================

    public boolean updatePassword(int enseignantId, String password) {
        return userDAO.updatePassword(enseignantId, password);
    }

    public boolean hasPassword(int enseignantId) {
        return userDAO.hasPassword(enseignantId);
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre total d'enseignants
     */
    public int getTotalEnseignants() throws SQLException {
        return userDAO.getTotalEnseignants();
    }

    /**
     * Récupère le nombre d'enseignants par UFR
     */
    public int getEnseignantsCountByUfr(int ufrId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ENSEIGNANT' AND ufr_id = ?";
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
     * Récupère le nombre d'enseignants par département
     */
    public int getEnseignantsCountByDepartement(int departementId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT u.id) FROM users u " +
                     "JOIN enseignant_filieres ef ON u.id = ef.enseignant_id " +
                     "JOIN filieres f ON ef.filiere_id = f.id " +
                     "WHERE u.role = 'ENSEIGNANT' AND f.departement_id = ?";
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
     * Récupère le nombre d'enseignants par filière
     */
    public int getEnseignantsCountByFiliere(int filiereId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enseignant_filieres WHERE filiere_id = ?";
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

    // ==========================================
    // MÉTHODES DE VÉRIFICATION
    // ==========================================

    public boolean emailExists(String email) throws SQLException {
        return userDAO.emailExists(email);
    }

    public boolean codePermanentExists(int code) throws SQLException {
        return userDAO.codePermanentExists(code);
    }

    public boolean loginExists(String login) throws SQLException {
        return userDAO.loginExists(login);
    }

    // ==========================================
    // MÉTHODES DE CHARGEMENT DES FILIÈRES
    // ==========================================

    /**
     * Charge les filières d'un enseignant
     */
    private void chargerFilieresEnseignant(User enseignant) {
        if (enseignant == null || enseignant.getId() <= 0) return;
        
        StringBuilder filieres = new StringBuilder();
        String sql = "SELECT f.nom FROM filieres f " +
                     "JOIN enseignant_filieres ef ON f.id = ef.filiere_id " +
                     "WHERE ef.enseignant_id = ? " +
                     "ORDER BY f.nom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignant.getId());
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) filieres.append(", ");
                    filieres.append(rs.getString("nom"));
                    first = false;
                }
                enseignant.setFilieresList(filieres.toString());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des filières pour l'enseignant ID: " + enseignant.getId());
            e.printStackTrace();
        }
    }

    /**
     * Récupère la liste des filières d'un enseignant (pour l'affichage dans la table)
     */
    public List<String> getFilieresForEnseignant(int enseignantId) {
        List<String> filieres = new ArrayList<>();
        String sql = "SELECT f.nom FROM filieres f " +
                     "JOIN enseignant_filieres ef ON f.id = ef.filiere_id " +
                     "WHERE ef.enseignant_id = ? " +
                     "ORDER BY f.nom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignantId);
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
    // MAPPING
    // ==========================================

    /**
     * Convertit un ResultSet en objet User (enseignant)
     */
    private User mapResultSetToEnseignant(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setCodePermanent(rs.getInt("code_permanent"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        
        int ufrId = rs.getInt("ufr_id");
        if (!rs.wasNull()) {
            u.setUfrId(ufrId);
        }
        u.setUfrNom(rs.getString("ufr_nom"));
        
        return u;
    }
}