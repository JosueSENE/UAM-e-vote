package app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.model.Enseignant_filieres;
import app.utils.DBConnection;

public class EnseignantFiliereDAO {

    // ===================================================
    // AJOUTER UN ENSEIGNANT À UNE FILIÈRE
    // ===================================================

    public boolean addEnseignantFilieres(Enseignant_filieres ef) {
        String sql = "INSERT INTO enseignant_filieres (enseignant_id, filiere_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ef.getEnseignant_id());
            ps.setInt(2, ef.getFiliere_id());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Enseignant " + ef.getEnseignant_id() + " ajouté dans la filière " + ef.getFiliere_id());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'enseignant " + ef.getEnseignant_id());
            e.printStackTrace();
        }
        return false;
    }

    // ===================================================
    // RECHERCHER UN ENSEIGNANT DANS UNE FILIÈRE
    // ===================================================

    public Enseignant_filieres searchEnseignantFilieres(int enseignant_id, int filiere_id) {
        String sql = "SELECT * FROM enseignant_filieres WHERE enseignant_id = ? AND filiere_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignant_id);
            ps.setInt(2, filiere_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enseignant_filieres ef = new Enseignant_filieres();
                    ef.setEnseignant_id(rs.getInt("enseignant_id"));
                    ef.setFiliere_id(rs.getInt("filiere_id"));
                    return ef;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'enseignant " + enseignant_id + " dans la filière n° " + filiere_id);
            e.printStackTrace();
        }
        return null;
    }

    // ================================================================
    // RÉCUPÉRER TOUTES LES LIGNES DE LA TABLE enseignant_filieres
    // ================================================================

    public List<Enseignant_filieres> getAllEnseignantFilieres() {
        List<Enseignant_filieres> liste = new ArrayList<>();
        String sql = "SELECT * FROM enseignant_filieres ORDER BY enseignant_id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Enseignant_filieres ef = new Enseignant_filieres();
                ef.setEnseignant_id(rs.getInt("enseignant_id"));
                ef.setFiliere_id(rs.getInt("filiere_id"));
                liste.add(ef);
            }       
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des enseignants : ");
            e.printStackTrace();
        }
        return liste;
    }

    // ===================================================
    // MODIFIER LA FILIÈRE D'UN ENSEIGNANT
    // ===================================================

    public boolean updateEnseignantFiliere(Enseignant_filieres ef, int ancienneFiliereId) {
        String sql = "UPDATE enseignant_filieres SET filiere_id = ? WHERE enseignant_id = ? AND filiere_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ef.getFiliere_id());
            ps.setInt(2, ef.getEnseignant_id());
            ps.setInt(3, ancienneFiliereId);         
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Lien mis à jour pour l'enseignant " + ef.getEnseignant_id());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'enseignant " + ef.getEnseignant_id());
            e.printStackTrace();
        }
        return false;
    }

    // ===================================================
    // SUPPRIMER UN ENSEIGNANT D'UNE FILIÈRE
    // ===================================================

    public boolean deleteEnseignantFiliere(int enseignant_id, int filiere_id) {
        String sql = "DELETE FROM enseignant_filieres WHERE enseignant_id = ? AND filiere_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enseignant_id);
            ps.setInt(2, filiere_id);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Enseignant " + enseignant_id + " retiré de la filière " + filiere_id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du lien pour l'enseignant " + enseignant_id);
            e.printStackTrace();
        }
        return false;
    }
}