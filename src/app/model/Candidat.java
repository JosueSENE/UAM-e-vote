package app.model;

public class Candidat {
    private int id;
    private int electionId;
    private int userId;
    private String programme;
    private String photo; 
    private  User user;

    public Candidat(){}
    
    public Candidat(int id, int electionId, int userId, String programme, String photo, User user) {
        this.id = id;
        this.electionId = electionId;
        this.userId = userId;
        this.programme = programme;
        this.photo = photo;
        this.user = user;
    }
    
    // GETTER ET SETTER

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

}