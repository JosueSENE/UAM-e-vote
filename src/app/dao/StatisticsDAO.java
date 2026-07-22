package app.dao;

import app.utils.DBConnection;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    // ==========================================
    // CLASSE INTERNE POUR LES DONNÉES STATISTIQUES
    // ==========================================

    public static class StatsData {
        private int totalVotes;
        private int totalElecteurs;
        private double tauxParticipation;
        private int enLigne;
        private int totalElections;
        private int electionsOuvertes;

        // Constructeurs
        public StatsData() {}

        public StatsData(int totalVotes, int totalElecteurs, double tauxParticipation, 
                        int enLigne, int totalElections, int electionsOuvertes) {
            this.totalVotes = totalVotes;
            this.totalElecteurs = totalElecteurs;
            this.tauxParticipation = tauxParticipation;
            this.enLigne = enLigne;
            this.totalElections = totalElections;
            this.electionsOuvertes = electionsOuvertes;
        }

        // Getters et Setters
        public int getTotalVotes() { return totalVotes; }
        public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

        public int getTotalElecteurs() { return totalElecteurs; }
        public void setTotalElecteurs(int totalElecteurs) { this.totalElecteurs = totalElecteurs; }

        public double getTauxParticipation() { return tauxParticipation; }
        public void setTauxParticipation(double tauxParticipation) { this.tauxParticipation = tauxParticipation; }

        public int getEnLigne() { return enLigne; }
        public void setEnLigne(int enLigne) { this.enLigne = enLigne; }

        public int getTotalElections() { return totalElections; }
        public void setTotalElections(int totalElections) { this.totalElections = totalElections; }

        public int getElectionsOuvertes() { return electionsOuvertes; }
        public void setElectionsOuvertes(int electionsOuvertes) { this.electionsOuvertes = electionsOuvertes; }
    }

    // ==========================================
    // MÉTHODES PRINCIPALES
    // ==========================================

    /**
     * Récupère toutes les données statistiques
     */
    public StatsData getStatsData() {
        StatsData data = new StatsData();
        
        try (Connection conn = DBConnection.getConnection()) {
            // 1. Total des votes
            data.setTotalVotes(getTotalVotes(conn));
            
            // 2. Total des électeurs
            data.setTotalElecteurs(getTotalElecteurs(conn));
            
            // 3. Taux de participation
            int totalVotes = data.getTotalVotes();
            int totalElecteurs = data.getTotalElecteurs();
            data.setTauxParticipation(totalElecteurs > 0 ? (double) totalVotes / totalElecteurs * 100 : 0);
            
            // 4. Utilisateurs en ligne (simulé - à adapter selon votre logique)
            data.setEnLigne(getEnLigne(conn));
            
            // 5. Total des élections
            data.setTotalElections(getTotalElections(conn));
            
            // 6. Élections ouvertes
            data.setElectionsOuvertes(getElectionsOuvertes(conn));
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    // ==========================================
    // DONNÉES POUR LE PIE CHART - PARTICIPATION
    // ==========================================

    /**
     * Récupère les données pour le graphique de participation
     */
    public List<PieChart.Data> getParticipationData() {
        List<PieChart.Data> data = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "COUNT(*) as total_votes, " +
                        "(SELECT COUNT(*) FROM users WHERE role != 'ADMIN') as total_electeurs " +
                        "FROM votes";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int votes = rs.getInt("total_votes");
                    int electeurs = rs.getInt("total_electeurs");
                    int nonVotants = electeurs - votes;
                    
                    data.add(new PieChart.Data("A voté (" + votes + ")", votes));
                    data.add(new PieChart.Data("Non votant (" + nonVotants + ")", Math.max(0, nonVotants)));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération participation: " + e.getMessage());
            e.printStackTrace();
            // Données par défaut en cas d'erreur
            data.add(new PieChart.Data("A voté", 0));
            data.add(new PieChart.Data("Non votant", 0));
        }
        
        return data;
    }

    // ==========================================
    // DONNÉES POUR LE BAR CHART - RÉSULTATS
    // ==========================================

    /**
     * Récupère les résultats des candidats pour un bar chart
     */
    public XYChart.Series<String, Number> getResultatsData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Voix par candidat");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "CONCAT(u.prenom, ' ', u.nom) as candidat_nom, " +
                        "COUNT(v.id) as nombre_voix " +
                        "FROM candidats c " +
                        "JOIN users u ON c.user_id = u.id " +
                        "LEFT JOIN votes v ON c.id = v.candidat_id " +
                        "GROUP BY c.id, u.prenom, u.nom " +
                        "ORDER BY nombre_voix DESC " +
                        "LIMIT 10";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String nom = rs.getString("candidat_nom");
                    int voix = rs.getInt("nombre_voix");
                    series.getData().add(new XYChart.Data<>(nom, voix));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération résultats: " + e.getMessage());
            e.printStackTrace();
            // Données par défaut
            series.getData().add(new XYChart.Data<>("Aucune donnée", 0));
        }
        
        return series;
    }

    /**
     * Récupère les résultats pour une élection spécifique
     */
    public XYChart.Series<String, Number> getResultatsByElection(int electionId) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Résultats");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "CONCAT(u.prenom, ' ', u.nom) as candidat_nom, " +
                        "COUNT(v.id) as nombre_voix " +
                        "FROM candidats c " +
                        "JOIN users u ON c.user_id = u.id " +
                        "LEFT JOIN votes v ON c.id = v.candidat_id " +
                        "WHERE c.election_id = ? " +
                        "GROUP BY c.id, u.prenom, u.nom " +
                        "ORDER BY nombre_voix DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, electionId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String nom = rs.getString("candidat_nom");
                        int voix = rs.getInt("nombre_voix");
                        series.getData().add(new XYChart.Data<>(nom, voix));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération résultats par élection: " + e.getMessage());
            e.printStackTrace();
        }
        
        return series;
    }

    // ==========================================
    // DONNÉES POUR LE LINE CHART - TENDANCE
    // ==========================================

    /**
     * Récupère la tendance des votes sur 7 jours
     */
    public XYChart.Series<String, Number> getTendanceData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Votes par jour");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "DATE(date_vote) as jour, " +
                        "COUNT(*) as nombre_votes " +
                        "FROM votes " +
                        "WHERE date_vote >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(date_vote) " +
                        "ORDER BY jour";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                // Préparer les 7 derniers jours
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                
                // Créer un map pour les jours
                java.util.Map<String, Integer> votesByDay = new java.util.HashMap<>();
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    String key = date.format(formatter);
                    votesByDay.put(key, 0);
                }
                
                // Remplir avec les données
                while (rs.next()) {
                    Date date = rs.getDate("jour");
                    if (date != null) {
                        String key = date.toLocalDate().format(formatter);
                        if (votesByDay.containsKey(key)) {
                            votesByDay.put(key, rs.getInt("nombre_votes"));
                        }
                    }
                }
                
                // Ajouter dans le series
                for (java.util.Map.Entry<String, Integer> entry : votesByDay.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération tendance: " + e.getMessage());
            e.printStackTrace();
            // Données par défaut
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                series.getData().add(new XYChart.Data<>(date.format(DateTimeFormatter.ofPattern("dd/MM")), 0));
            }
        }
        
        return series;
    }

    /**
     * Récupère la tendance pour une élection spécifique
     */
    public XYChart.Series<String, Number> getTendanceByElection(int electionId) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Votes par jour");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "DATE(date_vote) as jour, " +
                        "COUNT(*) as nombre_votes " +
                        "FROM votes v " +
                        "JOIN candidats c ON v.candidat_id = c.id " +
                        "WHERE c.election_id = ? AND date_vote >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(date_vote) " +
                        "ORDER BY jour";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, electionId);
                try (ResultSet rs = ps.executeQuery()) {
                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                    
                    java.util.Map<String, Integer> votesByDay = new java.util.HashMap<>();
                    for (int i = 6; i >= 0; i--) {
                        LocalDate date = today.minusDays(i);
                        votesByDay.put(date.format(formatter), 0);
                    }
                    
                    while (rs.next()) {
                        Date date = rs.getDate("jour");
                        if (date != null) {
                            String key = date.toLocalDate().format(formatter);
                            if (votesByDay.containsKey(key)) {
                                votesByDay.put(key, rs.getInt("nombre_votes"));
                            }
                        }
                    }
                    
                    for (java.util.Map.Entry<String, Integer> entry : votesByDay.entrySet()) {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération tendance par élection: " + e.getMessage());
            e.printStackTrace();
        }
        
        return series;
    }

    // ==========================================
    // DONNÉES POUR LE PIE CHART - RÉPARTITION
    // ==========================================

    /**
     * Récupère la répartition des électeurs par UFR
     */
    public List<PieChart.Data> getRepartitionElecteursData() {
        List<PieChart.Data> data = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "u.nom as ufr_nom, " +
                        "COUNT(usr.id) as nombre_etudiants " +
                        "FROM ufr u " +
                        "LEFT JOIN users usr ON u.id = usr.ufr_id AND usr.role = 'ETUDIANT' " +
                        "GROUP BY u.id, u.nom " +
                        "ORDER BY nombre_etudiants DESC";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String nom = rs.getString("ufr_nom");
                    int count = rs.getInt("nombre_etudiants");
                    data.add(new PieChart.Data(nom + " (" + count + ")", count));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération répartition: " + e.getMessage());
            e.printStackTrace();
            // Données par défaut
            data.add(new PieChart.Data("Aucune donnée", 1));
        }
        
        return data;
    }

    /**
     * Récupère la répartition des électeurs par niveau
     */
    public List<PieChart.Data> getRepartitionByNiveau() {
        List<PieChart.Data> data = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "niveau, " +
                        "COUNT(*) as nombre " +
                        "FROM users " +
                        "WHERE role = 'ETUDIANT' AND niveau IS NOT NULL " +
                        "GROUP BY niveau " +
                        "ORDER BY niveau";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String niveau = rs.getString("niveau");
                    int count = rs.getInt("nombre");
                    data.add(new PieChart.Data(niveau + " (" + count + ")", count));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération répartition par niveau: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    // ==========================================
    // STATISTIQUES SPÉCIFIQUES
    // ==========================================

    /**
     * Récupère les statistiques pour une élection
     */
    public StatsData getElectionStats(int electionId) {
        StatsData data = new StatsData();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Nombre de votes pour cette élection
            String sqlVotes = "SELECT COUNT(*) FROM votes v " +
                             "JOIN candidats c ON v.candidat_id = c.id " +
                             "WHERE c.election_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlVotes)) {
                ps.setInt(1, electionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data.setTotalVotes(rs.getInt(1));
                    }
                }
            }
            
            // Nombre de candidats
            String sqlCandidats = "SELECT COUNT(*) FROM candidats WHERE election_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCandidats)) {
                ps.setInt(1, electionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Pas de setter pour candidats, on l'ignore
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération stats élection: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    /**
     * Récupère le nombre de votes par jour pour une période donnée
     */
    public java.util.Map<String, Integer> getVotesByDay(int days) {
        java.util.Map<String, Integer> votesByDay = new java.util.LinkedHashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " +
                        "DATE(date_vote) as jour, " +
                        "COUNT(*) as nombre_votes " +
                        "FROM votes " +
                        "WHERE date_vote >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                        "GROUP BY DATE(date_vote) " +
                        "ORDER BY jour";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, days);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Date date = rs.getDate("jour");
                        if (date != null) {
                            String key = date.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                            votesByDay.put(key, rs.getInt("nombre_votes"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération votes par jour: " + e.getMessage());
            e.printStackTrace();
        }
        
        return votesByDay;
    }

    // ==========================================
    // MÉTHODES PRIVÉES POUR LES KPI
    // ==========================================

    private int getTotalVotes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM votes";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalElecteurs(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ETUDIANT'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getEnLigne(Connection conn) throws SQLException {
        // Simulé - vous pouvez implémenter une logique de session
        // Pour l'instant, on retourne un nombre aléatoire ou 0
        return 0;
    }

    private int getTotalElections(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getElectionsOuvertes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM elections WHERE statut = 'Ouverte' AND NOW() BETWEEN date_debut AND date_fin";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ==========================================
    // MÉTHODES POUR LE DASHBOARD ADMIN
    // ==========================================

    /**
     * Récupère les statistiques globales pour l'admin
     */
    public java.util.Map<String, Object> getAdminDashboardStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Total utilisateurs
            stats.put("totalUsers", getTotalUsers(conn));
            
            // Total étudiants
            stats.put("totalEtudiants", getTotalEtudiants(conn));
            
            // Total enseignants
            stats.put("totalEnseignants", getTotalEnseignants(conn));
            
            // Total admins
            stats.put("totalAdmins", getTotalAdmins(conn));
            
            // Total élections
            stats.put("totalElections", getTotalElections(conn));
            
            // Total votes
            stats.put("totalVotes", getTotalVotes(conn));
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }

    private int getTotalUsers(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalEtudiants(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ETUDIANT'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalEnseignants(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ENSEIGNANT'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getTotalAdmins(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ==========================================
    // MÉTHODES DE NETTOYAGE
    // ==========================================

    /**
     * Ferme toutes les ressources
     */
    public void close() {
        // Rien à fermer spécifiquement car on utilise try-with-resources
    }
}