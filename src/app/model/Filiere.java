package app.model;

public class Filiere {
    private int id;
    private int departement_id;
    private String nom;
    private Departement departement;

    public Filiere(){}

    public Filiere(int id, int departement_id, String nom, Departement departement) {
        this.id = id;
        this.departement_id = departement_id;
        this.nom = nom;
        this.departement = departement;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getDepartement_id() {return departement_id;}
    public void setDepartement_id(int departement_id) {this.departement_id = departement_id;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}  

    public Departement getDepartement() {return departement;}
    public void setDepartement(Departement departement) {this.departement = departement;}

    public String getDepartementNom() {
    return (departement != null && departement.getNom() != null) ? departement.getNom() : "N/A";}

    @Override
    public String toString() {
        return nom;
    }
    
}

