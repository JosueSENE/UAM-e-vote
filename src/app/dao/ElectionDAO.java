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

    /**
     * Récupère toutes les élections de la base de données
     */
    public List<Election> getAllElections() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "ORDER BY e.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToElection(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections :");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère une élection spécifique par son ID
     */
    public Election getElectionById(int id) {
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToElection(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'élection ID " + id);
            e.printStackTrace();
        }
        return null;
    }

    // ===================== CRUD ==============================

    /**
     * Ajouter une élection
     */
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

            setNullableInt(ps, 6, el.getCible_ufr_id());
            setNullableInt(ps, 7, el.getCible_departement_id());
            setNullableInt(ps, 8, el.getCible_filiere_id());
            
            setNullableString(ps, 9, el.getCible_niveau());
            setNullableString(ps, 10, el.getCible_profession());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'élection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mise à jour complète d'une élection
     */
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
            
            setNullableInt(ps, 6, el.getCible_ufr_id());
            setNullableInt(ps, 7, el.getCible_departement_id());
            setNullableInt(ps, 8, el.getCible_filiere_id());
            
            setNullableString(ps, 9, el.getCible_niveau());
            setNullableString(ps, 10, el.getCible_profession());

            ps.setInt(11, el.getId());

            if (ps.executeUpdate() > 0) {
                el.setStatut(nouveauStatut);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'élection ID " + el.getId());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mise à jour du statut d'une élection
     */
    public boolean updateStatus(int electionId, String newStatus) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, electionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du statut");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprimer une élection
     */
    public boolean deleteElection(int id) {
        String sql = "DELETE FROM elections WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'élection n° " + id);
            e.printStackTrace();
        }
        return false;
    }

    // ===================== FILTRES & RECHERCHES ==============================

    public List<Election> getElectionsEnCours() {
        return getElectionsByCondition("WHERE e.statut = 'Ouverte' ORDER BY e.date_fin ASC");
    }

    public List<Election> getElectionsTerminees() {
        return getElectionsByCondition("WHERE e.statut = 'Fermée' ORDER BY e.date_fin DESC");
    }

    public List<Election> getElectionsEnPreparation() {
        return getElectionsByCondition("WHERE e.statut = 'En préparation' ORDER BY e.date_debut ASC");
    }

    public List<Election> getElectionsByType(String typeElection) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.type_election = ? " +
                    "ORDER BY e.date_debut DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeElection);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToElection(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections par type");
            e.printStackTrace();
        }
        return list;
    }

    private List<Election> getElectionsByCondition(String conditionSql) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " + conditionSql;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToElection(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de récupération des élections avec filtre");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère les élections accessibles à un utilisateur spécifique
     */
    public List<Election> getElectionsForUser(int userId, String profession, Integer filiereId, String niveau, Integer ufrId) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.statut = 'Ouverte' AND ( " +
                    "   (e.cible_ufr_id IS NULL AND e.cible_departement_id IS NULL AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_ufr_id = ? AND e.cible_departement_id IS NULL AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_departement_id = (SELECT departement_id FROM filieres WHERE id = ?) AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_filiere_id = ? AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_niveau = ? AND e.cible_filiere_id IS NULL) " +
                    "   OR (e.cible_filiere_id = ? AND e.cible_niveau = ?) " +
                    ") " +
                    "AND (e.cible_profession IS NULL OR e.cible_profession = '' OR e.cible_profession LIKE ?) " +
                    "ORDER BY e.date_fin ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId != null ? ufrId : 0);
            ps.setInt(2, filiereId != null ? filiereId : 0);
            ps.setInt(3, filiereId != null ? filiereId : 0);
            ps.setString(4, niveau != null ? niveau : "");
            ps.setInt(5, filiereId != null ? filiereId : 0);
            ps.setString(6, niveau != null ? niveau : "");
            ps.setString(7, profession != null ? "%" + profession + "%" : "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToElection(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections pour l'utilisateur");
            e.printStackTrace();
        }
        return list;
    }

    // ===================== STATISTIQUES ==============================

    public int countElectionsByStatut(String statut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections WHERE statut = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalElections() throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Récupère le nombre d'électeurs concernés par une élection
     */
    public int getElecteursCountForElection(int electionId) throws SQLException {
        Election election = getElectionById(electionId);
        if (election == null) return 0;
        
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT u.id) FROM users u ");
        sql.append("JOIN filieres f ON u.filiere_id = f.id ");
        sql.append("JOIN departements d ON f.departement_id = d.id ");
        sql.append("JOIN ufr uf ON d.ufr_id = uf.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        Integer ufrId = election.getCible_ufr_id();
        if (ufrId != null) {
            sql.append("AND uf.id = ? ");
            params.add(ufrId);
        }
        
        Integer depId = election.getCible_departement_id();
        if (depId != null) {
            sql.append("AND d.id = ? ");
            params.add(depId);
        }
        
        Integer filId = election.getCible_filiere_id();
        if (filId != null) {
            sql.append("AND u.filiere_id = ? ");
            params.add(filId);
        }
        
        String niveau = election.getCible_niveau();
        if (niveau != null && !niveau.trim().isEmpty()) {
            sql.append("AND u.niveau = ? ");
            params.add(niveau);
        }

        String profession = election.getCible_profession();
        if (profession != null && !profession.trim().isEmpty()) {
            sql.append("AND u.profession LIKE ? ");
            params.add("%" + profession + "%");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public void updateAllStatuses() {
        List<Election> elections = getAllElections();
        int updated = 0;
        
        for (Election el : elections) {
            String nouveauStatut = el.calculerStatut();
            if (!nouveauStatut.equals(el.getStatut())) {
                if (updateStatus(el.getId(), nouveauStatut)) {
                    el.setStatut(nouveauStatut);
                    updated++;
                }
            }
        }
        
        if (updated > 0) {
            System.out.println("✅ " + updated + " statut(s) d'élection mis à jour automatiquement");
        }
    }

    // ===================== HELPER MAPPING ==============================

    private Election mapResultSetToElection(ResultSet rs) throws SQLException {
        Election el = new Election();
        el.setId(rs.getInt("id"));
        el.setTitre(rs.getString("titre"));
        el.setTypeElection(rs.getString("type_election"));
        
        Timestamp dateDebut = rs.getTimestamp("date_debut");
        if (dateDebut != null) el.setDateDebut(dateDebut.toLocalDateTime());
        
        Timestamp dateFin = rs.getTimestamp("date_fin");
        if (dateFin != null) el.setDateFin(dateFin.toLocalDateTime());
        
        String statutBase = rs.getString("statut");
        String statutActuel = el.calculerStatut();
        
        if (!statutActuel.equals(statutBase)) {
            updateStatus(el.getId(), statutActuel);
            el.setStatut(statutActuel);
        } else {
            el.setStatut(statutBase);
        }
        
        // Mapping Cibles
        int ufrId = rs.getInt("cible_ufr_id");
        if (!rs.wasNull()) {
            el.setCible_ufr_id(ufrId);
            Ufr u = new Ufr();
            u.setId(ufrId);
            u.setNom(rs.getString("ufr_nom"));
            el.setUfr(u);
        }
        
        int depId = rs.getInt("cible_departement_id");
        if (!rs.wasNull()) {
            el.setCible_departement_id(depId);
            Departement d = new Departement();
            d.setId(depId);
            d.setNom(rs.getString("departement_nom"));
            el.setDepartement(d);
        }
        
        int filId = rs.getInt("cible_filiere_id");
        if (!rs.wasNull()) {
            el.setCible_filiere_id(filId);
            Filiere f = new Filiere();
            f.setId(filId);
            f.setNom(rs.getString("filiere_nom"));
            el.setFiliere(f);
        }
        
        el.setCible_niveau(rs.getString("cible_niveau"));
        el.setCible_profession(rs.getString("cible_profession"));
        
        return el;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null && value > 0) {
            ps.setInt(index, value);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value != null && !value.trim().isEmpty()) {
            ps.setString(index, value);
        } else {
            ps.setNull(index, Types.VARCHAR);
        }
    }
}