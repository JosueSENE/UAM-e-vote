package app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modèle de données représentant une élection au sein de l'UAM.
 * Correspond à la table 'elections' de la base de données.
 */
public class Election {
    
    // ==========================================
    // ATTRIBUTS
    // ==========================================
    
    private int id;
    private String titre;
    private String typeElection; // DELEGUE, CHEF_DEPARTEMENT, DIRECTEUR_UFR, REPRESENTANT
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut; // En préparation, Ouverte, Fermée
    
    // Cibles de l'élection (NULL = toutes)
    private Integer cibleUfrId;
    private Integer cibleDepartementId;
    private Integer cibleFiliereId;
    private String cibleNiveau; // L1, L2, L3, M1, M2
    
    // Champs additionnels pour les jointures (noms des cibles)
    private String cibleUfrNom;
    private String cibleDepartementNom;
    private String cibleFiliereNom;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    /**
     * Constructeur par défaut (sans arguments).
     * Indispensable pour l'instanciation progressive (ex: dans ElectionDAO avec les setters).
     */
    public Election() {}

    /**
     * Constructeur complet
     */
    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin,
                    String statut, Integer cibleUfrId, Integer cibleDepartementId, 
                    Integer cibleFiliereId, String cibleNiveau) {
        this.id = id;
        this.titre = titre;
        this.typeElection = typeElection;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.cibleUfrId = cibleUfrId;
        this.cibleDepartementId = cibleDepartementId;
        this.cibleFiliereId = cibleFiliereId;
        this.cibleNiveau = cibleNiveau;
    }

    /**
     * Constructeur simplifié pour création rapide
     */
    public Election(String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.titre = titre;
        this.typeElection = typeElection;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = calculerStatut();
    }

    // ==========================================
    // CALCUL DYNAMIQUE DU STATUT
    // ==========================================

    /**
     * Calcule le statut actuel de l'élection en fonction de la date et heure courantes
     * @return "En préparation", "Ouverte" ou "Fermée"
     */
    public String calculerStatut() {
        if (dateDebut == null || dateFin == null) {
            return "En préparation";
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        
        if (maintenant.isBefore(dateDebut)) {
            return "En préparation";
        } else if (maintenant.isAfter(dateFin)) {
            return "Fermée";
        } else {
            return "Ouverte";
        }
    }

    /**
     * Met à jour le statut en fonction de la date courante
     * @return true si le statut a changé
     */
    public boolean mettreAJourStatut() {
        String nouveauStatut = calculerStatut();
        if (!nouveauStatut.equals(this.statut)) {
            this.statut = nouveauStatut;
            return true;
        }
        return false;
    }

    // ==========================================
    // MÉTHODES DE VÉRIFICATION
    // ==========================================

    public boolean estOuverte() {
        return "Ouverte".equals(statut);
    }
    
    public boolean estFermee() {
        return "Fermée".equals(statut);
    }
    
    public boolean enPreparation() {
        return "En préparation".equals(statut);
    }

    public boolean estTerminee() {
        return estFermee();
    }

    public boolean estEnCours() {
        return estOuverte();
    }

    public boolean estAccessible() {
        return estOuverte() && !estFermee();
    }

    /**
     * Vérifie si l'élection est ciblée sur un UFR spécifique
     */
    public boolean estCibleeSurUfr() {
        return cibleUfrId != null && cibleUfrId > 0;
    }

    /**
     * Vérifie si l'élection est ciblée sur un département spécifique
     */
    public boolean estCibleeSurDepartement() {
        return cibleDepartementId != null && cibleDepartementId > 0;
    }

    /**
     * Vérifie si l'élection est ciblée sur une filière spécifique
     */
    public boolean estCibleeSurFiliere() {
        return cibleFiliereId != null && cibleFiliereId > 0;
    }

    /**
     * Vérifie si l'élection est ciblée sur un niveau spécifique
     */
    public boolean estCibleeSurNiveau() {
        return cibleNiveau != null && !cibleNiveau.isEmpty();
    }

    /**
     * Vérifie si l'élection est générale (pas de cible spécifique)
     */
    public boolean estGenerale() {
        return !estCibleeSurUfr() && !estCibleeSurDepartement() && 
               !estCibleeSurFiliere() && !estCibleeSurNiveau();
    }

    // ==========================================
    // MÉTHODES DE FORMATAGE
    // ==========================================

    /**
     * Retourne la date de début formatée
     */
    public String getDateDebutFormatee() {
        if (dateDebut == null) return "Non définie";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateDebut.format(formatter);
    }

    /**
     * Retourne la date de fin formatée
     */
    public String getDateFinFormatee() {
        if (dateFin == null) return "Non définie";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateFin.format(formatter);
    }

    /**
     * Retourne la description de la cible
     */
    public String getCibleDescription() {
        if (estGenerale()) {
            return "Tous les utilisateurs";
        }
        
        StringBuilder desc = new StringBuilder();
        if (cibleUfrNom != null && !cibleUfrNom.isEmpty()) {
            desc.append("UFR: ").append(cibleUfrNom);
        }
        if (cibleDepartementNom != null && !cibleDepartementNom.isEmpty()) {
            if (desc.length() > 0) desc.append(", ");
            desc.append("Département: ").append(cibleDepartementNom);
        }
        if (cibleFiliereNom != null && !cibleFiliereNom.isEmpty()) {
            if (desc.length() > 0) desc.append(", ");
            desc.append("Filière: ").append(cibleFiliereNom);
        }
        if (cibleNiveau != null && !cibleNiveau.isEmpty()) {
            if (desc.length() > 0) desc.append(", ");
            desc.append("Niveau: ").append(cibleNiveau);
        }
        
        return desc.toString();
    }

    /**
     * Retourne le statut avec un emoji
     */
    public String getStatutAvecEmoji() {
        if (estOuverte()) return "🟢 Ouverte";
        if (estFermee()) return "🔴 Fermée";
        return "🟡 En préparation";
    }

    /**
     * Retourne la couleur CSS associée au statut
     */
    public String getStatutCouleur() {
        if (estOuverte()) return "green";
        if (estFermee()) return "red";
        return "orange";
    }

    // ==========================================
    // GETTERS ET SETTERS
    // ==========================================

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getTitre() { 
        return titre; 
    }
    
    public void setTitre(String titre) { 
        this.titre = titre; 
    }

    public String getTypeElection() { 
        return typeElection; 
    }
    
    public void setTypeElection(String typeElection) { 
        this.typeElection = typeElection; 
    }

    public LocalDateTime getDateDebut() { 
        return dateDebut; 
    }
    
    public void setDateDebut(LocalDateTime dateDebut) { 
        this.dateDebut = dateDebut; 
    }

    public LocalDateTime getDateFin() { 
        return dateFin; 
    }
    
    public void setDateFin(LocalDateTime dateFin) { 
        this.dateFin = dateFin; 
    }

    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getCibleUfrId() {
        return cibleUfrId;
    }
    
    public void setCibleUfrId(Integer cibleUfrId) {
        this.cibleUfrId = cibleUfrId;
    }

    public Integer getCibleDepartementId() {
        return cibleDepartementId;
    }
    
    public void setCibleDepartementId(Integer cibleDepartementId) {
        this.cibleDepartementId = cibleDepartementId;
    }

    public Integer getCibleFiliereId() {
        return cibleFiliereId;
    }
    
    public void setCibleFiliereId(Integer cibleFiliereId) {
        this.cibleFiliereId = cibleFiliereId;
    }

    public String getCibleNiveau() {
        return cibleNiveau;
    }
    
    public void setCibleNiveau(String cibleNiveau) {
        this.cibleNiveau = cibleNiveau;
    }

    // ==========================================
    // GETTERS POUR LES NOMS DES CIBLES (JOINTURES)
    // ==========================================

    public String getCibleUfrNom() {
        return cibleUfrNom;
    }
    
    public void setCibleUfrNom(String cibleUfrNom) {
        this.cibleUfrNom = cibleUfrNom;
    }

    public String getCibleDepartementNom() {
        return cibleDepartementNom;
    }
    
    public void setCibleDepartementNom(String cibleDepartementNom) {
        this.cibleDepartementNom = cibleDepartementNom;
    }

    public String getCibleFiliereNom() {
        return cibleFiliereNom;
    }
    
    public void setCibleFiliereNom(String cibleFiliereNom) {
        this.cibleFiliereNom = cibleFiliereNom;
    }

    // ==========================================
    // MÉTHODES DE CONVERSION
    // ==========================================

    /**
     * Vérifie si l'élection est valide
     */
    public boolean isValid() {
        if (titre == null || titre.trim().isEmpty()) return false;
        if (typeElection == null || typeElection.trim().isEmpty()) return false;
        if (dateDebut == null || dateFin == null) return false;
        if (dateDebut.isAfter(dateFin)) return false;
        return true;
    }

    /**
     * Vérifie si l'élection concerne un utilisateur donné
     */
    public boolean concerneUtilisateur(User user) {
        if (user == null) return false;
        
        // Si l'élection est générale, tout le monde est concerné
        if (estGenerale()) return true;
        
        // Vérifier les cibles
        if (estCibleeSurUfr() && user.getUfrId() != null) {
            if (!cibleUfrId.equals(user.getUfrId())) return false;
        }
        
        if (estCibleeSurDepartement() && user.getFiliereId() != null) {
            // Récupérer le département de l'utilisateur via sa filière
            // Cette vérification nécessite une requête supplémentaire
            // On laisse le DAO gérer ce cas
        }
        
        if (estCibleeSurFiliere() && user.getFiliereId() != null) {
            if (!cibleFiliereId.equals(user.getFiliereId())) return false;
        }
        
        if (estCibleeSurNiveau() && user.getNiveau() != null) {
            if (!cibleNiveau.equals(user.getNiveau())) return false;
        }
        
        return true;
    }

    // ==========================================
    // MÉTHODES OVERRIDE
    // ==========================================

    @Override
    public String toString() {
        return titre + " (" + typeElection + ") - [" + statut + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Election election = (Election) o;
        return id == election.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}