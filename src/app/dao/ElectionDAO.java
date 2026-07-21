package app.dao;

import app.model.Election;
import app.utils.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

    // ==========================================
    // LECTURE (READ)
    // ==========================================

    /**
     * Récupère toutes les élections de la base de données
     */
    public List<Election> getAllElections() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "ORDER BY e.date_debut DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Election el = mapResultSetToElection(rs);
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections : ");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère une élection par son ID
     */
    public Election getElectionById(int electionId) {
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToElection(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'élection ID " + electionId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère les élections en cours
     */
    public List<Election> getElectionsEnCours() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.statut = 'Ouverte' " +
                    "ORDER BY e.date_fin ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Election el = mapResultSetToElection(rs);
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections en cours");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère les élections terminées
     */
    public List<Election> getElectionsTerminees() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.statut = 'Fermée' " +
                    "ORDER BY e.date_fin DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Election el = mapResultSetToElection(rs);
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections terminées");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère les élections en préparation
     */
    public List<Election> getElectionsEnPreparation() {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.statut = 'En préparation' " +
                    "ORDER BY e.date_debut ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Election el = mapResultSetToElection(rs);
                list.add(el);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections en préparation");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère les élections par type
     */
    public List<Election> getElectionsByType(String typeElection) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.type_election = ? " +
                    "ORDER BY e.date_debut DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeElection);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Election el = mapResultSetToElection(rs);
                    list.add(el);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections par type");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Récupère les élections accessibles à un utilisateur spécifique
     */
    public List<Election> getElectionsForUser(int userId, String role, Integer filiereId, String niveau, Integer ufrId) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                    "uf.nom as ufr_nom, d.nom as departement_nom, f.nom as filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr uf ON e.cible_ufr_id = uf.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    "WHERE e.statut = 'Ouverte' AND ( " +
                    "   (e.cible_ufr_id IS NULL AND e.cible_departement_id IS NULL AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_ufr_id = ? AND e.cible_departement_id IS NULL AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_departement_id = (SELECT departement_id FROM filieres WHERE id = ?) AND e.cible_filiere_id IS NULL AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_filiere_id = ? AND e.cible_niveau IS NULL) " +
                    "   OR (e.cible_niveau = ? AND e.cible_filiere_id IS NULL) " +
                    "   OR (e.cible_filiere_id = ? AND e.cible_niveau = ?) " +
                    ") ORDER BY e.date_fin ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ufrId != null ? ufrId : 0);
            ps.setInt(2, filiereId != null ? filiereId : 0);
            ps.setInt(3, filiereId != null ? filiereId : 0);
            ps.setString(4, niveau != null ? niveau : "");
            ps.setInt(5, filiereId != null ? filiereId : 0);
            ps.setString(6, niveau != null ? niveau : "");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Election el = mapResultSetToElection(rs);
                    list.add(el);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des élections pour l'utilisateur");
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // CRUD
    // ==========================================

    /**
     * Ajoute une élection
     * Retourne true si l'ajout a réussi
     */
    public boolean addElection(Election el) {
        String sql = "INSERT INTO elections " +
                    "(titre, type_election, date_debut, date_fin, statut, " +
                    "cible_ufr_id, cible_departement_id, cible_filiere_id, cible_niveau) " + 
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, el.getTitre());
            ps.setString(2, el.getTypeElection());
            ps.setTimestamp(3, Timestamp.valueOf(el.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(el.getDateFin()));
            
            // Calcul du statut initial
            String statutInitial = el.calculerStatut();
            ps.setString(5, statutInitial);
            
            // Gestion des cibles (NULL autorisé)
            if (el.getCibleUfrId() != null) { 
                ps.setInt(6, el.getCibleUfrId()); 
            } else { 
                ps.setNull(6, Types.INTEGER); 
            }
            
            if (el.getCibleDepartementId() != null) { 
                ps.setInt(7, el.getCibleDepartementId()); 
            } else { 
                ps.setNull(7, Types.INTEGER); 
            }
            
            if (el.getCibleFiliereId() != null) { 
                ps.setInt(8, el.getCibleFiliereId()); 
            } else { 
                ps.setNull(8, Types.INTEGER); 
            }
            
            if (el.getCibleNiveau() != null && !el.getCibleNiveau().isEmpty()) { 
                ps.setString(9, el.getCibleNiveau()); 
            } else { 
                ps.setNull(9, Types.VARCHAR); 
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        el.setId(generatedKeys.getInt(1));
                    }
                }
                el.setStatut(statutInitial);
                System.out.println("✅ Succès : Élection ajoutée dans la base de données");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'élection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Met à jour le statut d'une élection
     */
    public boolean updateStatus(int electionId, String newStatus) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, electionId);
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Statut de l'élection " + electionId + " mis à jour : " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du statut en base (ID: " + electionId + ")");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Met à jour complète d'une élection
     */
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
            
            // Recalcul automatique du statut
            String nouveauStatut = el.calculerStatut();
            ps.setString(5, nouveauStatut);
            
            // Gestion des cibles (NULL autorisé)
            if (el.getCibleUfrId() != null) { 
                ps.setInt(6, el.getCibleUfrId()); 
            } else { 
                ps.setNull(6, Types.INTEGER); 
            }
            
            if (el.getCibleDepartementId() != null) { 
                ps.setInt(7, el.getCibleDepartementId()); 
            } else { 
                ps.setNull(7, Types.INTEGER); 
            }
            
            if (el.getCibleFiliereId() != null) { 
                ps.setInt(8, el.getCibleFiliereId()); 
            } else { 
                ps.setNull(8, Types.INTEGER); 
            }
            
            if (el.getCibleNiveau() != null && !el.getCibleNiveau().isEmpty()) { 
                ps.setString(9, el.getCibleNiveau()); 
            } else { 
                ps.setNull(9, Types.VARCHAR); 
            }

            ps.setInt(10, el.getId());

            if (ps.executeUpdate() > 0) {
                el.setStatut(nouveauStatut);
                System.out.println("✅ Succès : Élection ID " + el.getId() + " mise à jour avec le statut '" + nouveauStatut + "'");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'élection ID " + el.getId());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Supprime une élection
     */
    public boolean deleteElection(int electionId) {
        String sql = "DELETE FROM elections WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Élection ID " + electionId + " supprimée");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'élection ID " + electionId);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES ET COMPTAGE
    // ==========================================

    /**
     * Compte le nombre d'élections par statut
     */
    public int countElectionsByStatut(String statut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections WHERE statut = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre total d'élections
     */
    public int getTotalElections() throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'électeurs inscrits pour une élection
     */
    public int getElecteursCountForElection(int electionId) throws SQLException {
        Election election = getElectionById(electionId);
        if (election == null) return 0;
        
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT u.id) FROM users u ");
        List<Object> params = new ArrayList<>();
        
        // Construction de la requête selon les cibles
        if (election.getCibleUfrId() != null) {
            sql.append("JOIN ufr uf ON u.ufr_id = uf.id ");
            sql.append("WHERE u.ufr_id = ? ");
            params.add(election.getCibleUfrId());
        }
        
        if (election.getCibleDepartementId() != null) {
            sql.append("JOIN filieres f ON u.filiere_id = f.id ");
            sql.append("JOIN departements d ON f.departement_id = d.id ");
            if (params.isEmpty()) {
                sql.append("WHERE ");
            } else {
                sql.append("AND ");
            }
            sql.append("d.id = ? ");
            params.add(election.getCibleDepartementId());
        }
        
        if (election.getCibleFiliereId() != null) {
            if (params.isEmpty()) {
                sql.append("WHERE ");
            } else {
                sql.append("AND ");
            }
            sql.append("u.filiere_id = ? ");
            params.add(election.getCibleFiliereId());
        }
        
        if (election.getCibleNiveau() != null && !election.getCibleNiveau().isEmpty()) {
            if (params.isEmpty()) {
                sql.append("WHERE ");
            } else {
                sql.append("AND ");
            }
            sql.append("u.niveau = ? ");
            params.add(election.getCibleNiveau());
        }
        
        // Si aucune cible, tous les utilisateurs sont concernés
        if (params.isEmpty()) {
            sql.append("WHERE u.role != 'ADMIN'");
        } else {
            sql.append("AND u.role != 'ADMIN'");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère le nombre d'électeurs pour une élection et un UFR spécifique
     */
    public int getElecteursCountForElectionAndUfr(int electionId, int ufrId) throws SQLException {
        Election election = getElectionById(electionId);
        if (election == null) return 0;
        
        // Si l'élection n'est pas ciblée sur cet UFR, retourner 0
        if (election.getCibleUfrId() != null && election.getCibleUfrId() != ufrId) {
            return 0;
        }
        
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT u.id) FROM users u ");
        sql.append("WHERE u.ufr_id = ? AND u.role != 'ADMIN'");
        
        if (election.getCibleDepartementId() != null) {
            sql.append(" AND u.filiere_id IN (SELECT id FROM filieres WHERE departement_id = ?)");
        }
        
        if (election.getCibleFiliereId() != null) {
            sql.append(" AND u.filiere_id = ?");
        }
        
        if (election.getCibleNiveau() != null && !election.getCibleNiveau().isEmpty()) {
            sql.append(" AND u.niveau = ?");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, ufrId);
            
            if (election.getCibleDepartementId() != null) {
                ps.setInt(paramIndex++, election.getCibleDepartementId());
            }
            if (election.getCibleFiliereId() != null) {
                ps.setInt(paramIndex++, election.getCibleFiliereId());
            }
            if (election.getCibleNiveau() != null && !election.getCibleNiveau().isEmpty()) {
                ps.setString(paramIndex++, election.getCibleNiveau());
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ==========================================
    // MISE À JOUR AUTOMATIQUE DES STATUTS
    // ==========================================

    /**
     * Met à jour automatiquement les statuts de toutes les élections
     * Doit être appelé périodiquement (par exemple au démarrage et toutes les heures)
     */
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

    // ==========================================
    // MAPPING
    // ==========================================

    /**
     * Convertit un ResultSet en objet Election
     */
    private Election mapResultSetToElection(ResultSet rs) throws SQLException {
        Election el = new Election();
        el.setId(rs.getInt("id"));
        el.setTitre(rs.getString("titre"));
        el.setTypeElection(rs.getString("type_election"));
        
        Timestamp dateDebut = rs.getTimestamp("date_debut");
        if (dateDebut != null) {
            el.setDateDebut(dateDebut.toLocalDateTime());
        }
        
        Timestamp dateFin = rs.getTimestamp("date_fin");
        if (dateFin != null) {
            el.setDateFin(dateFin.toLocalDateTime());
        }
        
        // Récupération du statut de la base
        String statutBase = rs.getString("statut");
        
        // Calcul du statut réel actuel
        String statutActuel = el.calculerStatut();
        
        // Si le statut en base est obsolète, on le met à jour
        if (!statutActuel.equals(statutBase)) {
            updateStatus(el.getId(), statutActuel);
            el.setStatut(statutActuel);
        } else {
            el.setStatut(statutBase);
        }
        
        // Cibles
        int ufrId = rs.getInt("cible_ufr_id");
        el.setCibleUfrId(rs.wasNull() ? null : ufrId);
        el.setCibleUfrNom(rs.getString("ufr_nom"));
        
        int depId = rs.getInt("cible_departement_id");
        el.setCibleDepartementId(rs.wasNull() ? null : depId);
        el.setCibleDepartementNom(rs.getString("departement_nom"));
        
        int filId = rs.getInt("cible_filiere_id");
        el.setCibleFiliereId(rs.wasNull() ? null : filId);
        el.setCibleFiliereNom(rs.getString("filiere_nom"));
        
        el.setCibleNiveau(rs.getString("cible_niveau"));
        
        return el;
    }
}