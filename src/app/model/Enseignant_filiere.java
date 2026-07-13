package app.model;

public class Enseignant_filiere {
    private int enseignant_id;
    private int ufr_id;

    public Enseignant_filiere(){}

    public Enseignant_filiere(int enseignant_id, int ufr_id) {
        this.enseignant_id = enseignant_id;
        this.ufr_id = ufr_id;
    }

    public int getEnseignant_id() {return enseignant_id;}
    public void setEnseignant_id(int enseignant_id) {this.enseignant_id = enseignant_id;}

    public int getUfr_id() {return ufr_id;}
    public void setUfr_id(int ufr_id) {this.ufr_id = ufr_id;}
    
}
