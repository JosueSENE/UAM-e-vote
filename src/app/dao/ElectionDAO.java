package app.dao;

import app.model.Election;
import app.model.Ufr;
import app.model.Departement;
import app.model.Filiere;
import app.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

    // ===================== LECTURE (READ) ==============================

    // RÉCUPÉRER TOUTES LES ÉLECTIONS DE LA BASE DE DONNÉES 

    public List<Election> getAllElections() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS nom_ufr, d.nom AS nom_departement, f.nom AS nom_filiere " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "ORDER BY e.id DESC";

        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Election el = new Election();
                el.setId(rs.getInt("id"));
                el.setTitre(rs.getString("titre"));
                el.setTypeElection(rs.getString("type_election"));
                
                Timestamp tsDebut = rs.getTimestamp("date_debut");
                if (tsDebut != null) el.setDateDebut(tsDebut.toLocalDateTime());
                
                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) el.setDateFin(tsFin.toLocalDateTime());

                el.setCible_ufr_id(rs.getObject("cible_ufr_id") != null ? rs.getInt("cible_ufr_id") : null);
                el.setCible_departement_id(rs.getObject("cible_departement_id") != null ? rs.getInt("cible_departement_id") : null);
                el.setCible_filiere_id(rs.getObject("cible_filiere_id") != null ? rs.getInt("cible_filiere_id") : null);
                el.setCible_niveau(rs.getString("cible_niveau"));
                el.setCible_profession(rs.getString("cible_profession"));

                if (el.getCible_ufr_id() != null) {
                    Ufr u = new Ufr();
                    u.setId(el.getCible_ufr_id());
                    u.setNom(rs.getString("nom_ufr")); 
                    el.setUfr(u);
                }
                if (el.getCible_departement_id() != null) {
                    Departement d = new Departement();
                    d.setId(el.getCible_departement_id());
                    d.setNom(rs.getString("nom_departement"));
                    el.setDepartement(d);
                }
                if (el.getCible_filiere_id() != null) {
                    Filiere f = new Filiere();
                    f.setId(el.getCible_filiere_id());
                    f.setNom(rs.getString("nom_filiere"));
                    el.setFiliere(f);
                }

                String statutBase = rs.getString("statut");
                String statutActuel = el.calculerStatut();

                if (statutBase == null || !statutActuel.equals(statutBase)) {
                    updateStatus(el.getId(), statutActuel);
                    el.setStatut(statutActuel);
                } else {
                    el.setStatut(statutBase);
                }

                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des élections :");
            e.printStackTrace();
        }
        return list;
    }

    // ===================== CRUD ==============================

    // AJOUTER UNE ÉLECTION (Retourne true si l'ajout a réussi)

    public boolean addElection(Election el) {
        String sql = "INSERT INTO elections " +
                    "(titre, type_election, date_debut, date_fin, statut, cible_ufr_id, cible_departement_id, cible_filiere_id, cible_niveau, cible_profession) " + 
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, el.getTitre());
            ps.setString(2, el.getTypeElection());
            ps.setTimestamp(3, Timestamp.valueOf(el.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(el.getDateFin()));
            
            String statutInitial = el.calculerStatut();
            ps.setString(5, statutInitial);

            if (el.getCible_ufr_id() != null) ps.setInt(6, el.getCible_ufr_id());
            else ps.setNull(6, Types.INTEGER);
            
            if (el.getCible_departement_id() != null) ps.setInt(7, el.getCible_departement_id());
            else ps.setNull(7, Types.INTEGER);
            
            if (el.getCible_filiere_id() != null) ps.setInt(8, el.getCible_filiere_id());
            else ps.setNull(8, Types.INTEGER);
            
            if (el.getCible_niveau() != null) ps.setString(9, el.getCible_niveau());
            else ps.setNull(9, Types.VARCHAR);

            if (el.getCible_profession() != null) ps.setString(10, el.getCible_profession());
            else ps.setNull(10, Types.VARCHAR);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'élection");
            e.printStackTrace();
        }
        return false;
    }

    // MISE À JOUR DU STATUT D'UNE ÉLECTION (Retourne true si la modification a réussi)

    public boolean updateStatus(int electionId, String newStatus) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, electionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut");
            e.printStackTrace();
            return false;
        }
    }

    // MISE À JOUR COMPLÈTE D'UNE ÉLÉCTION (Retourne true si la modification a réussi)
    
    public boolean updateElection(Election el) {
        String sql = "UPDATE elections SET " +
                    "titre = ?, type_election = ?, date_debut = ?, date_fin = ?, statut = ?, " +
                    "cible_ufr_id = ?, cible_departement_id = ?, cible_filiere_id = ?, cible_niveau = ?, cible_profession = ? " + 
                    "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, el.getTitre());
            ps.setString(2, el.getTypeElection());
            ps.setTimestamp(3, Timestamp.valueOf(el.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(el.getDateFin()));
            
            String nouveauStatut = el.calculerStatut();
            ps.setString(5, nouveauStatut);
            
            if (el.getCible_ufr_id() != null) ps.setInt(6, el.getCible_ufr_id()); 
            else ps.setNull(6, Types.INTEGER);
            
            if (el.getCible_departement_id() != null) ps.setInt(7, el.getCible_departement_id()); 
            else ps.setNull(7, Types.INTEGER);
            
            if (el.getCible_filiere_id() != null) ps.setInt(8, el.getCible_filiere_id()); 
            else ps.setNull(8, Types.INTEGER);
            
            if (el.getCible_niveau() != null) ps.setString(9, el.getCible_niveau()); 
            else ps.setNull(9, Types.VARCHAR);

            if (el.getCible_profession() != null) ps.setString(10, el.getCible_profession());
            else ps.setNull(10, Types.VARCHAR);

            ps.setInt(11, el.getId());

            if (ps.executeUpdate() > 0) {
                el.setStatut(nouveauStatut);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'élection ID " + el.getId());
            e.printStackTrace();
        }
        return false;
    }

    // SUPPRIMER UNE ÉLÉCTION

    public boolean deleteElection(int id) {
        String sql = "DELETE FROM elections WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'élection n° " + id);
            e.printStackTrace();
        }
        return false;
    }
}