package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Filiere;
import app.utils.DBConnection;

public class FiliereDAO {

    // AJOUTER UNE FILIÉRE

    public void addFiliere ( Filiere f) throws SQLException{
        String sql = "INSERT INTO filieres (departement_id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion de la filière "+f.getNom());
            else System.err.println("Succés : Filière "+f.getNom()+" ajoutée dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la filière "+f.getNom());
            e.printStackTrace();
        }
    }

    // RECHERCHER UNE FILIERE PAR SON id

    public Filiere searchFiliere (int id){
        String sql = "SELECT * FROM filieres WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Filiere f = new Filiere();
                    f.setId(rs.getInt("departement_id"));
                    f.setNom(rs.getString("nom"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la filière n° "+ id);
            e.printStackTrace();
        }
        return null;
    }

    //  RECUPERER TOUS LES FILIÈRES

    public List<Filiere> getAllFilieres() throws SQLException{
        List<Filiere> liste = new ArrayList<>();
        String sql = "SELECT * FROM filieres ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Filiere f = new Filiere();
                f.setDepartement_id(rs.getInt("departement_id"));
                f.setNom(rs.getString("nom"));
                liste.add(f);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des filières : ");
            e.printStackTrace();
        }
        return liste;
    }

    //  MODIFIER UNE FILIÈRE

    public void updateFiliere (Filiere f) throws SQLException{
        String sql = "UPDADE filieres SET departement_id=?, nom=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour de la filière "+f.getNom());
            else System.err.println("Succés : Filière "+f.getNom()+" modifiée dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la filière "+f.getNom());
            e.printStackTrace();
        }
    }

    //  SUPPRIMER UNE FILIERE

    public void deleteFiliere (int id) throws SQLException {
        String sql = "DELETE FROM filieres WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression de la filière n° "+id);
            else System.err.println("Succes: Filière "+id+" supprimée dans la base de données ");  
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la filière "+id);
            e.printStackTrace();
        }
    }
    
}
