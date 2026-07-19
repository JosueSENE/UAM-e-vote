package app.model;

public class Admin  implements Connectable {
    private int id;
    private int code_permanent;
    private String nom;
    private String prenom;
    private String email;
    private String password;

    public Admin(){}

    public Admin(int id, int code_permanent, String nom, String prenom, String email, String password) {
        this.id = id;
        this.code_permanent = code_permanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
    }

    public boolean aUnMotDePasse() {
        return this.password != null && !this.password.trim().isEmpty();
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getCode_permanent() {return code_permanent;}
    public void setCode_permanent(int code_permanent) {this.code_permanent = code_permanent;}

    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}

    public String getPrenom() {return prenom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;} 
    
}
