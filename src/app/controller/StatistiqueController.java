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
    private final AdminDAO adminDAO; // Ajout pour les statistiques des admins

    public StatistiqueController() {
        this.userDAO = new UserDAO();
        this.voteDAO = new VoteDAO();
        this.electionDAO = new ElectionDAO();
        this.candidatDAO = new CandidatDAO();
        this.ufrDAO = new UfrDAO();
        this.departementDAO = new DepartementDAO();
        this.filiereDAO = new FiliereDAO();
        this.adminDAO = new AdminDAO(); // Initialisation
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
            // Utilisateurs (exclut les admins)
            int totalUsers = userDAO.getTotalUsers();
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();
            int totalAdmins = getTotalAdmins();

            // Votes et participation
            int totalVotants = voteDAO.getVotants();
            int totalNonVotants = Math.max(0, totalUsers - totalVotants);

            // Calcul du taux de participation global
            double tauxParticipation = (totalUsers > 0) 
                    ? ((double) totalVotants / totalUsers) * 100.0 
                    : 0.0;

            // Structure académique
            int totalUfr = ufrDAO.getAllUfr().size();
            int totalDepartements = departementDAO.getAllDepartements().size();
            int totalFilieres = filiereDAO.getAllFilieres().size();

            // Élections
            int totalElections = electionDAO.getAllElections().size();
            int electionsEnCours = electionDAO.getElectionsEnCours().size();
            int electionsTerminees = electionDAO.getElectionsTerminees().size();

            // Candidats
            int totalCandidats = candidatDAO.getTotalCandidats();

            // Injection des données
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
            System.err.println("❌ Erreur lors du chargement des statistiques du tableau de bord : " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // =========================================================================
    // 2. RÉSULTATS ET PARTICIPATION PAR ÉLECTION
    // =========================================================================

    /**
     * Récupère les résultats détaillés d'une élection spécifique 
     * (Nom du candidat -> Nombre de voix).
     */
    public Map<String, Integer> getElectionResults(int electionId) {
        return voteDAO.getResults(electionId);
    }

    /**
     * Récupère les résultats détaillés d'une élection avec les informations des candidats
     */
    public Map<Candidat, Integer> getElectionResultsWithCandidats(int electionId) {
        Map<Candidat, Integer> resultats = new HashMap<>();
        
        try {
            // Récupérer tous les candidats de l'élection
            List<Candidat> candidats = candidatDAO.getCandidatesForElection(electionId);
            
            // Pour chaque candidat, récupérer son nombre de votes
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
     * Calcule le pourcentage de participation pour une élection donnée.
     */
    public double getTauxParticipationByElection(int electionId) {
        try {
            // Récupérer le nombre d'électeurs ciblés pour cette élection
            int totalInscrits = electionDAO.getElecteursCountForElection(electionId);
            if (totalInscrits == 0) return 0.0;

            // Récupérer le total des votes pour cette élection
            int votesExprimes = voteDAO.getVotesCountForElection(electionId);

            double taux = ((double) votesExprimes / totalInscrits) * 100.0;
            return Math.round(taux * 100.0) / 100.0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du calcul du taux de participation pour l'élection ID " + electionId);
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Récupère le nombre de votes pour chaque candidat d'une élection
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

    // =========================================================================
    // 3. DÉCOMPTE PAR CATÉGORIE ACADÉMIQUE
    // =========================================================================

    /**
     * Retourne la répartition des étudiants par niveau (L1, L2, L3, M1, M2...).
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
     * Retourne la répartition des étudiants par filière.
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
     * Retourne la répartition des étudiants par UFR.
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
     * Retourne la répartition des utilisateurs par rôle.
     */
    public Map<String, Integer> getRepartitionParRole() {
        Map<String, Integer> repartition = new HashMap<>();
        
        try {
            int totalEtudiants = userDAO.getTotalEtudiants();
            int totalEnseignants = userDAO.getTotalEnseignants();
            int totalAdmins = getTotalAdmins();
            
            repartition.put("Étudiants", totalEtudiants);
            repartition.put("Enseignants", totalEnseignants);
            repartition.put("Administrateurs", totalAdmins);
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la répartition par rôle");
            e.printStackTrace();
        }
        
        return repartition;
    }

    // =========================================================================
    // 4. STATISTIQUES DE PARTICIPATION
    // =========================================================================

    /**
     * Vérifie si un utilisateur donné a déjà voté à une élection.
     */
    public boolean userHasVoted(int electionId, int userId) {
        return voteDAO.hasUserVoted(electionId, userId);
    }

    /**
     * Récupère le taux de participation par élection avec les détails.
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
     * Récupère la liste des utilisateurs qui n'ont pas voté pour une élection.
     */
    public List<User> getNonVotantsForElection(int electionId) {
        return voteDAO.getNonVotantsForElection(electionId);
    }

    // =========================================================================
    // 5. STATISTIQUES ACADÉMIQUES AVANCÉES
    // =========================================================================

    /**
     * Récupère le nombre d'étudiants par niveau et par filière.
     */
    public Map<String, Map<String, Integer>> getRepartitionParNiveauEtFiliere() {
        Map<String, Map<String, Integer>> repartition = new HashMap<>();
        List<User> users = userDAO.getAllEtudiants();

        for (User u : users) {
            String niveau = u.getNiveau();
            String filiere = u.getFiliereNom();
            
            if (niveau != null && !niveau.isEmpty() && filiere != null && !filiere.isEmpty()) {
                // Initialiser le niveau si non existant
                repartition.putIfAbsent(niveau, new HashMap<>());
                
                Map<String, Integer> filieresMap = repartition.get(niveau);
                filieresMap.put(filiere, filieresMap.getOrDefault(filiere, 0) + 1);
            }
        }
        
        return repartition;
    }

    /**
     * Calcule le taux de participation par UFR pour une élection.
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

    // =========================================================================
    // 6. STATISTIQUES D'ADMINISTRATION
    // =========================================================================

    /**
     * Récupère le nombre total d'administrateurs.
     */
    private int getTotalAdmins() {
        try {
            return adminDAO.getTotalAdmins();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des administrateurs");
            return 0;
        }
    }

    /**
     * Récupère la répartition des élections par statut.
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

    // =========================================================================
    // 7. MÉTHODES UTILITAIRES
    // =========================================================================

    /**
     * Génère un rapport complet des statistiques.
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
     * Formate un pourcentage pour l'affichage.
     */
    public String formatPercentage(double value) {
        return String.format("%.2f%%", value);
    }

    /**
     * Formate un nombre avec des séparateurs de milliers.
     */
    public String formatNumber(int number) {
        return String.format("%,d", number);
    }
}