package app.dao;

import app.utils.DBConnection;
import app.model.Candidat;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {

    // ===================== CRUD ==============================

    // AJOUTER UN CANDIDAT (Retourne true si l'ajout a réussi)

    public boolean addCandidat(Candidat c) {
        String sql = "INSERT INTO candidats (election_id, user_id, programme, photo) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());

            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Candidat ajouté avec succès.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du candidat.");
            e.printStackTrace();
        }
        return false;
    }

    // LISTE DES CANDIDATS D'UNE ÉLECTION DONNÉE

    public List<Candidat> getCandidatesForElection(int electionId) {
        List<Candidat> liste = new ArrayList<>();
        // Correction des espaces, de l'orthographe de 'candidats' et ajout des champs utilisateurs nécessaires
        String sql = "SELECT c.*, u.nom, u.prenom, u.profession, u.email, u.code_permanent " +
                    "FROM candidats c " +
                    "JOIN users u ON c.user_id = u.id " +
                    "WHERE c.election_id = ?";
                    
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("user_id")); // Récupère l'ID de l'utilisateur lié
                    u.setCode_permanent(rs.getInt("code_permanent"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setProfession(rs.getString("profession")); // Correction du setter ici (fini le doublon setPrenom)

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
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des candidats pour l'élection n° " + electionId);
            e.printStackTrace();
        }
        return liste;
    }

    // MODIFIER UN CANDIDAT (Retourne true si la modification a réussi)
    
    public boolean updateCandidat(Candidat c) {
        String sql = "UPDATE candidats SET election_id = ?, user_id = ?, programme = ?, photo = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());
            ps.setInt(5, c.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Candidat n° " + c.getId() + " modifié dans la base de données.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du candidat n° " + c.getId());
            e.printStackTrace();
        }
        return false;
    }
    
    // SUPPRIMER UN CANDIDAT (Retourne true si la suppression a réussi)

    public boolean deleteCandidate(int id) {
        String sql = "DELETE FROM candidats WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Candidat n° " + id + " supprimé de la base de données.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du candidat n° " + id);
            e.printStackTrace();
        }
        return false;
    }
}