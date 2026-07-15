package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Vote;
import app.utils.DBConnection;

public class VoteDAO {

        // ===================== CRUD  ==============================

    // AJOUTER UN VOTE (CREATE)

    public void addVote(Vote v) throws SQLException{
        String sql = "INSERT INTO votes (election_id ,candidat_id, utilisateur_id) VALUES (?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, v.getElection_id());
            ps.setInt(2, v.getCandidat_id());
            ps.setInt(3, v.getUtilisateur_id());

            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion ");
            else System.err.println("Succés : Vote ajouter dans la base de données");
        }catch(SQLException e){e.printStackTrace();}
    }

    //RECHERCHER UN VOTE PAR SON id (READ)

    public Vote searchVote (int id){
        String sql = "SELECT * FROM votes WHERE id= ?";
        try  (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){  
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                Vote v = new Vote();
                v.setId(rs.getInt("id"));
                v.setElection_id(rs.getInt("election_id"));
                v.setCandidat_id(rs.getInt("candidat_id"));
                v.setUtilisateur_id(rs.getInt("utilisateur_id"));
                v.setDate_vote(rs.getTimestamp("date_vote").toLocalDateTime());                      
                return v; // Succès
                }          
            } catch (SQLException e) {e.printStackTrace();}
        return null;
    }

    //RECUPERER TOUS LES VOTES (READ)

    public List<Vote> getAllVote() throws SQLException{
        List<Vote> liste = new ArrayList<>();
        String sql = "SELECT * FROM votes ORDER BY id DESC";
        try ( Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Vote v = new Vote();
                v.setId(rs.getInt("id"));
                v.setElection_id(rs.getInt("election_id"));
                v.setCandidat_id(rs.getInt("candidat_id"));
                v.setUtilisateur_id(rs.getInt("utilisateur_id"));
                v.setDate_vote(rs.getTimestamp("date_vote").toLocalDateTime());
                liste.add(v);
            } 
        } catch (SQLException e) {e.printStackTrace();}
        return liste;
    }

    //SUPPRIMER UN VOTE (DELETE)

    public void deleteVote(int id) throws SQLException {
        String sql = "DELETE FROM votes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression");
            else System.err.println("Succes: Vote supprimer dans la base de données ");
        } catch (SQLException e) {e.printStackTrace();
        }
    }
}
