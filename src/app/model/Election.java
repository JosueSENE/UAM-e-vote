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
    private int cible_ufr_id;
    private int cible_departemennt_id;
    private int cible_filiere_id;
    private int cible_niveau;

    /**
     * Constructeur par défaut (sans arguments).
     * Indispensable pour l'instanciation progressive (ex: dans ElectionDAO avec les setters).
     */
    public Election() {}
    
    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin,
            String statut, int cible_ufr_id, int cible_departemennt_id, int cible_filiere_id, int cible_niveau) {
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

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getCible_ufr_id() {return cible_ufr_id;}
    public void setCible_ufr_id(int cible_ufr_id) {this.cible_ufr_id = cible_ufr_id;}

    public int getCible_departemennt_id() {return cible_departemennt_id;}
    public void setCible_departemennt_id(int cible_departemennt_id) {this.cible_departemennt_id = cible_departemennt_id;}
    
    public int getCible_filiere_id() {return cible_filiere_id;}
    public void setCible_filiere_id(int cible_filiere_id) {this.cible_filiere_id = cible_filiere_id;}
    
    public int getCible_niveau() {return cible_niveau;}
    public void setCible_niveau(int cible_niveau) {this.cible_niveau = cible_niveau;}

    @Override
    public String toString() {
        return titre + " (" + typeElection + ") - [" + statut + "]";
    }
}