package app.dao;

import app.utils.DBConnection;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoteDAO {

    // ==========================================
    // VÉRIFICATION
    // ==========================================

    /**
     * Vérifie si un électeur a déjà voté pour une élection donnée
     */
    public boolean hasUserVoted(int electionId, int userId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE election_id = ? AND utilisateur_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification du statut de vote (User: " + userId + ", Election: " + electionId + ")");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Vérifie si un candidat a reçu des votes
     */

    public boolean hasCandidatReceivedVotes(int candidatId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE candidat_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification des votes du candidat");
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // RÉSULTATS
    // ==========================================

    /**
     * Récupère les résultats d'une élection (Nom du candidat -> Nombre de voix)
     */
    public Map<String, Integer> getResults(int electionId) {
        Map<String, Integer> results = new LinkedHashMap<>();      
        String sql = "SELECT u.nom, u.prenom, COUNT(v.id) AS voix " +
                    "FROM candidats c " +
                    "LEFT JOIN votes v ON c.id = v.candidat_id " +
                    "JOIN users u ON c.user_id = u.id " +
                    "WHERE c.election_id = ? " +
                    "GROUP BY c.id, u.nom, u.prenom " + 
                    "ORDER BY voix DESC";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String prenom = rs.getString("prenom");
                    String nom = rs.getString("nom");
                    
                    String fullNom = (prenom != null && nom != null) 
                            ? (prenom + " " + nom) 
                            : "Candidat Inconnu";
                    
                    results.put(fullNom, rs.getInt("voix"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des résultats pour l'élection ID " + electionId);
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Récupère les résultats détaillés d'une élection avec les IDs des candidats
     */
    public Map<Integer, Integer> getResultsWithCandidatIds(int electionId) {
        Map<Integer, Integer> results = new LinkedHashMap<>();      
        String sql = "SELECT c.id, COUNT(v.id) AS voix " +
                    "FROM candidats c " +
                    "LEFT JOIN votes v ON c.id = v.candidat_id " +
                    "WHERE c.election_id = ? " +
                    "GROUP BY c.id " + 
                    "ORDER BY voix DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.put(rs.getInt("id"), rs.getInt("voix"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des résultats détaillés");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Récupère le gagnant d'une élection
     */
    public Map<String, Integer> getWinner(int electionId) {
        Map<String, Integer> results = getResults(electionId);
        if (results.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        Map<String, Integer> winner = new LinkedHashMap<>();
        Map.Entry<String, Integer> firstEntry = results.entrySet().iterator().next();
        winner.put(firstEntry.getKey(), firstEntry.getValue());
        
        return winner;
    }

    /**
     * Récupère le nombre de votes pour un candidat spécifique
     */
    public int getVotesCountForCandidat(int candidatId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE candidat_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des votes pour le candidat " + candidatId);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Récupère le nombre total de votes pour une élection
     */
    public int getVotesCountForElection(int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE election_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des votes pour l'élection " + electionId);
            e.printStackTrace();
        }
        return 0;
    }

    // ==========================================
    // CRUD
    // ==========================================

    /**
     * Enregistre un vote
     * Retourne true si le vote est validé, false en cas d'erreur ou double vote
     */
    public boolean saveVote(int electionId, int candidateId, int userId) {
        String sql = "INSERT INTO votes (election_id, candidat_id, utilisateur_id, date_vote) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            ps.setInt(2, candidateId);
            ps.setInt(3, userId);

            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Vote enregistré avec succès dans la base de données.");
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Code erreur MySQL pour violation de clé unique
                System.err.println("⚠️ UAM e-Vote : Tentative de double vote détectée (User ID: " + userId + ", Election: " + electionId + ").");
            } else {
                System.err.println("❌ Erreur lors de l'enregistrement du vote");
                e.printStackTrace();
            }
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Récupère le nombre total de votants (utilisateurs ayant voté au moins une fois)
     */
    public int getTotalVotants() throws SQLException {
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
     * Récupère le nombre de votants pour une élection spécifique
     */
    public int getVotantsForElection(int electionId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT utilisateur_id) FROM votes WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre de non-votants (utilisateurs n'ayant jamais voté)
     */
    public int getNonVotants() throws SQLException {
        UserDAO userDAO = new UserDAO();
        int total = userDAO.getTotalUsers();
        int votants = getTotalVotants();
        return Math.max(0, total - votants);
    }

    /**
     * Récupère le nombre de non-votants pour une élection spécifique
     */
    public int getNonVotantsCountForElection(int electionId) throws SQLException {
        int totalInscrits = getElecteursCountForElection(electionId);
        int votants = getVotantsForElection(electionId);
        return Math.max(0, totalInscrits - votants);
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
     * Récupère le taux de participation global
     */
    public double getTauxParticipationGlobal() throws SQLException {
        UserDAO userDAO = new UserDAO();
        int total = userDAO.getTotalUsers();
        if (total == 0) return 0.0;
        
        int votants = getTotalVotants();
        return (double) votants / total * 100.0;
    }

    /**
     * Récupère le taux de participation pour une élection
     */
    public double getTauxParticipationForElection(int electionId) throws SQLException {
        int totalInscrits = getElecteursCountForElection(electionId);
        if (totalInscrits == 0) return 0.0;
        
        int votants = getVotantsForElection(electionId);
        return (double) votants / totalInscrits * 100.0;
    }

    // ==========================================
    // LISTES D'UTILISATEURS
    // ==========================================

    /**
     * Récupère la liste des utilisateurs qui ont voté pour une élection
     */
    public List<User> getVotantsForElectionDetails(int electionId) {
        List<User> votants = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM votes v " +
                    "JOIN users u ON v.utilisateur_id = u.id " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE v.election_id = ? " +
                    "GROUP BY u.id " +
                    "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    votants.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des votants");
            e.printStackTrace();
        }
        return votants;
    }

    /**
     * Récupère la liste des utilisateurs qui n'ont pas voté pour une élection
     */
    public List<User> getNonVotantsForElection(int electionId) {
        List<User> nonVotants = new ArrayList<>();
        String sql = "SELECT u.*, f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM users u " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE u.id NOT IN ( " +
                    "    SELECT DISTINCT utilisateur_id " +
                    "    FROM votes " +
                    "    WHERE election_id = ? " +
                    ") AND u.role != 'ADMIN' " +
                    "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    nonVotants.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des non-votants");
            e.printStackTrace();
        }
        return nonVotants;
    }

    // ==========================================
    // STATISTIQUES AVANCÉES
    // ==========================================

    /**
     * Récupère le nombre de votes par UFR pour une élection
     */
    public Map<String, Integer> getVotesByUfrForElection(int electionId) throws SQLException {
        Map<String, Integer> votesByUfr = new LinkedHashMap<>();
        String sql = "SELECT uf.nom, COUNT(v.id) as nb_votes " +
                    "FROM votes v " +
                    "JOIN users u ON v.utilisateur_id = u.id " +
                    "JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE v.election_id = ? " +
                    "GROUP BY uf.nom " +
                    "ORDER BY nb_votes DESC";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votesByUfr.put(rs.getString("nom"), rs.getInt("nb_votes"));
                }
            }
        }
        return votesByUfr;
    }

    /**
     * Récupère le nombre de votes par niveau pour une élection
     */
    public Map<String, Integer> getVotesByNiveauForElection(int electionId) throws SQLException {
        Map<String, Integer> votesByNiveau = new LinkedHashMap<>();
        String sql = "SELECT u.niveau, COUNT(v.id) as nb_votes " +
                    "FROM votes v " +
                    "JOIN users u ON v.utilisateur_id = u.id " +
                    "WHERE v.election_id = ? AND u.niveau IS NOT NULL " +
                    "GROUP BY u.niveau " +
                    "ORDER BY nb_votes DESC";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votesByNiveau.put(rs.getString("niveau"), rs.getInt("nb_votes"));
                }
            }
        }
        return votesByNiveau;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Récupère le nombre d'électeurs inscrits pour une élection
     */
    private int getElecteursCountForElection(int electionId) throws SQLException {
        UserDAO userDAO = new UserDAO();
        return userDAO.getTotalUsers();
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
        u.setProfession(rs.getString("role"));
        u.setNiveau(rs.getString("niveau"));
        
        int filiereId = rs.getInt("filiere_id");
        if (!rs.wasNull()) {
            u.setFiliereId(filiereId);
        }

        return u;
    }
}