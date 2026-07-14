package app.model;


public class User {
    private int id;
    private int code_permanent;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private Integer filiere_id;
    private String niveau;
    private Integer ufr_id;
 
    public User(int id, int code_permanent, String nom, String prenom, String email, String role, Integer filiere_id, String niveau, Integer ufr_id) {
        this.id = id;
        this.code_permanent = code_permanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.filiere_id = filiere_id;
        this.niveau = niveau;
        this.ufr_id = ufr_id;
    }

    public User(){}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCode_permanent() {return code_permanent;}
    public void setCode_permanent(int code_permanent) {this.code_permanent = code_permanent;}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getFiliere_id() {return filiere_id;}
    public void setFiliere_id(int filiere_id) {this.filiere_id = filiere_id;}

    public String getNiveau() {return niveau;}
    public void setNiveau(String niveau) {this.niveau = niveau;}

    public Integer getUfr_id() {return ufr_id;}
    public void setUfr_id(int ufr_id) {this.ufr_id = ufr_id;}

}