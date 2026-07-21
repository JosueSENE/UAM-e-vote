package app.model;

public class Admin implements Connectable {
    private int id;
    private int codePermanent;
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String password;
    private String role; // Toujours "ADMIN"
    private Integer ufrId;
    private String ufrNom;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public Admin() {
        this.role = "ADMIN";
    }

    public Admin(int id, String nom, String prenom, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = "ADMIN";
    }

    public Admin(int id, String nom, String prenom, String email, String login) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.role = "ADMIN";
    }

    public Admin(int id, String nom, String prenom, String email, String login, Integer ufrId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.ufrId = ufrId;
        this.role = "ADMIN";
    }

    // ==========================================
    // GETTERS ET SETTERS
    // ==========================================

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public int getCodePermanent() { 
        return codePermanent; 
    }
    
    public void setCodePermanent(int codePermanent) { 
        this.codePermanent = codePermanent; 
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

    public String getLogin() { 
        return login; 
    }
    
    public void setLogin(String login) { 
        this.login = login; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }

    public Integer getUfrId() { 
        return ufrId; 
    }
    
    public void setUfrId(Integer ufrId) { 
        this.ufrId = ufrId; 
    }

    public String getUfrNom() { 
        return ufrNom; 
    }
    
    public void setUfrNom(String ufrNom) { 
        this.ufrNom = ufrNom; 
    }

    // ==========================================
    // MÉTHODES DE CONVENANCE (UTILITAIRES)
    // ==========================================

    /**
     * Vérifie si l'administrateur a déjà un mot de passe
     */
    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * Retourne le nom complet (prénom + nom)
     */
    public String getFullName() {
        return prenom + " " + nom;
    }

    /**
     * Retourne le nom complet formaté (nom + prénom)
     */
    public String getFullNameFormatted() {
        return nom + " " + prenom;
    }

    /**
     * Vérifie si l'administrateur est rattaché à un UFR
     */
    public boolean hasUfr() {
        return ufrId != null && ufrId > 0;
    }

    // ==========================================
    // IMPLÉMENTATION DE Connectable
    // ==========================================

    @Override
    public String getCode_permanent() {
        return String.valueOf(codePermanent);
    }

    @Override
    public String getMotDePasse() {
        return password;
    }

    @Override
    public String getEmailAddress() {
        return email;
    }

    // ==========================================
    // MÉTHODES OVERRIDE
    // ==========================================

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", codePermanent=" + codePermanent +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", ufrNom='" + ufrNom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}