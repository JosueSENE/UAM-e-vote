package app.model;

public class User  implements Connectable{
    private int id;
    private int codePermanent;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String profession;
    private int filiereId;
    private String niveau;

    public User(){}

    public User(int id, int codePermanent, String nom, String prenom, String email, String password,
        String profession, int filiereId, String niveau) {
        this.id = id;
        this.codePermanent = codePermanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.profession = profession;
        this.filiereId = filiereId;
        this.niveau = niveau;
    }

    public boolean hasPassword() {return this.password != null && !this.password.trim().isEmpty();}
    public boolean isEtudiant() {return "ETUDIANT".equals(profession);}
    public boolean isEnseignant() {return "ENSEIGNANT".equals(profession);}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCodePermanent() {return codePermanent;}
    public void setCodePermanent(int codePermanent) {this.codePermanent = codePermanent;}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password;}
    public void setPassword(String password) {this.password = password;}

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public int getFiliereId() { return filiereId; }
    public void setFiliereId(int filiereId) {this.filiereId= filiereId; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) {this.niveau = niveau;}
}