package app.model;

import java.time.LocalDateTime;

public class Election {
    private int id;
    private String titre;
    private String typeElection;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private Integer cible_ufr_id;
    private Integer cible_departement_id;
    private Integer cible_filiere_id;
    private String cible_niveau;
    private String cible_profession; 
    private Ufr ufr;
    private Departement departement;
    private Filiere filiere;

    public Election() {}
    
    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin,
            String statut, Integer cible_ufr_id, Integer cible_departement_id, Integer cible_filiere_id,
            String cible_niveau, String cible_profession, Ufr ufr, Departement departement, Filiere filiere) {
        this.id = id;
        this.titre = titre;
        this.typeElection = typeElection;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.cible_ufr_id = cible_ufr_id;
        this.cible_departement_id = cible_departement_id;
        this.cible_filiere_id = cible_filiere_id;
        this.cible_niveau = cible_niveau;
        this.cible_profession = cible_profession;
        this.ufr = ufr;
        this.departement = departement;
        this.filiere = filiere;
    }

    public String calculerStatut() {
        LocalDateTime maintenant = LocalDateTime.now();
        if (dateDebut != null && maintenant.isBefore(this.dateDebut)) { return "En préparation"; }
        else if (dateFin != null && maintenant.isAfter(this.dateFin)) { return "Fermée"; }
        else { return "Ouverte"; }    
    }

    public boolean estOuverte() { return "Ouverte".equals(statut); }
    public boolean estFermee() { return "Fermée".equals(statut); }
    public boolean enPreparation() { return "En préparation".equals(statut); }

    public String getCibleIdAffichage() {
        if (cible_ufr_id != null) return String.valueOf(cible_ufr_id);
        if (cible_departement_id != null) return String.valueOf(cible_departement_id);
        if (cible_filiere_id != null) return String.valueOf(cible_filiere_id);
        return "Tous";
    }

    public String getCibleNomAffichage() {
        if (ufr != null && ufr.getNom() != null) return ufr.getNom();
        if (departement != null && departement.getNom() != null) return departement.getNom();
        if (filiere != null && filiere.getNom() != null) return filiere.getNom();
        return "Toutes les cibles";
    }

    // Getters & Setters Standard
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

    public Integer getCible_ufr_id() { return cible_ufr_id; }
    public void setCible_ufr_id(Integer cible_ufr_id) { this.cible_ufr_id = cible_ufr_id; }

    public Integer getCible_departement_id() { return cible_departement_id; }
    public void setCible_departement_id(Integer cible_departement_id) { this.cible_departement_id = cible_departement_id; }
    
    public Integer getCible_filiere_id() { return cible_filiere_id; }
    public void setCible_filiere_id(Integer cible_filiere_id) { this.cible_filiere_id = cible_filiere_id; }
    
    public String getCible_niveau() { return cible_niveau; }
    public void setCible_niveau(String cible_niveau) { this.cible_niveau = cible_niveau; }

    public String getCible_profession() { return cible_profession; }
    public void setCible_profession(String cible_profession) { this.cible_profession = cible_profession; }

    // Alias CamelCase
    public Integer getCibleUfrId() { return cible_ufr_id; }
    public void setCibleUfrId(Integer cibleUfrId) { this.cible_ufr_id = cibleUfrId; }

    public Integer getCibleDepartementId() { return cible_departement_id; }
    public void setCibleDepartementId(Integer cibleDepartementId) { this.cible_departement_id = cibleDepartementId; }

    public Integer getCibleFiliereId() { return cible_filiere_id; }
    public void setCibleFiliereId(Integer cibleFiliereId) { this.cible_filiere_id = cibleFiliereId; }

    public String getCibleNiveau() { return cible_niveau; }
    public void setCibleNiveau(String cibleNiveau) { this.cible_niveau = cibleNiveau; }

    public String getCibleProfession() { return cible_profession; }
    public void setCibleProfession(String cibleProfession) { this.cible_profession = cibleProfession; }

    public Ufr getUfr() { return ufr; }
    public void setUfr(Ufr ufr) { this.ufr = ufr; }
    public String getUfrNom() { return (ufr != null && ufr.getNom() != null) ? ufr.getNom() : "N/A"; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }
    public String getDepartementNom() { return (departement != null && departement.getNom() != null) ? departement.getNom() : "N/A"; }

    public Filiere getFiliere() { return filiere; }
    public void setFiliere(Filiere filiere) { this.filiere = filiere; }
    public String getFiliereNom() { return (filiere != null && filiere.getNom() != null) ? filiere.getNom() : "N/A"; }

    @Override
    public String toString() { return titre + " (" + typeElection + ") - [" + statut + "]"; }
}