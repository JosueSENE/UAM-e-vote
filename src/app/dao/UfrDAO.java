package app.dao;

import app.model.Ufr;
import app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UfrDAO {

    // AJOUTER UNE UFR

    public void addUfr( Ufr u) throws SQLException{
        String sql = "INSERT INTO ufr (nom) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, u.getNom());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion de l'UFR "+u.getNom());
            else System.err.println("Succés : UFR "+u.getNom()+" ajoutée dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'UFR "+u.getNom());
            e.printStackTrace();
        }
    }

    // RECHERCHER UNE UFR PAR SON id

    public Ufr searchUfr (int id){
        String sql = "SELECT * FROM ufr WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Ufr u = new Ufr();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'UFR n° "+ id);
            e.printStackTrace();
        }
        return null;
    }

    //  RECUPERER TOUS LES UFRs

    public List<Ufr> getAllUfr() throws SQLException{
        List<Ufr> liste = new ArrayList<>();
        String sql = "SELECT * FROM ufr ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Ufr u = new Ufr();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                liste.add(u);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des UFRs : ");
            e.printStackTrace();
        }
        return liste;
    }

    //  MODIFIER UNE UFR

    public void updateUfr(Ufr u) throws SQLException{
        String sql = "UPDADE ufr SET nom=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, u.getNom());
            ps.setInt(2, u.getId());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour de l'UFR "+u.getNom());
            else System.err.println("Succés : UFR "+u.getNom()+" modifiée dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'UFR "+u.getNom());
            e.printStackTrace();
        }
    }

    //  SUPPRIMER UNE UFR

    public void deleteUfr (int id) throws SQLException {
        String sql = "DELETE FROM ufr WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression de l'UFR n° "+id);
            else System.err.println("Succés: UFR "+id+" supprimée dans la base de données ");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'UFR "+id);
            e.printStackTrace();
        }
    }

}