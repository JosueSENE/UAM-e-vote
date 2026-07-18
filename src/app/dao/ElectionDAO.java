package app.dao;

import app.model.Election;
import app.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

    // ===================== LECTURE (READ) ==============================

    // RÉCUPÉRER TOUTES LES ÉLECTIONS DE LA BASE DE DONNÉES 

    public List<Election> getAllElections() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT * FROM elections";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Election el = new Election();
                el.setId(rs.getInt("id"));
                el.setTitre(rs.getString("titre"));
                el.setTypeElection(rs.getString("type_election")); 
                el.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
                el.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime());
                
                String statutBase = rs.getString("statut");
                // 1. Calcul du statut réel actuel en Java
                String statutActuel = el.calculerStatut();
                // 2. Si le statut en base est obsolète, on le met à jour physiquement en base
                if (!statutActuel.equals(statutBase)) {
                    updateStatus(el.getId(), statutActuel);
                    el.setStatut(statutActuel);
                } else {
                    el.setStatut(statutBase);
                }
                el.setCible_ufr_id(rs.getObject("cible_ufr_id") != null ? rs.getInt("cible_ufr_id") : null);
                el.setCible_departemennt_id(rs.getObject("cible_departement_id") != null ? rs.getInt("cible_departement_id") : null);
                el.setCible_filiere_id(rs.getObject("cible_filiere_id") != null ? rs.getInt("cible_filiere_id") : null);
                el.setCible_niveau(rs.getString("cible_niveau"));
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des élections : ");
            e.printStackTrace();
        }
        return list;
    }

    // ===================== CRUD ==============================

    // AJOUTER UNE ÉLECTION (Retourne true si l'ajout a réussi)

    public boolean addElection(Election el) {
        // Correction de l'espace de concaténation pour éviter les erreurs de syntaxe SQL
        String sql = "INSERT INTO elections " +
                    "(titre, type_election, date_debut, date_fin, statut, cible_ufr_id, cible_departement_id, cible_filiere_id, cible_niveau) " + 
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, el.getTitre());
            ps.setString(2, el.getTypeElection());
            ps.setTimestamp(3, Timestamp.valueOf(el.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(el.getDateFin()));
            // On calcule et insère directement le statut initial
            String statutInitial = el.calculerStatut();
            ps.setString(5, statutInitial);
            if (el.getCible_ufr_id() != null) { ps.setInt(6, el.getCible_ufr_id()); }
            else { ps.setNull(6, Types.INTEGER); }
            
            if (el.getCible_departemennt_id() != null) { ps.setInt(7, el.getCible_departemennt_id()); }
            else { ps.setNull(7, Types.INTEGER); }
            
            if (el.getCible_filiere_id() != null) { ps.setInt(8, el.getCible_filiere_id()); }
            else { ps.setNull(8, Types.INTEGER); }
            
            if (el.getCible_niveau() != null) { ps.setString(9, el.getCible_niveau()); }
            else { ps.setNull(9, Types.VARCHAR); }

            if (ps.executeUpdate() > 0) {
                System.out.println("Succès : Élection ajoutée dans la base de données");
                return true;
            }
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
            System.err.println("Erreur lors de la mise à jour du statut en base (ID: " + electionId + ")");
            e.printStackTrace();
            return false;
        }
    }

    // MISE À JOUR COMPLÈTE D'UNE ÉLÉCTION (Retourne true si la modification a réussi)
    
    public boolean updateElection(Election el) {
        String sql = "UPDATE elections SET " +
                    "titre = ?, type_election = ?, date_debut = ?, date_fin = ?, statut = ?, " +
                    "cible_ufr_id = ?, cible_departement_id = ?, cible_filiere_id = ?, cible_niveau = ? " + 
                    "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, el.getTitre());
            ps.setString(2, el.getTypeElection());
            ps.setTimestamp(3, Timestamp.valueOf(el.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(el.getDateFin()));
            
            // On recalcule automatiquement le nouveau statut à enregistrer en base de données
            String nouveauStatut = el.calculerStatut();
            ps.setString(5, nouveauStatut);
            
            // Gestion des clés étrangères facultatives (NULL)
            if (el.getCible_ufr_id() != null) { ps.setInt(6, el.getCible_ufr_id()); } 
            else { ps.setNull(6, Types.INTEGER); }
            
            if (el.getCible_departemennt_id() != null) { ps.setInt(7, el.getCible_departemennt_id()); } 
            else { ps.setNull(7, Types.INTEGER); }
            
            if (el.getCible_filiere_id() != null) { ps.setInt(8, el.getCible_filiere_id()); } 
            else { ps.setNull(8, Types.INTEGER); }
            
            if (el.getCible_niveau() != null) { ps.setString(9, el.getCible_niveau()); } 
            else { ps.setNull(9, Types.VARCHAR); }

            ps.setInt(10, el.getId());

            if (ps.executeUpdate() > 0) {
                el.setStatut(nouveauStatut);
                System.out.println("Succès : Élection ID " + el.getId() + " mise à jour avec le statut '" + nouveauStatut + "'");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'élection ID " + el.getId());
            e.printStackTrace();
        }
        return false;
    }
}