package app.controller;

import app.dao.*;
import app.model.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiqueController {

    private final UserDAO userDAO;
    private final VoteDAO voteDAO;
    private final ElectionDAO electionDAO;
    private final CandidatDAO candidatDAO;
    private final UfrDAO ufrDAO;
    private final DepartementDAO departementDAO;
    private final FiliereDAO filiereDAO;

    public StatistiqueController() {
        this.userDAO = new UserDAO();
        this.voteDAO = new VoteDAO();
        this.electionDAO = new ElectionDAO();
        this.candidatDAO = new CandidatDAO();
        this.ufrDAO = new UfrDAO();
        this.departementDAO = new DepartementDAO();
        this.filiereDAO = new FiliereDAO();
    }

    // =========================================================================
    // 1. STATISTIQUES GLOBALES DU TABLEAU DE BORD (DASHBOARD)
    // =========================================================================

    /**
     * Récupère un résumé complet des chiffres clés de l'application UAM e-Vote.
     * Pratique pour remplir les cartes (cards) du tableau de bord.
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Utilisateurs
            int totalUsers = userDAO.getTotalUsers();
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();

            // Votes et participation
            int totalVotants = voteDAO.getVotants();
            int totalNonVotants = voteDAO.getNonVotants();

            // Calcul du taux de participation global
            double tauxParticipation = (totalUsers > 0) 
                    ? ((double) totalVotants / totalUsers) * 100.0 
                    : 0.0;

            // Structure académique
            int totalUfr = ufrDAO.getAllUfr().size();
            int totalDepartements = departementDAO.getAllDepartements().size();
            int totalFilieres = filiereDAO.getAllFilieres().size();

            // Injection des données
            stats.put("totalUsers", totalUsers);
            stats.put("totalEtudiants", totalEtudiants);
            stats.put("totalEnseignants", totalEnseignants);
            stats.put("totalVotants", totalVotants);
            stats.put("totalNonVotants", totalNonVotants);
            stats.put("tauxParticipation", Math.round(tauxParticipation * 100.0) / 100.0);
            stats.put("totalUfr", totalUfr);
            stats.put("totalDepartements", totalDepartements);
            stats.put("totalFilieres", totalFilieres);

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des statistiques du tableau de bord : " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // =========================================================================
    // 2. RÉSULTATS ET PARTICIPATION PAR ÉLECTION
    // =========================================================================

    /**
     * Récupère les résultats détaillés d'une élection spécifique (Nom du candidat -> Nombre de voix).
     */
    public Map<String, Integer> getElectionResults(int electionId) {
        return voteDAO.getResults(electionId);
    }

    /**
     * Calcule le pourcentage de participation pour une élection donnée.
     */
    public double getTauxParticipationByElection(int electionId) {
        try {
            int totalInscrits = userDAO.getTotalUsers();
            if (totalInscrits == 0) return 0.0;

            // On récupère le total des voix enregistrées pour cette élection
            Map<String, Integer> resultats = voteDAO.getResults(electionId);
            int votesExprimes = resultats.values().stream().mapToInt(Integer::intValue).sum();

            double taux = ((double) votesExprimes / totalInscrits) * 100.0;
            return Math.round(taux * 100.0) / 100.0;

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du taux de participation pour l'élection ID " + electionId);
            e.printStackTrace();
        }
        return 0.0;
    }

    // =========================================================================
    // 3. DÉCOMPTE PAR CATÉGORIE ACADÉMIQUE
    // =========================================================================

    /**
     * Retourne la répartition des étudiants par niveau (L1, L2, L3, M1, M2...).
     */
    public Map<String, Integer> getRepartitionParNiveau() {
        Map<String, Integer> repartition = new HashMap<>();
        List<User> users = userDAO.getAllUsers();

        for (User u : users) {
            if ("ETUDIANT".equalsIgnoreCase(u.getProfession()) && u.getNiveau() != null) {
                String niveau = u.getNiveau();
                repartition.put(niveau, repartition.getOrDefault(niveau, 0) + 1);
            }
        }
        return repartition;
    }

    /**
     * Vérifie si un utilisateur donné a déjà voté à une élection.
     */
    public boolean userHasVoted(int electionId, int userId) {
        return voteDAO.hasUserVoted(electionId, userId);
    }
}