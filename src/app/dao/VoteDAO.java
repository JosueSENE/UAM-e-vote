package app.dao;

import app.utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class VoteDAO {

    //  VERIFIER QU'UN ELECTEUR A VOTER OU PAS 

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
            e.printStackTrace();
        }
        return false;
    }

    //  VOTER

    public void voted(int electionId, int candidateId, int userId) {
        String sql = "INSERT INTO votes (election_id, candidat_id, utilisateur_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            ps.setInt(2, candidateId);
            ps.setInt(3, userId);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors du vote ");
            else System.err.println("Succés : vote ajouter dans la base de données");
        } catch (SQLException e) {
            // Gère l'exception d'unicité SQL de manière silencieuse (contrainte UNIQUE respectée)
            System.err.println("UAM e-Vote : Tentative de double vote rejetée par contrainte SQL.");
            e.printStackTrace();
        }
    }

    //  RECUPERATION DES RÉSULTATS 

    public Map<String, Integer> getResults(int electionId) {
        Map<String, Integer> results = new HashMap<>();      
        String sql = "SELECT u.nom, u.prenom, COUNT(v.id) AS voix "+
                    "FROM candidats c" +
                    "LEFT JOIN votes v ON c.id = v.candidat_id " +
                    "LEFT JOIN users u ON c.user_id = u.id"+
                    "WHERE c.election_id = ? " +
                    "GROUP BY c.id ORDER BY voix DESC";

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fullNom = rs.getString("prenom") + " " + rs.getString("nom");
                    results.put(fullNom, rs.getInt("voix"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}