package app.model;

public class User implements Connectable {
    private int id;
    private int codePermanent;
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String password;
    private String role; // ETUDIANT, ENSEIGNANT, ADMIN
    private Integer filiereId;
    private String niveau; // L1, L2, L3, M1, M2
    private Integer ufrId;
    
    // Champs additionnels pour les jointures
    private String filiereNom;
    private String departementNom;
    private String ufrNom;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public User() {}

    public User(int id, String nom, String prenom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    public User(int id, String nom, String prenom, String email, String login, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.role = role;
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

    public Integer getFiliereId() { 
        return filiereId; 
    }
    
    public void setFiliereId(Integer filiereId) { 
        this.filiereId = filiereId; 
    }

    public String getNiveau() { 
        return niveau; 
    }
    
    public void setNiveau(String niveau) { 
        this.niveau = niveau; 
    }

    public Integer getUfrId() { 
        return ufrId; 
    }
    
    public void setUfrId(Integer ufrId) { 
        this.ufrId = ufrId; 
    }

    public String getFiliereNom() { 
        return filiereNom; 
    }
    
    public void setFiliereNom(String filiereNom) { 
        this.filiereNom = filiereNom; 
    }

    public String getDepartementNom() { 
        return departementNom; 
    }
    
    public void setDepartementNom(String departementNom) { 
        this.departementNom = departementNom; 
    }

    public String getUfrNom() { 
        return ufrNom; 
    }
    
    public void setUfrNom(String ufrNom) { 
        this.ufrNom = ufrNom; 
    }
    private String filieresList; // Liste des filieres separees par des virgules

    // Getters et Setters
    public String getFilieresList() { 
        return filieresList; 
    }

    public void setFilieresList(String filieresList) { 
        this.filieresList = filieresList; 
    }

    // ==========================================
    // MÉTHODES DE CONVENANCE (UTILITAIRES)
    // ==========================================

    /**
     * Vérifie si l'utilisateur est un étudiant
     */
    public boolean isEtudiant() {
        return "ETUDIANT".equals(role);
    }

    /**
     * Vérifie si l'utilisateur est un enseignant
     */
    public boolean isEnseignant() {
        return "ENSEIGNANT".equals(role);
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Vérifie si l'utilisateur a déjà un mot de passe
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
        return "User{" +
                "id=" + id +
                ", codePermanent=" + codePermanent +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", niveau='" + niveau + '\'' +
                ", filiereNom='" + filiereNom + '\'' +
                ", ufrNom='" + ufrNom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}