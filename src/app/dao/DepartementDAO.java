package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Departement;
import app.utils.DBConnection;

public class DepartementDAO {

    // AJOUTER UN DEPARTEMENT

    public void addDepartement ( Departement d) throws SQLException{
        String sql = "INSERT INTO departements (ufr_id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, d.getUfr_id());
            ps.setString(2, d.getNom());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion du département "+d.getNom());
            else System.err.println("Succés : Département "+d.getNom()+" ajouté dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du département "+d.getNom());
            e.printStackTrace();
        }
    }

    // RECHERCHER UN DÉPARTEMENT PAR SON id

    public Departement searchDepartement (int id){
        String sql = "SELECT * FROM departements WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Departement d = new Departement();
                    d.setId(rs.getInt("ufr_id"));
                    d.setNom(rs.getString("nom"));
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du département n° "+ id);
            e.printStackTrace();
        }
        return null;
    }

    //  RECUPERER TOUS LES DÉPARTEMENTS

    public List<Departement> getAllDepartements() throws SQLException{
        List<Departement> liste = new ArrayList<>();
        String sql = "SELECT * FROM departements ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Departement d = new Departement();
                d.setUfr_id(rs.getInt("id"));
                d.setNom(rs.getString("nom"));
                liste.add(d);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des départements : ");
            e.printStackTrace();
        }
        return liste;
    }

    //  MODIFIER UN DÉPARTEMENT

    public void updateDepartement (Departement d) throws SQLException{
        String sql = "UPDADE departements SET ufr_id=?, nom=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, d.getUfr_id());
            ps.setString(2, d.getNom());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour du département"+d.getNom());
            else System.err.println("Succés : Département "+d.getNom()+" modifié dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du département "+d.getNom());
            e.printStackTrace();
        }
    }

    //  SUPPRIMER UN DÉPARTEMENT

    public void deleteDepartement (int id) throws SQLException {
        String sql = "DELETE FROM departements WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression du département n° "+id);
            else System.err.println("Succes: Département "+id+" supprimer dans la base de données ");    
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du département "+id);
            e.printStackTrace();
        }
    }
    
}