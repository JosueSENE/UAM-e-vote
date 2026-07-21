package app.model;

public class Departement {
    private int id;
    private int ufr_id;
    private String nom;
    private  Ufr ufr;;

    public Departement(){}

    public Departement(int id, int ufr_id, String nom, Ufr ufr) {
        this.id = id;
        this.ufr_id = ufr_id;
        this.nom = nom;
        this.ufr = ufr;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getUfr_id() {return ufr_id;}
    public void setUfr_id(int ufr_id) {this.ufr_id = ufr_id;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}

    public Ufr getUfr() {return ufr;}
    public void setUfr(Ufr ufr) {this.ufr = ufr;}

    public String getUfrNom() {
    return (ufr != null && ufr.getNom() != null) ? ufr.getNom() : "N/A";}

    @Override
    public String toString() {
        return nom;
    }
}
