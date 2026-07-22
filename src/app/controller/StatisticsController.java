package app.controller;

import app.dao.*;
import app.model.*;
import app.view.StatisticsView;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController extends BorderPane {

    // ==========================================
    // DAO
    // ==========================================
    private final UserDAO userDAO;
    private final VoteDAO voteDAO;
    private final ElectionDAO electionDAO;
    private final CandidatDAO candidatDAO;
    private final UfrDAO ufrDAO;
    private final DepartementDAO departementDAO;
    private final FiliereDAO filiereDAO;
    private final AdminDAO adminDAO;
    private final StatisticsDAO statisticsDAO;
    
    // ==========================================
    // VUE
    // ==========================================
    private final StatisticsView view;

    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

    public StatisticsController() {
        // Initialisation des DAO
        this.userDAO = new UserDAO();
        this.voteDAO = new VoteDAO();
        this.electionDAO = new ElectionDAO();
        this.candidatDAO = new CandidatDAO();
        this.ufrDAO = new UfrDAO();
        this.departementDAO = new DepartementDAO();
        this.filiereDAO = new FiliereDAO();
        this.adminDAO = new AdminDAO();
        this.statisticsDAO = new StatisticsDAO();
        
        // Initialisation de la vue
        this.view = new StatisticsView();
        this.setCenter(view);
        
        // Configuration des événements
        configurerEvenements();
        
        // Chargement initial des données
        chargerStatistiques();
    }

    // ==========================================
    // CONFIGURATION DES ÉVÉNEMENTS
    // ==========================================

    private void configurerEvenements() {
        // Bouton Retour
        view.getBtnRetour().setOnAction(e -> retournerDashboard());
        
        // Bouton Rafraîchir
        view.getBtnRafraichir().setOnAction(e -> chargerStatistiques());
    }

    // ==========================================
    // CHARGEMENT DES STATISTIQUES
    // ==========================================

    private void chargerStatistiques() {
        try {
            System.out.println("🔄 Chargement des statistiques...");
            
            // 1. Charger les KPI via StatisticsDAO
            StatisticsDAO.StatsData data = statisticsDAO.getStatsData();
            view.updateKPIs(
                data.getTotalVotes(),
                data.getTotalElecteurs(),
                data.getTauxParticipation(),
                data.getEnLigne(),
                data.getTotalElections(),
                data.getElectionsOuvertes()
            );

            // 2. Charger les graphiques via StatisticsDAO
            view.updateParticipationChart(statisticsDAO.getParticipationData());
            view.updateResultatsChart(statisticsDAO.getResultatsData());
            view.updateTendanceChart(statisticsDAO.getTendanceData());
            view.updateRepartitionElecteursChart(statisticsDAO.getRepartitionElecteursData());

            System.out.println("✅ Statistiques chargées avec succès !");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
            afficherDonneesParDefaut();
        }
    }

    // ==========================================
    // DONNÉES PAR DÉFAUT (EN CAS D'ERREUR)
    // ==========================================

    private void afficherDonneesParDefaut() {
        // KPI par défaut
        view.updateKPIs(0, 0, 0.0, 0, 0, 0);
        
        // Graphiques vides
        view.updateParticipationChart(new PieChart.Data("Aucune donnée", 1));
        view.updateRepartitionElecteursChart(new PieChart.Data("Aucune donnée", 1));
        
        XYChart.Series<String, Number> emptySeries = new XYChart.Series<>();
        emptySeries.setName("Aucune donnée");
        emptySeries.getData().add(new XYChart.Data<>("", 0));
        view.updateResultatsChart(emptySeries);
        view.updateTendanceChart(emptySeries);
    }

    // ==========================================
    // NAVIGATION
    // ==========================================

    private void retournerDashboard() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            AdminDashboardController dashboard = new AdminDashboardController();
            Scene scene = new Scene(dashboard, 1400, 700);
            stage.setTitle("UAM e-Vote - Tableau de bord administrateur");
            stage.setScene(scene);
            stage.centerOnScreen();
            System.out.println("✅ Retour au tableau de bord");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour au dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // STATISTIQUES GLOBALES (METHODES EXISTANTES)
    // ==========================================

    /**
     * Récupère les statistiques globales pour le dashboard
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> stats = new HashMap<>();

        try {
            int totalUsers = userDAO.getTotalUsers();
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();
            int totalAdmins = adminDAO.getTotalAdmins();

            int totalVotants = voteDAO.getTotalVotants();
            int totalNonVotants = Math.max(0, totalUsers - totalVotants);

            double tauxParticipation = (totalUsers > 0) 
                    ? ((double) totalVotants / totalUsers) * 100.0 
                    : 0.0;

            int totalUfr = ufrDAO.getAllUfr().size();
            int totalDepartements = departementDAO.getAllDepartements().size();
            int totalFilieres = filiereDAO.getAllFilieres().size();

            int totalElections = electionDAO.getAllElections().size();
            int electionsEnCours = electionDAO.getElectionsEnCours().size();
            int electionsTerminees = electionDAO.getElectionsTerminees().size();

            int totalCandidats = candidatDAO.getTotalCandidats();

            stats.put("totalUsers", totalUsers);
            stats.put("totalEtudiants", totalEtudiants);
            stats.put("totalEnseignants", totalEnseignants);
            stats.put("totalAdmins", totalAdmins);
            stats.put("totalVotants", totalVotants);
            stats.put("totalNonVotants", totalNonVotants);
            stats.put("tauxParticipation", Math.round(tauxParticipation * 100.0) / 100.0);
            stats.put("totalUfr", totalUfr);
            stats.put("totalDepartements", totalDepartements);
            stats.put("totalFilieres", totalFilieres);
            stats.put("totalElections", totalElections);
            stats.put("electionsEnCours", electionsEnCours);
            stats.put("electionsTerminees", electionsTerminees);
            stats.put("totalCandidats", totalCandidats);

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des statistiques globales: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // ==========================================
    // RÉSULTATS PAR ÉLECTION
    // ==========================================

    /**
     * Récupère les résultats d'une élection
     */
    public Map<String, Integer> getElectionResults(int electionId) {
        return voteDAO.getResults(electionId);
    }

    /**
     * Récupère les résultats d'une élection avec les objets Candidat
     */
    public Map<Candidat, Integer> getElectionResultsWithCandidats(int electionId) {
        Map<Candidat, Integer> resultats = new HashMap<>();
        
        try {
            List<Candidat> candidats = candidatDAO.getCandidatesForElection(electionId);
            for (Candidat candidat : candidats) {
                int nbVotes = voteDAO.getVotesCountForCandidat(candidat.getId());
                resultats.put(candidat, nbVotes);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des résultats détaillés");
            e.printStackTrace();
        }
        
        return resultats;
    }

    /**
     * Calcule le taux de participation pour une élection
     */
    public double getTauxParticipationByElection(int electionId) {
        try {
            int totalInscrits = electionDAO.getElecteursCountForElection(electionId);
            if (totalInscrits == 0) return 0.0;
            int votesExprimes = voteDAO.getVotesCountForElection(electionId);
            double taux = ((double) votesExprimes / totalInscrits) * 100.0;
            return Math.round(taux * 100.0) / 100.0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du calcul du taux de participation");
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Récupère les votes par candidat pour une élection
     */
    public Map<String, Integer> getVotesParCandidat(int electionId) {
        Map<String, Integer> votes = new HashMap<>();
        
        try {
            List<Candidat> candidats = candidatDAO.getCandidatesForElection(electionId);
            for (Candidat candidat : candidats) {
                String nomComplet = candidat.getUser().getFullName();
                int nbVotes = voteDAO.getVotesCountForCandidat(candidat.getId());
                votes.put(nomComplet, nbVotes);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des votes par candidat");
            e.printStackTrace();
        }
        
        return votes;
    }

    // ==========================================
    // RÉPARTITION ACADÉMIQUE
    // ==========================================

    /**
     * Récupère la répartition des étudiants par niveau
     */
    public Map<String, Integer> getRepartitionParNiveau() {
        Map<String, Integer> repartition = new HashMap<>();
        List<User> users = userDAO.getAllEtudiants();

        for (User u : users) {
            if (u.getNiveau() != null && !u.getNiveau().isEmpty()) {
                String niveau = u.getNiveau();
                repartition.put(niveau, repartition.getOrDefault(niveau, 0) + 1);
            }
        }
        return repartition;
    }

    /**
     * Récupère la répartition des étudiants par filière
     */
    public Map<String, Integer> getRepartitionParFiliere() {
        Map<String, Integer> repartition = new HashMap<>();
        List<User> users = userDAO.getAllEtudiants();

        for (User u : users) {
            if (u.getFiliereNom() != null && !u.getFiliereNom().isEmpty()) {
                String filiere = u.getFiliereNom();
                repartition.put(filiere, repartition.getOrDefault(filiere, 0) + 1);
            }
        }
        return repartition;
    }

    /**
     * Récupère la répartition des étudiants par UFR
     */
    public Map<String, Integer> getRepartitionParUfr() {
        Map<String, Integer> repartition = new HashMap<>();
        List<User> users = userDAO.getAllEtudiants();

        for (User u : users) {
            if (u.getUfrNom() != null && !u.getUfrNom().isEmpty()) {
                String ufr = u.getUfrNom();
                repartition.put(ufr, repartition.getOrDefault(ufr, 0) + 1);
            }
        }
        return repartition;
    }

    /**
     * Récupère la répartition des utilisateurs par rôle
     */
    public Map<String, Integer> getRepartitionParRole() {
        Map<String, Integer> repartition = new HashMap<>();
        
        try {
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();
            int totalAdmins = adminDAO.getTotalAdmins();
            
            repartition.put("Étudiants", totalEtudiants);
            repartition.put("Enseignants", totalEnseignants);
            repartition.put("Administrateurs", totalAdmins);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la répartition par rôle");
            e.printStackTrace();
        }
        
        return repartition;
    }

    // ==========================================
    // PARTICIPATION
    // ==========================================

    /**
     * Vérifie si un utilisateur a voté pour une élection
     */
    public boolean userHasVoted(int electionId, int userId) {
        return voteDAO.hasUserVoted(electionId, userId);
    }

    /**
     * Récupère les détails de participation pour une élection
     */
    public Map<String, Object> getParticipationDetails(int electionId) {
        Map<String, Object> details = new HashMap<>();
        
        try {
            int totalInscrits = electionDAO.getElecteursCountForElection(electionId);
            int totalVotants = voteDAO.getVotesCountForElection(electionId);
            int totalNonVotants = Math.max(0, totalInscrits - totalVotants);
            
            double taux = (totalInscrits > 0) 
                    ? ((double) totalVotants / totalInscrits) * 100.0 
                    : 0.0;
            
            details.put("totalInscrits", totalInscrits);
            details.put("totalVotants", totalVotants);
            details.put("totalNonVotants", totalNonVotants);
            details.put("tauxParticipation", Math.round(taux * 100.0) / 100.0);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du calcul des détails de participation");
            e.printStackTrace();
        }
        
        return details;
    }

    /**
     * Récupère la liste des non-votants pour une élection
     */
    public List<User> getNonVotantsForElection(int electionId) {
        return voteDAO.getNonVotantsForElection(electionId);
    }

    // ==========================================
    // STATISTIQUES AVANCÉES
    // ==========================================

    /**
     * Récupère la répartition par niveau et filière
     */
    public Map<String, Map<String, Integer>> getRepartitionParNiveauEtFiliere() {
        Map<String, Map<String, Integer>> repartition = new HashMap<>();
        List<User> users = userDAO.getAllEtudiants();

        for (User u : users) {
            String niveau = u.getNiveau();
            String filiere = u.getFiliereNom();
            
            if (niveau != null && !niveau.isEmpty() && filiere != null && !filiere.isEmpty()) {
                repartition.putIfAbsent(niveau, new HashMap<>());
                Map<String, Integer> filieresMap = repartition.get(niveau);
                filieresMap.put(filiere, filieresMap.getOrDefault(filiere, 0) + 1);
            }
        }
        
        return repartition;
    }

    /**
     * Récupère le taux de participation par UFR pour une élection
     */
    public Map<String, Double> getTauxParticipationParUfr(int electionId) {
        Map<String, Double> tauxParUfr = new HashMap<>();
        
        try {
            List<Ufr> ufrs = ufrDAO.getAllUfr();
            
            for (Ufr ufr : ufrs) {
                int totalInscrits = electionDAO.getElecteursCountForElectionAndUfr(electionId, ufr.getId());
                int totalVotants = voteDAO.getVotesCountForElectionAndUfr(electionId, ufr.getId());
                
                double taux = (totalInscrits > 0) 
                        ? ((double) totalVotants / totalInscrits) * 100.0 
                        : 0.0;
                
                tauxParUfr.put(ufr.getNom(), Math.round(taux * 100.0) / 100.0);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du calcul du taux de participation par UFR");
            e.printStackTrace();
        }
        
        return tauxParUfr;
    }

    // ==========================================
    // STATISTIQUES D'ADMINISTRATION
    // ==========================================

    /**
     * Récupère la répartition des élections par statut
     */
    public Map<String, Integer> getRepartitionElectionsParStatut() {
        Map<String, Integer> repartition = new HashMap<>();
        
        try {
            int enPreparation = electionDAO.countElectionsByStatut("En préparation");
            int ouvertes = electionDAO.countElectionsByStatut("Ouverte");
            int fermees = electionDAO.countElectionsByStatut("Fermée");
            
            repartition.put("En préparation", enPreparation);
            repartition.put("Ouvertes", ouvertes);
            repartition.put("Fermées", fermees);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la répartition des élections");
            e.printStackTrace();
        }
        
        return repartition;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Génère un rapport complet de toutes les statistiques
     */
    public Map<String, Object> getRapportComplet() {
        Map<String, Object> rapport = new HashMap<>();
        
        rapport.put("dashboardSummary", getDashboardSummary());
        rapport.put("repartitionParNiveau", getRepartitionParNiveau());
        rapport.put("repartitionParFiliere", getRepartitionParFiliere());
        rapport.put("repartitionParUfr", getRepartitionParUfr());
        rapport.put("repartitionParRole", getRepartitionParRole());
        rapport.put("repartitionElections", getRepartitionElectionsParStatut());
        rapport.put("repartitionNiveauFiliere", getRepartitionParNiveauEtFiliere());
        
        return rapport;
    }

    /**
     * Formate un pourcentage
     */
    public String formatPercentage(double value) {
        return String.format("%.2f%%", value);
    }

    /**
     * Formate un nombre avec séparateurs de milliers
     */
    public String formatNumber(int number) {
        return String.format("%,d", number);
    }

    // ==========================================
    // NETTOYAGE
    // ==========================================

    public void cleanup() {
        if (view != null) {
            view.cleanup();
        }
    }
}