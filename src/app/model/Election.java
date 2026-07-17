package app.model;

import java.time.LocalDateTime;

//Modèle de données représentant une élection au sein de l'UAM.

public class Election {
    private int id;
    private String titre;
    private String typeElection;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private Integer cible_ufr_id;
    private Integer cible_departemennt_id;
    private Integer cible_filiere_id;
    private String cible_niveau;

    /**
     * Constructeur par défaut (sans arguments).
     * Indispensable pour l'instanciation progressive (ex: dans ElectionDAO avec les setters).
     */
    public Election() {}
    
    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin,
        String statut,Integer cible_ufr_id, Integer cible_departemennt_id, Integer cible_filiere_id, String cible_niveau) {
        this.id = id;
        this.titre = titre;
        this.typeElection = typeElection;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.cible_ufr_id = cible_ufr_id;
        this.cible_departemennt_id = cible_departemennt_id;
        this.cible_filiere_id = cible_filiere_id;
        this.cible_niveau = cible_niveau;
    }

    //  LE CALCUL DYNAMIQUE DU STATUT

    public String calculerStatut() {
        LocalDateTime maintenant = LocalDateTime.now();
        if (maintenant.isBefore(this.dateDebut)) {return "En préparation";}
        else if (maintenant.isAfter(this.dateFin)) {return "Fermée";}
        else {return "Ouverte";}    
    }

    //  Méthodes d'aide très pratiques pour vos contrôleurs JavaFX

    public boolean estOuverte() {return "Ouverte".equals(statut);}
    public boolean estFermee() {return "Fermée".equals(statut);}
    public boolean enPreparation() {return "En préparation".equals(statut);}

    //GETTER ET SETTER

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getTypeElection() { return typeElection; }
    public void setTypeElection(String typeElection) { this.typeElection = typeElection; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getStatut() {return statut;}
    public void setStatut(String statut) {this.statut = statut;}

    public Integer getCible_ufr_id() {return cible_ufr_id;}
    public void setCible_ufr_id(Integer cible_ufr_id) {this.cible_ufr_id = cible_ufr_id;}

    public Integer getCible_departemennt_id() {return cible_departemennt_id;}
    public void setCible_departemennt_id(Integer cible_departemennt_id) {this.cible_departemennt_id = cible_departemennt_id;}
    
    public Integer getCible_filiere_id() {return cible_filiere_id;}
    public void setCible_filiere_id(Integer cible_filiere_id) {this.cible_filiere_id = cible_filiere_id;}
    
    public String getCible_niveau() {return cible_niveau;}
    public void setCible_niveau(String cible_niveau) {this.cible_niveau = cible_niveau;}

    @Override
    public String toString() {return titre + " (" + typeElection + ") - [" + statut + "]";}
}