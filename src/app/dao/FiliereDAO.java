package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Departement;
import app.model.Filiere;
import app.utils.DBConnection;

public class FiliereDAO {

    // ===================================================
    // AJOUTER UNE FILIÈRE
    // ===================================================

    public boolean addFiliere(Filiere f) {
        String sql = "INSERT INTO filieres (departement_id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Filière " + f.getNom() + " ajoutée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌Erreur lors de l'insertion de la filière " + f.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // ===================================================
    // RECHERCHER UNE FILIÈRE PAR SON ID
    // ===================================================

    public Filiere searchFiliereById(int id) {
        String sql = "SELECT * FROM filieres WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Filiere f = new Filiere();
                    f.setId(rs.getInt("id"));                   
                    f.setDepartement_id(rs.getInt("departement_id"));
                    f.setNom(rs.getString("nom"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de la filière n° " + id);
            e.printStackTrace();
        }
        return null;
    }

    // ===================================================
    // RECHERCHER UNE FILIÈRE PAR SON NOM
    // ===================================================

    public Filiere getFiliereIdByName(String nom) throws SQLException {
        String sql = "SELECT id FROM filieres WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Filiere f = new Filiere();
                    f.setId(rs.getInt("id"));                   
                    f.setDepartement_id(rs.getInt("departement_id"));
                    f.setNom(rs.getString("nom"));
                    return f;
                }
            }
        }catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de la filière nom " + nom);
            e.printStackTrace();
        }
        return null;
    }

    // ===================================================
    // RÉCUPÉRER TOUTES LES FILIÈRES
    // ===================================================

    public List<Filiere> getAllFilieres() {
        List<Filiere> liste = new ArrayList<>();
        String sql = "SELECT f.*, d.nom AS departement_nom "+
        "FROM filieres f "+
        "LEFT JOIN departements d ON f.departement_id = d.id "+
        "ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Filiere f = new Filiere();
                f.setId(rs.getInt("id"));                     
                f.setDepartement_id(rs.getInt("departement_id")); 
                f.setNom(rs.getString("nom"));
                Departement d = new Departement();
                d.setId(rs.getInt("departement_id"));
                d.setNom(rs.getString("departement_nom"));
                f.setDepartement(d);
                liste.add(f);
            }       
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des filières");
            e.printStackTrace();
        }
        return liste;
    }

    // ===================================================
    // RÉCUPÉRER TOUTES LES FILIÈRES D'UN DÉPARTEMENT
    // ===================================================

    public List<Filiere> getAllFilieresForDepartement(int idDepartement) {
        List<Filiere> liste = new ArrayList<>();
        String sql = "SELECT f.*, d.nom AS departement_nom "+
        "FROM filieres f "+
        "LEFT JOIN departements d ON f.departement_id = d.id "+
        "WHERE departement_id = ? "+
        "ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                ps.setInt(1, idDepartement);
            while (rs.next()) {
                Filiere f = new Filiere();
                f.setId(rs.getInt("id"));                     
                f.setDepartement_id(rs.getInt("departement_id")); 
                f.setNom(rs.getString("nom"));
                Departement d = new Departement();
                d.setId(rs.getInt("departement_id"));
                d.setNom(rs.getString("departement_nom"));
                f.setDepartement(d);
                liste.add(f);
            }       
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des filières");
            e.printStackTrace();
        }
        return liste;
    }

    // ===================================================
    // MODIFIER UNE FILIÈRE
    
    // ===================================================
    public boolean updateFiliere(Filiere f) {
        String sql = "UPDATE filieres SET departement_id = ?, nom = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            ps.setInt(3, f.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Filière " + f.getNom() + " modifiée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la filière " + f.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // ===================================================
    // SUPPRIMER UNE FILIÈRE
    // ===================================================

    public boolean deleteFiliere(int id) {
        String sql = "DELETE FROM filieres WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Filière n° " + id + " supprimée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la filière n° " + id);
            e.printStackTrace();
        }
        return false;
    }
}