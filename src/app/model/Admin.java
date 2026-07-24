package app.model;

public class Admin implements Connectable {
    private int id;
    private int codePermanent;
    private String nom;
    private String prenom;
    private String email;
    private String password;

    public Admin(){}

    public Admin(int id, int codePermanent, String nom, String prenom, String email, String password) {
        this.id = id;
        this.codePermanent = codePermanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
    }

    public boolean hasPassword() {return password != null && !password.trim().isEmpty();}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getCodePermanent() {return codePermanent;}
    public void setCodePermanent(int code_permanent) {this.codePermanent = code_permanent;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}

    public String getPrenom() {return prenom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;} 

    public String getFullName() {return prenom + " " + nom;}

    public String getFullNameFormatted() {return nom + " " + prenom;}

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", codePermanent=" + codePermanent +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '}';
    }
    
}
