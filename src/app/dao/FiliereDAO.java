package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Filiere;
import app.utils.DBConnection;

public class FiliereDAO {

    // ===================== CRUD ==============================

    // AJOUTER UNE FILIÈRE (Retourne true si l'ajout a réussi)
    public boolean addFiliere(Filiere f) {
        String sql = "INSERT INTO filieres (departement_id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Filière " + f.getNom() + " ajoutée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la filière " + f.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // RECHERCHER UNE FILIÈRE PAR SON id

    public Filiere searchFiliere(int id) {
        String sql = "SELECT * FROM filieres WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Filiere f = new Filiere();
                    f.setId(rs.getInt("id"));                     // Correction : ID de la filière
                    f.setDepartement_id(rs.getInt("departement_id")); // Correction : Clé étrangère du département
                    f.setNom(rs.getString("nom"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la filière n° " + id);
            e.printStackTrace();
        }
        return null;
    }

    // RÉCUPÉRER TOUTES LES FILIÈRES

    public List<Filiere> getAllFilieres() {
        List<Filiere> liste = new ArrayList<>();
        String sql = "SELECT * FROM filieres ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Filiere f = new Filiere();
                f.setId(rs.getInt("id"));                     // Correction : Récupération de l'ID de la ligne
                f.setDepartement_id(rs.getInt("departement_id")); // Récupération du département lié
                f.setNom(rs.getString("nom"));
                liste.add(f);
            }       
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des filières");
            e.printStackTrace();
        }
        return liste;
    }

    // MODIFIER UNE FILIÈRE (Retourne true si la modification a réussi)

    public boolean updateFiliere(Filiere f) {
        String sql = "UPDATE filieres SET departement_id = ?, nom = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getDepartement_id());
            ps.setString(2, f.getNom());
            ps.setInt(3, f.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Filière " + f.getNom() + " modifiée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la filière " + f.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // SUPPRIMER UNE FILIÈRE (Retourne true si la suppression a réussi)

    public boolean deleteFiliere(int id) {
        String sql = "DELETE FROM filieres WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Filière n° " + id + " supprimée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la filière n° " + id);
            e.printStackTrace();
        }
        return false;
    }
}