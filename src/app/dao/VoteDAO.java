package app.dao;

import app.utils.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VoteDAO {

    // ===================== LECTURE & VÉRIFICATION ==============================

    // VÉRIFIER QU'UN ÉLECTEUR A DÉJÀ VOTÉ OU PAS

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
            System.err.println("Erreur lors de la vérification du statut de vote (User: " + userId + ", Election: " + electionId + ")");
            e.printStackTrace();
        }
        return false;
    }

    // RÉCUPÉRATION DES RÉSULTATS (Classement par nombre de voix décroissant)

    public Map<String, Integer> getResults(int electionId) {
        Map<String, Integer> results = new LinkedHashMap<>();      
        String sql = "SELECT u.nom, u.prenom, COUNT(v.id) AS voix " +
                    "FROM candidats c " +
                    "LEFT JOIN votes v ON c.id = v.candidat_id " +
                    "LEFT JOIN users u ON c.user_id = u.id " +
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
                    
                    // Sécurité pour éviter les chaînes "null null" si l'utilisateur est introuvable
                    String fullNom = (prenom != null && nom != null) ? (prenom + " " + nom) : "Candidat Inconnu";
                    
                    results.put(fullNom, rs.getInt("voix"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des résultats pour l'élection ID " + electionId);
            e.printStackTrace();
        }
        return results;
    }

    // ===================== CRUD ==============================

    // ENREGISTRER UN VOTE (Retourne true si le vote est validé, false si tentative de double vote ou erreur)
    
    public boolean saveVote(int electionId, int candidateId, int userId) {
        String sql = "INSERT INTO votes (election_id, candidat_id, utilisateur_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            ps.setInt(2, candidateId);
            ps.setInt(3, userId);

            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Vote enregistré avec succès dans la base de données.");
                return true;
            }
        } catch (SQLException e) {
            // Intercepte de manière propre la violation de contrainte UNIQUE (double vote) sans saturer la console
            System.err.println("UAM e-Vote : Tentative de double vote détectée ou contrainte SQL violée (User ID: " + userId + ").");
        }
        return false;
    }
}