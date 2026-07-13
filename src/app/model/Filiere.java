package app.model;

public class Filiere {
    private int id;
    private int departement_id;
    private String nom;

    public Filiere(){}

    public Filiere(int id, int departement_id, String nom) {
        this.id = id;
        this.departement_id = departement_id;
        this.nom = nom;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getDepartement_id() {return departement_id;}
    public void setDepartement_id(int departement_id) {this.departement_id = departement_id;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}  
    
}

