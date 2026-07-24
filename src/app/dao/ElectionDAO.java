package app.dao;

import app.model.Election;
import app.model.Ufr;
import app.model.Departement;
import app.model.Filiere;
import app.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

    // =========================================================================
    // 1. LECTURE & FILTRES (READ)
    // =========================================================================

    /**
     * Récupère toutes les élections de la base de données.
     */
    public List<Election> getAllElections() {
        return getElectionsByCondition("ORDER BY e.id DESC");
    }

    /**
     * Récupère une élection spécifique grâce à son ID unique.
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

    /**
     * Récupère toutes les élections correspondant à un type donné (ex: "Délégué", "BDE", etc.).
     */
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

    /**
     * Récupère la liste des élections ouvertes (en cours).
     */
    public List<Election> getElectionsEnCours() {
        return getElectionsByCondition("e.statut = 'Ouverte' ORDER BY e.date_fin ASC");
    }

    /**
     * Récupère la liste des élections fermées (terminées).
     */
    public List<Election> getElectionsTerminees() {
        return getElectionsByCondition("e.statut = 'Fermée' ORDER BY e.date_fin DESC");
    }

    /**
     * Récupère la liste des élections à venir (en préparation).
     */
    public List<Election> getElectionsEnPreparation() {
        return getElectionsByCondition("e.statut = 'En préparation' ORDER BY e.date_debut ASC");
    }

    /**
     * Méthode générique privée pour filtrer les élections selon une clause SQL personnalisée.
     */
    private List<Election> getElectionsByCondition(String conditionSql) {
        List<Election> list = new ArrayList<>();
        String sql = "SELECT e.*, u.nom AS ufr_nom, d.nom AS departement_nom, f.nom AS filiere_nom " +
                    "FROM elections e " +
                    "LEFT JOIN ufr u ON e.cible_ufr_id = u.id " +
                    "LEFT JOIN departements d ON e.cible_departement_id = d.id " +
                    "LEFT JOIN filieres f ON e.cible_filiere_id = f.id " +
                    (conditionSql.startsWith("ORDER") || conditionSql.trim().isEmpty() ? " " : " WHERE ") + conditionSql;
        
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
     * Récupère toutes les élections ouvertes auxquelles un utilisateur a le droit de participer
     * en fonction de son profil (UFR, département, filière, niveau, profession).
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

    // =========================================================================
    // 2. ÉCRITURE & PERSISTANCE (CRUD / CUD)
    // =========================================================================

    /**
     * Insère une nouvelle élection en base de données.
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
            
            String statutInitial = calculerStatut(el);
            ps.setString(5, statutInitial);
            
            setNullableInt(ps, 6, el.getCibleUfrId());
            setNullableInt(ps, 7, el.getCibleDepartementId());
            setNullableInt(ps, 8, el.getCibleFiliereId()); 
            setNullableString(ps, 9, el.getCibleNiveau());
            setNullableString(ps, 10, el.getCibleProfession());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion de l'élection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mettre à jour une élection existante en base.
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
            
            String nouveauStatut = calculerStatut(el);
            ps.setString(5, nouveauStatut);
            
            setNullableInt(ps, 6, el.getCibleUfrId());
            setNullableInt(ps, 7, el.getCibleDepartementId());
            setNullableInt(ps, 8, el.getCibleFiliereId()); 
            setNullableString(ps, 9, el.getCibleNiveau());
            setNullableString(ps, 10, el.getCibleProfession());
            
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
     * Met à jour uniquement le champ statut d'une élection.
     */
    public boolean updateStatus(int electionId, String newStatus) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, electionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du statut en base (ID: " + electionId + ")");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Parcourt toutes les élections et recalcule dynamiquement leurs statuts en base
     * si les dates de début ou de fin sont dépassées.
     */
    public void updateAllStatuses() {
        List<Election> elections = getAllElections();
        int updated = 0;
        
        for (Election el : elections) {
            String nouveauStatut = calculerStatut(el);
            if (!nouveauStatut.equals(el.getStatut())) {
                if (updateStatus(el.getId(), nouveauStatut)) {
                    el.setStatut(nouveauStatut);
                    updated++;
                }
            }
        }
        
        if (updated > 0) {
            System.out.println("✅ " + updated + " statut(s) d'élection mis à jour automatiquement.");
        }
    }

    /**
     * Supprime définitivement une élection de la base de données.
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

    // =========================================================================
    // 3. STATISTIQUES & COMPTAGE
    // =========================================================================

    /**
     * Calcule le nombre potentiel d'électeurs concernés par une élection
     * en fonction du ciblage (UFR, Département, Filière, Niveau, Profession).
     */
    public int getElecteursCountForElection(int electionId) throws SQLException {
        Election election = getElectionById(electionId);
        if (election == null) return 0;
        
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT u.id) FROM users u ");
        sql.append("LEFT JOIN filieres f ON u.filiere_id = f.id ");
        sql.append("LEFT JOIN departements d ON f.departement_id = d.id ");
        sql.append("LEFT JOIN ufr uf ON d.ufr_id = uf.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        Integer ufrId = election.getCibleUfrId();
        if (ufrId != null) {
            sql.append("AND uf.id = ? ");
            params.add(ufrId);
        }
        Integer depId = election.getCibleDepartementId();
        if (depId != null) {
            sql.append("AND d.id = ? ");
            params.add(depId);
        }
        Integer filId = election.getCibleFiliereId();
        if (filId != null) {
            sql.append("AND u.filiere_id = ? ");
            params.add(filId);
        }
        String niveau = election.getCibleNiveau();
        if (niveau != null && !niveau.trim().isEmpty()) {
            sql.append("AND u.niveau = ? ");
            params.add(niveau);
        }
        String profession = election.getCibleProfession();
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

    /**
     * Compte le nombre d'élections correspondant à un statut précis.
     */
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

    /**
     * Compte le nombre total d'élections créées.
     */
    public int getTotalElections() throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // =========================================================================
    // 4. LOGIQUE MÉTIER & CALCULS D'ÉTAT
    // =========================================================================

    /**
     * Détermine dynamiquement si l'élection doit être "En préparation", "Ouverte" ou "Fermée"
     * selon l'heure actuelle et ses dates de début/fin.
     */
    public String calculerStatut(Election el) {
        if (el == null) return "Inconnu";
        LocalDateTime maintenant = LocalDateTime.now();
        if (el.getDateDebut() != null && maintenant.isBefore(el.getDateDebut())) { 
            return "En préparation"; 
        } else if (el.getDateFin() != null && maintenant.isAfter(el.getDateFin())) { 
            return "Fermée"; 
        } else { 
            return "Ouverte"; 
        }    
    }

    public boolean estOuverte(Election el) { return el != null && "Ouverte".equals(el.getStatut()); }
    public boolean estFermee(Election el) { return el != null && "Fermée".equals(el.getStatut()); }
    public boolean enPreparation(Election el) { return el != null && "En préparation".equals(el.getStatut()); }

    public boolean estAccessible(Election el) {
        return estOuverte(el) && !estFermee(el);
    }

    public boolean estCibleeSurUfr(Election el) { return el != null && el.getCibleUfrId() != null && el.getCibleUfrId() > 0; }
    public boolean estCibleeSurDepartement(Election el) { return el != null && el.getCibleDepartementId() != null && el.getCibleDepartementId() > 0; }
    public boolean estCibleeSurFiliere(Election el) { return el != null && el.getCibleFiliereId() != null && el.getCibleFiliereId() > 0; }
    public boolean estCibleeSurNiveau(Election el) { return el != null && el.getCibleNiveau() != null && !el.getCibleNiveau().isEmpty(); }
    
    public boolean estGenerale(Election el) {
        return !estCibleeSurUfr(el) && !estCibleeSurDepartement(el) && !estCibleeSurFiliere(el) && !estCibleeSurNiveau(el);
    }

    // =========================================================================
    // 5. FORMATEURS & UTILITAIRES D'AFFICHAGE (IHM/UI)
    // =========================================================================

    public String getCibleIdAffichage(Election el) {
        if (el == null) return "Tous";
        if (el.getCibleUfrId() != null) return String.valueOf(el.getCibleUfrId());
        if (el.getCibleDepartementId() != null) return String.valueOf(el.getCibleDepartementId());
        if (el.getCibleFiliereId() != null) return String.valueOf(el.getCibleFiliereId());
        return "Tous";
    }

    public String getCibleNomAffichage(Election el) {
        if (el == null) return "Toutes les cibles";
        if (el.getUfr() != null && el.getUfr().getNom() != null) return el.getUfr().getNom();
        if (el.getDepartement() != null && el.getDepartement().getNom() != null) return el.getDepartement().getNom();
        if (el.getFiliere() != null && el.getFiliere().getNom() != null) return el.getFiliere().getNom();
        return "Toutes les cibles";
    }

    public String getStatutCouleur(Election el) {
        if (estOuverte(el)) return "green";
        if (estFermee(el)) return "red";
        return "orange";
    }

    public String getStatutAvecEmoji(Election el) {
        if (estOuverte(el)) return "🟢 Ouverte";
        if (estFermee(el)) return "🔴 Fermée";
        return "🟡 En préparation";
    }

    // =========================================================================
    // 6. VALIDATIONS
    // =========================================================================

    /**
     * Vérifie la validité des attributs essentiels d'une élection.
     */
    public boolean isValid(Election el) {
        if (el == null) return false;
        if (el.getTitre() == null || el.getTitre().trim().isEmpty()) return false;
        if (el.getTypeElection() == null || el.getTypeElection().trim().isEmpty()) return false;
        if (el.getDateDebut() == null || el.getDateFin() == null) return false;
        if (el.getDateDebut().isAfter(el.getDateFin())) return false;
        return true;
    }

    // =========================================================================
    // 7. MAPPING & UTILITAIRES JDBC INTERNES
    // =========================================================================

    /**
     * Mappe une ligne d'un ResultSet SQL vers un objet Java `Election`.
     */
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
        String statutActuel = calculerStatut(el);
        
        if (!statutActuel.equals(statutBase)) {
            updateStatus(el.getId(), statutActuel);
            el.setStatut(statutActuel);
        } else {
            el.setStatut(statutBase);
        }
        
        // Mapping Cibles
        int ufrId = rs.getInt("cible_ufr_id");
        if (!rs.wasNull()) {
            el.setCibleUfrId(ufrId);
            Ufr u = new Ufr();
            u.setId(ufrId);
            u.setNom(rs.getString("ufr_nom"));
            el.setUfr(u);
        }
        int depId = rs.getInt("cible_departement_id");
        if (!rs.wasNull()) {
            el.setCibleDepartementId(depId);
            Departement d = new Departement();
            d.setId(depId);
            d.setNom(rs.getString("departement_nom"));
            el.setDepartement(d);
        }
        int filId = rs.getInt("cible_filiere_id");
        if (!rs.wasNull()) {
            el.setCibleFiliereId(filId);
            Filiere f = new Filiere();
            f.setId(filId);
            f.setNom(rs.getString("filiere_nom"));
            el.setFiliere(f);
        }
        
        el.setCibleNiveau(rs.getString("cible_niveau"));
        el.setCibleProfession(rs.getString("cible_profession"));
        
        return el;
    }

    /**
     * Gère correctement l'insertion de valeurs d'entiers pouvant être NULL.
     */
    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null && value > 0) {
            ps.setInt(index, value);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }

    /**
     * Gère correctement l'insertion de valeurs textuelles pouvant être NULL ou vides.
     */
    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value != null && !value.trim().isEmpty()) {
            ps.setString(index, value);
        } else {
            ps.setNull(index, Types.VARCHAR);
        }
    }
}