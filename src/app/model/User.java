package app.model;

public class User {
    private int id;
    private Long code_permanent;      // ✅ Long pour supporter les grands nombres
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String profession;
    private Integer filiere_id;       // ✅ Integer pour supporter null
    private String niveau;

    // ==================== CONSTRUCTEURS ====================
    
    public User() {
        // Constructeur par défaut
    }

    public User(Long code_permanent, String nom, String prenom, String email, 
                String password, String profession, Integer filiere_id, String niveau) {
        this.code_permanent = code_permanent;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.profession = profession;
        this.filiere_id = filiere_id;
        this.niveau = niveau;
    }

    // ==================== GETTERS ET SETTERS ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCode_permanent() {
        return code_permanent;
    }

    public void setCode_permanent(Long code_permanent) {
        this.code_permanent = code_permanent;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Integer getFiliere_id() {
        return filiere_id;
    }

    public void setFiliere_id(Integer filiere_id) {
        this.filiere_id = filiere_id;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", code_permanent=" + code_permanent +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", profession='" + profession + '\'' +
                ", filiere_id=" + filiere_id +
                ", niveau='" + niveau + '\'' +
                '}';
    }
}