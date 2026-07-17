package app.dao;

import app.utils.DBConnection;
import app.model.Candidat;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CandidatDAO {

    //  AJOUTER UN CANDIDAT

    public void addCandidat(Candidat c) throws SQLException{
        String sql = "INSERT INTO candidats (election_id, user_id, programme, photo) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());

            if (ps.executeUpdate() == 0) throw new SQLException("Echec lors de l'insertion de la candidature n° "+c.getId());
            else System.err.println("Succés : Candidat "+c.getId()+" ajouté dans la base de données");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du candidat :"+c.getId());
            e.printStackTrace();
        }
    }

    //  LISTE DES CANDIDATS D'UNE ÉLECTION DONNÉE 

    public List<Candidat> getCandidatesForElection (int electionId) {
        List<Candidat> liste = new ArrayList<>();
        String sql = "SELECT c.*, u.nom, u.prenom, u.profession"+
        "FROM candidatd c"+
        "JOIN users u ON c.user_id = u.id"+
        "WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    User u = new User();
                    u.setCode_permanent(rs.getInt("code_permanent"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setPrenom(rs.getString("profession"));
                    Candidat c = new Candidat();
                    c.setId(rs.getInt("id"));
                    c.setProgramme(rs.getString("programme"));
                    c.setPhoto(rs.getString("photo"));
                    c.setUser(u);
                    liste.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des candidats : ");
            e.printStackTrace();
        }
        return liste;
    }

    //  MODIFIER UN CANDIDAT

    public void updateCandidat (Candidat c) throws SQLException{
        String sql ="UPDATE candidats SET election_id=?, user_id=?, programme=?, photo=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());
            ps.setInt(5, c.getId());
            
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour du candidat "+c.getId());
            else System.err.println("Succés : Candidat "+c.getId()+" modifié dans la base de données");

        }catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du candidat n° "+c.getId());
            e.printStackTrace();}
    }
    
    //  SUPPRIMER UN CANDIDAT (DELETE)

    public void deleteCandidate(int id) throws SQLException {
        String sql = "DELETE FROM candidats WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression du candidat "+id);
            else System.err.println("Succes: Candidat n° "+id+" supprimé dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du candidat "+id);
            e.printStackTrace();
        }
    }
}
