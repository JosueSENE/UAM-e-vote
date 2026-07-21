package app.model;

public class Enseignant_filieres {
    private int enseignant_id;
    private int filiere_id;

    public Enseignant_filieres(){}

    public Enseignant_filieres(int enseignant_id, int filiere_id) {
        this.enseignant_id = enseignant_id;
        this.filiere_id = filiere_id;
    }

    public int getEnseignant_id() {return enseignant_id;}
    public void setEnseignant_id(int enseignant_id) {this.enseignant_id = enseignant_id;}

    public int getFiliere_id() {return filiere_id;}
    public void setFiliere_id(int filiere_id) {this.filiere_id = filiere_id;}
    
}
