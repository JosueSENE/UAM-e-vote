package app.model;

/**
 * Modèle représentant un candidat à une élection locale de l'UAM.
 */
public class Candidat {
    private int id;
    private int electionId;
    private String userId;
    private String programme;
    private String photo; 

    public Candidat(){}
    
    public Candidat(int id, int electionId,String userId, String programme, String photo) {
        this.id = id;
        this.electionId = electionId;
        this.userId = userId;
        this.programme = programme;
        this.photo = photo;
    }
    // GETTER ET SETTER

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

}