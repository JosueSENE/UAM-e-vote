package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Enseignant_filiere;
import app.utils.DBConnection;

public class EnseignantFiliereDAO {

    // AJOUTER UN ENSEIGNANT À UNE FILIÉRE

    public void addEnseignantFilieres ( Enseignant_filiere ef) throws SQLException{
        String sql = "INSERT INTO enseignant_filieres (enseignant_id, filiere_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, ef.getEnseignant_id());
            ps.setInt(2, ef.getUfr_id());
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de l'insertion de l'Enseignant "+ef.getEnseignant_id());
            else System.err.println("Succés : Enseignant "+ef.getEnseignant_id()+" ajouté dans la filière "+ef.getUfr_id());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'enseignant "+ef.getEnseignant_id());
            e.printStackTrace();
        }
    }

    // RECHERCHER UN ENSEIGNANT DANS UNE FILIÈRE

    public Enseignant_filiere searchEnseignantFilieres (int enseignant_id, int filiere_id){
        String sql = "SELECT * FROM enseignant_filieres WHERE enseignant_id=? AND filiere_id=?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,enseignant_id);
            ps.setInt(2,filiere_id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Enseignant_filiere ef = new Enseignant_filiere();
                    ef.setEnseignant_id(rs.getInt("enseignant_id"));
                    ef.setUfr_id(rs.getInt("filiere_id"));
                    return ef;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'enseignant "+enseignant_id+" dans la filière n° "+filiere_id);
            e.printStackTrace();
        }
        return null;
    }

    //  RECUPERER TOUS LES LIGNE DANS LA TABLE enseignant_filieres

    public List<Enseignant_filiere> getAllEnseignantFilieres() throws SQLException{
        List<Enseignant_filiere> liste = new ArrayList<>();
        String sql = "SELECT * FROM enseignant_filieres ORDER BY enseignant_id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                Enseignant_filiere ef = new Enseignant_filiere();
                ef.setEnseignant_id(rs.getInt("enseignant_id"));
                ef.setUfr_id(rs.getInt("filiere_id"));
                liste.add(ef);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récuperation des enseignants : ");
            e.printStackTrace();
        }
        return liste;
    }

    //  MODIFIER UN ENSEIGNANT DANS UNE FILIÈRE

    public void updateEnseignantFiliere (Enseignant_filiere ef, int anciennefiliereid) throws SQLException{
        String sql = "UPDATE enseignant_filieres SET filiere_id=? WHERE enseignant_id=? AND filiere_id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, ef.getUfr_id());
            ps.setInt(2, ef.getEnseignant_id());
            ps.setInt(3, anciennefiliereid);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec de la mise à jour pour l'enseignant "+ef.getEnseignant_id());
            else System.err.println("Succés : Lien mis à jour pour l'enseignant "+ef.getEnseignant_id()+" modifié dans la base de données");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'enseignant "+ef.getEnseignant_id());
            e.printStackTrace();
        }
    }

    //  SUPPRIMER UNE ENSEINANT D'UNE FILIÈRE

    public void deleteEnseignantFiliere (int enseignant_id, int filiere_id) throws SQLException {
        String sql = "DELETE FROM enseignant_filieres WHERE enseignant_id=? AND filiere_id=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, enseignant_id);
            ps.setInt(2, filiere_id);
            if (ps.executeUpdate() ==  0) throw new SQLException("Echec lors de la suppression du lien");
            else System.err.println("Succes: Enseignant "+enseignant_id+" retiré de la filière "+filiere_id);  
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du lien");
            e.printStackTrace();
        }
    }
}