package app.model;

import java.time.LocalDateTime;

public class Election {
    private int id;
    private String titre;
    private String typeElection;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private Integer cibleUfrId;
    private Integer cibleDepartementId;
    private Integer cibleFiliereId;
    private String cibleNiveau;
    private String cibleProfession; 
    private Ufr ufr;
    private Departement departement;
    private Filiere filiere;

    public Election() {}
    
    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, LocalDateTime dateFin,
            String statut, Integer cibleUfrId, Integer cibleDepartementId, Integer cibleFiliereId,
            String cibleNiveau, String cibleProfession, Ufr ufr, Departement departement, Filiere filiere) {
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
        this.cibleProfession = cibleProfession;
        this.ufr = ufr;
        this.departement = departement;
        this.filiere = filiere;
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

    public Integer getCibleUfrId() { return cibleUfrId; }
    public void setCibleUfrId(Integer cibleUfrId) { this.cibleUfrId = cibleUfrId; }

    public Integer getCibleDepartementId() { return cibleDepartementId; }
    public void setCibleDepartementId(Integer cibleDepartementId) { this.cibleDepartementId = cibleDepartementId; }

    public Integer getCibleFiliereId() { return cibleFiliereId; }
    public void setCibleFiliereId(Integer cibleFiliereId) { this.cibleFiliereId = cibleFiliereId; }

    public String getCibleNiveau() { return cibleNiveau; }
    public void setCibleNiveau(String cibleNiveau) { this.cibleNiveau = cibleNiveau; }

    public String getCibleProfession() { return cibleProfession; }
    public void setCibleProfession(String cibleProfession) { this.cibleProfession = cibleProfession; }

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