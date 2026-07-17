package app.model;


public class User {
    private int id;
    private int code_permanent;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String profession;
    private int filiere_id;
    private String niveau;

    public User(){}

    public User(int id, int code_permanent, String nom, String prenom, String email, String password,
        String profession, int filiere_id, String niveau) {
        this.id = id;
        this.code_permanent = code_permanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.profession = profession;
        this.filiere_id = filiere_id;
        this.niveau = niveau;
    }

    // Méthode utilitaire pour savoir s'il s'est déjà connecté au moins une fois
    public boolean aUnMotDePasseDefini() {
        return this.password != null && !this.password.trim().isEmpty();
    }

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

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public int getFiliere_id() {return filiere_id;}
    public void setFiliere_id(int filiere_id) {this.filiere_id = filiere_id;}

    public String getNiveau() {return niveau;}
    public void setNiveau(String niveau) {this.niveau = niveau;}
}