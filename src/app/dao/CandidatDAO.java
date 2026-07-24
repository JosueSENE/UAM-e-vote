package app.dao;

import app.utils.DBConnection;
import app.model.Candidat;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {

    // ==========================================
    //  AJOUTER UN CANDIDAT
    // ==========================================
    
    public boolean addCandidat(Candidat c) {
        String sql = "INSERT INTO candidats (election_id, user_id, programme, photo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {  
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());
            if (ps.executeUpdate()> 0) {
                System.out.println("✅ Succès : Candidat ajouté avec succès.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'insertion du candidat.");
            e.printStackTrace();
        }
        return false;
    }

    // ================================================
    //  RÉCUPERER LES CANDIDATS D'UNE ÉLECTION DONNÉE
    // ================================================

    public List<Candidat> getCandidatesForElection(int electionId) {
        List<Candidat> liste = new ArrayList<>();
        String sql = "SELECT c.*, u.code_permanent, u.nom, u.prenom, u.email, u.profession, " +
                    "u.filiere_id, u.niveau, f.departement_id, d.uf_id " +
                    "f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM candidats c " +
                    "JOIN users u ON c.user_id = u.id " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE c.election_id = ? " +
                    "ORDER BY u.nom, u.prenom";      
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();) {
            ps.setInt(1, electionId);
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                Candidat c = mapResultSetToCandidat(rs);
                c.setUser(u);
                liste.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la liste des candidats pour l'élection n° " + electionId);
            e.printStackTrace();
        }
        return liste;
    }

    // ================================================
    //  RÉCUPERER UN CANDIDAT PAR SON ID 
    // ================================================

    public Candidat getCandidatById(int id) {
        String sql = "SELECT c.*, u.code_permanent, u.nom, u.prenom, u.email, u.profession, " +
                    "u.filiere_id, u.niveau, f.departement_id, d.uf_id, " +
                    "f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM candidats c " +
                    "JOIN users u ON c.user_id = u.id " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE c.id = ?";        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    Candidat c = mapResultSetToCandidat(rs);
                    c.setUser(u);  
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du candidat n° " + id);
            e.printStackTrace();
        }
        return null;
    }

    // ============================================
    // RECHERCHE UN CANDIDATS PAR SON NOM OU PRÉNOM
    // ============================================

    public List<Candidat> searchCandidats(String searchTerm) {
        List<Candidat> liste = new ArrayList<>();
        String sql = "SELECT c.*, u.code_permanent, u.nom, u.prenom, u.email, u.profession, " +
                "u.filiere_id, u.niveau, f.departement_id, d.uf_id, " +
                "f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                "FROM candidats c " +
                "JOIN users u ON c.user_id = u.id " +
                "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                "LEFT JOIN departements d ON f.departement_id = d.id " +
                "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                "WHERE u.nom LIKE ? OR u.prenom LIKE ? " +
                "ORDER BY u.nom, u.prenom";
                    
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    
                    Candidat c = new Candidat();
                    c.setId(rs.getInt("id"));
                    c.setElectionId(rs.getInt("election_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setProgramme(rs.getString("programme"));
                    c.setPhoto(rs.getString("photo"));
                    c.setUser(u);
                    
                    liste.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des candidats");
            e.printStackTrace();
        }
        return liste;
    }

    // ========================================================================
    //  VERIFIER SI UN UTILISATEUR EST DÉJA CANDIDAT POUR UNE ÉLECTION DONNÉE
    // ========================================================================

    public boolean isUserCandidate(int electionId, int userId) {
        String sql = "SELECT COUNT(*) FROM candidats WHERE election_id = ? AND user_id = ?";      
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification du candidat");
            e.printStackTrace();
        }
        return false;
    }

    // ========================================================
    //  RÉCUPERER TOUS LES CANDIDATURES D'UN UTILISATEUR DONNÉE
    // ========================================================

    public List<Candidat> getCandidatsByUser(int userId) {
        List<Candidat> liste = new ArrayList<>();
        String sql = "SELECT c.*, u.code_permanent, u.nom, u.prenom, u.email, u.profesion, " +
                    "u.filiere_id, u.niveau, f.departement_id, d.ufr_id, " +
                    "f.nom as filiere_nom, d.nom as departement_nom, uf.nom as ufr_nom " +
                    "FROM candidats c " +
                    "JOIN users u ON c.user_id = u.id " +
                    "LEFT JOIN filieres f ON u.filiere_id = f.id " +
                    "LEFT JOIN departements d ON f.departement_id = d.id " +
                    "LEFT JOIN ufr uf ON u.ufr_id = uf.id " +
                    "WHERE c.user_id = ? " +
                    "ORDER BY c.election_id DESC";
                    
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = mapResultSetToUser(rs);
                    Candidat c = mapResultSetToCandidat(rs);
                    c.setUser(u);
                    liste.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des candidats de l'utilisateur");
            e.printStackTrace();
        }
        return liste;
    }

    // ================================================
    //  MODIFIER UN CANDIDAT 
    // ================================================

    public boolean updateCandidat(Candidat c) {
        String sql = "UPDATE candidats SET election_id = ?, user_id = ?, programme = ?, photo = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getElectionId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getProgramme());
            ps.setString(4, c.getPhoto());
            ps.setInt(5, c.getId());
            
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Candidat n° " + c.getId() + " modifié dans la base de données.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du candidat n° " + c.getId());
            e.printStackTrace();
        }
        return false;
    }
    
    // ================================================
    //  SUPPRIMER UN CANDIDAT
    // ================================================

    public boolean deleteCandidatById(int id) {
        String sql = "DELETE FROM candidats WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                System.out.println("✅ Succès : Candidat n° " + id + " supprimé de la base de données.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du candidat n° " + id);
            e.printStackTrace();
        }
        return false;
    }

    // ================================================
    //  SUPPRIMER TOUS LES CANDIDATS D'UNE ÉLECTION
    // ================================================

    public boolean deleteCandidatsByElection(int electionId) {
        String sql = "DELETE FROM candidats WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            int deleted = ps.executeUpdate();
            System.out.println("✅ " + deleted + " candidat(s) supprimé(s) pour l'élection n° " + electionId);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression des candidats de l'élection n° " + electionId);
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    public int getTotalCandidats() throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidats";     
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql)) {
            if (rs.next()) {return rs.getInt(1);}
        }
        return 0;
    }


    public int getCandidatsCountByElection(int electionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidats WHERE election_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {return rs.getInt(1);}
            }
        }
        return 0;
    }

    // ==========================================
    // MAPPING
    // ==========================================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setCodePermanent(rs.getInt("code_permanent"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setProfession(rs.getString("role"));
        u.setNiveau(rs.getString("niveau"));
        int filiereId = rs.getInt("filiere_id");
        if (!rs.wasNull()) {
            u.setFiliereId(filiereId);
        }
        return u;
    }

    private Candidat mapResultSetToCandidat(ResultSet rs) throws SQLException {
        Candidat c = new Candidat();
        c.setId(rs.getInt("id"));
        c.setElectionId(rs.getInt("election_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setProgramme(rs.getString("programme"));
        c.setPhoto(rs.getString("photo"));
        return c;
    }
}