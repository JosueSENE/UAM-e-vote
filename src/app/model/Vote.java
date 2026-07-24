package app.model;


import java.time.LocalDateTime;

public class Vote {
    private int id;
    private int electionId;
    private int candidatId;
    private int utilisateurId;
    private LocalDateTime date;

    public Vote(){}
    

    public Vote(int id, int electionId, int candidatId, int utilisateurId, LocalDateTime date) {
        this.id = id;
        this.electionId = electionId;
        this.candidatId = candidatId;
        this.utilisateurId = utilisateurId;
        this.date = date;
    }


    public int getId() {return id;}
    public void setId(int id) {this.id = id;}


    public int getElectionId() {return electionId;}
    public void setElectionId(int electionId) {this.electionId = electionId;}

    public int getCandidatId() {return candidatId;}
    public void setCandidatId(int candidatId) {this.candidatId = candidatId;}

    public int getUtilisateurId() {return utilisateurId;}
    public void setUtilisateurId(int utilisateurId) {this.utilisateurId = utilisateurId;}

    public LocalDateTime getDate() {return date;}
    public void setDate(LocalDateTime date) {this.date = date;}

}
