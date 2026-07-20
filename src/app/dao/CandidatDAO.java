package app.dao;

import app.utils.DBConnection;
import app.model.Candidat;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {

    // ==========================================
    // AJOUTER UN CANDIDAT
    // ==========================================
    public boolean addCandidat(Candidat c) {
        String sql = "INSERT INTO candidats (election_id, user_id, programme, photo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            
            // Gestion du null pour photo
            if (c.getPhoto() != null && !c.getPhoto().isEmpty()) {
                ps.setString(4, c.getPhoto());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Candidat ajouté pour l'élection : " + c.getElectionId());
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du candidat pour l'élection : " + c.getElectionId());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // LISTE DES CANDIDATS D'UNE ÉLECTION
    // ==========================================
    public List<Candidat> getCandidatesForElection(int electionId) {
        List<Candidat> liste = new ArrayList<>();
        
        // ✅ CORRECTION : Espaces et nom de table corrigés
        String sql = "SELECT c.*, u.id as user_id, u.nom, u.prenom, u.email, u.profession, " +
                     "u.code_permanent, u.filiere_id, u.niveau " +
                     "FROM candidats c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.election_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, electionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 1️⃣ Récupérer l'utilisateur
                    User u = new User();
                    u.setId(rs.getInt("user_id"));
                    u.setCode_permanent(rs.getLong("code_permanent"));  // ✅ long
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setProfession(rs.getString("profession"));  // ✅ CORRIGÉ
                    
                    // Gestion des nulls pour filiere_id
                    int filId = rs.getInt("filiere_id");
                    u.setFiliere_id(rs.wasNull() ? null : filId);
                    
                    u.setNiveau(rs.getString("niveau"));
                    
                    // 2️⃣ Récupérer le candidat
                    Candidat c = new Candidat();
                    c.setId(rs.getInt("id"));
                    c.setElectionId(rs.getInt("election_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setProgramme(rs.getString("programme"));
                    c.setPhoto(rs.getString("photo"));
                    c.setUser(u);
                    
                    liste.add(c);
                }
            }
            
            System.out.println("✅ " + liste.size() + " candidats trouvés pour l'élection : " + electionId);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des candidats pour l'élection : " + electionId);
            e.printStackTrace();
        }
        return liste;
    }

    // ==========================================
    // RÉCUPÉRER UN CANDIDAT PAR SON ID
    // ==========================================
    public Candidat getCandidatById(int candidatId) {
        String sql = "SELECT c.*, u.id as user_id, u.nom, u.prenom, u.email, u.profession, " +
                     "u.code_permanent, u.filiere_id, u.niveau " +
                     "FROM candidats c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, candidatId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1️⃣ Récupérer l'utilisateur
                    User u = new User();
                    u.setId(rs.getInt("user_id"));
                    u.setCode_permanent(rs.getLong("code_permanent"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setProfession(rs.getString("profession"));
                    
                    int filId = rs.getInt("filiere_id");
                    u.setFiliere_id(rs.wasNull() ? null : filId);
                    u.setNiveau(rs.getString("niveau"));
                    
                    // 2️⃣ Récupérer le candidat
                    Candidat c = new Candidat();
                    c.setId(rs.getInt("id"));
                    c.setElectionId(rs.getInt("election_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setProgramme(rs.getString("programme"));
                    c.setPhoto(rs.getString("photo"));
                    c.setUser(u);
                    
                    return c;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du candidat ID: " + candidatId);
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // VÉRIFIER SI UN UTILISATEUR EST CANDIDAT
    // ==========================================
    public boolean isUserCandidate(int userId, int electionId) {
        String sql = "SELECT 1 FROM candidats WHERE user_id = ? AND election_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, electionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la candidature");
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // MODIFIER UN CANDIDAT
    // ==========================================
    public boolean updateCandidat(Candidat c) {
        String sql = "UPDATE candidats SET election_id = ?, user_id = ?, programme = ?, photo = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            
            // Gestion du null pour photo
            if (c.getPhoto() != null && !c.getPhoto().isEmpty()) {
                ps.setString(4, c.getPhoto());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            
            ps.setInt(5, c.getId());
            
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Candidat modifié : ID " + c.getId());
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du candidat : " + c.getId());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // SUPPRIMER UN CANDIDAT
    // ==========================================
    public boolean deleteCandidat(int id) {
        String sql = "DELETE FROM candidats WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Candidat supprimé : ID " + id);
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du candidat : " + id);
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // COMPTER LES CANDIDATS D'UNE ÉLECTION
    // ==========================================
    public int countCandidatesForElection(int electionId) {
        String sql = "SELECT COUNT(*) FROM candidats WHERE election_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, electionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des candidats");
            e.printStackTrace();
        }
        return 0;
    }
}