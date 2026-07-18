package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Departement;
import app.utils.DBConnection;

public class DepartementDAO {

    // ===================== CRUD ==============================

    // AJOUTER UN DEPARTEMENT (Retourne true si l'ajout a réussi)
    
    public boolean addDepartement(Departement d) {
        String sql = "INSERT INTO departements (ufr_id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getUfr_id());
            ps.setString(2, d.getNom());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Département " + d.getNom() + " ajouté dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du département " + d.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // RECHERCHER UN DÉPARTEMENT PAR SON id

    public Departement searchDepartement(int id) {
        String sql = "SELECT * FROM departements WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Departement d = new Departement();
                    d.setId(rs.getInt("id"));              
                    d.setUfr_id(rs.getInt("ufr_id"));       
                    d.setNom(rs.getString("nom"));
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du département n° " + id);
            e.printStackTrace();
        }
        return null;
    }

    // RECUPERER TOUS LES DÉPARTEMENTS

    public List<Departement> getAllDepartements() {
        List<Departement> liste = new ArrayList<>();
        String sql = "SELECT * FROM departements ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Departement d = new Departement();
                d.setId(rs.getInt("id"));              
                d.setUfr_id(rs.getInt("ufr_id"));       
                d.setNom(rs.getString("nom"));
                liste.add(d);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des départements");
            e.printStackTrace();
        }
        return liste;
    }

    // MODIFIER UN DÉPARTEMENT (Retourne true si la modification a réussi)

    public boolean updateDepartement(Departement d) {
        // Correction de UPDADE -> UPDATE et ajout du filtre id
        String sql = "UPDATE departements SET ufr_id = ?, nom = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getUfr_id());
            ps.setString(2, d.getNom());
            ps.setInt(3, d.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Département " + d.getNom() + " modifié dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du département " + d.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // SUPPRIMER UN DÉPARTEMENT (Retourne true si la suppression a réussi)

    public boolean deleteDepartement(int id) {
        String sql = "DELETE FROM departements WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Département n° " + id + " supprimé dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du département n° " + id);
            e.printStackTrace();
        }
        return false;
    }
}