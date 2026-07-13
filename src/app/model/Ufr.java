package app.model;

public class Ufr {
    private int id;
    private String nom;

    public Ufr(){}

    public Ufr(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}
    
}
