package app.dao;

import app.model.Ufr;
import app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UfrDAO {

    // =======================================
    // AJOUTER UNE UFR
    // =======================================

    public boolean addUfr(Ufr u) {
        String sql = "INSERT INTO ufr (nom) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : UFR " + u.getNom() + " ajoutée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'UFR " + u.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // =======================================
    // RECUPERER TOUTES LES UFRs
    // =======================================
    
    public List<Ufr> getAllUfr() {
        List<Ufr> liste = new ArrayList<>();
        String sql = "SELECT * FROM ufr ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ufr u = new Ufr();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                liste.add(u);
            }       
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des UFRs");
            e.printStackTrace();
        }
        return liste;
    }

    // =======================================
    // RECHERCHER UNE UFR PAR SON ID
    // =======================================

    public Ufr searchUfrForId(int id) {
        String sql = "SELECT * FROM ufr WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ufr u = new Ufr();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'UFR n° " + id);
            e.printStackTrace();
        }
        return null;
    }

    // =======================================
    // RECHERCHER UNE UFR PAR SON NOM
    // =======================================

    public Ufr searchUfrForName(String nom) {
        String sql = "SELECT * FROM ufr WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ufr u = new Ufr();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche de l'UFR " + nom);
            e.printStackTrace();
        }
        return null;
    }

    // =======================================
    // MODIFIER UNE UFR
    // =======================================

    public boolean updateUfr(Ufr u) {
        String sql = "UPDATE ufr SET nom = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setInt(2, u.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : UFR " + u.getNom() + " modifiée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'UFR " + u.getNom());
            e.printStackTrace();
        }
        return false;
    }

    // =======================================
    // SUPPRIMER UNE UFR
    // =======================================

    public boolean deleteUfr(int id) {
        String sql = "DELETE FROM ufr WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : UFR n° " + id + " supprimée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'UFR " + id);
            e.printStackTrace();
        }
        return false;
    }
    
}