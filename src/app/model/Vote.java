package app.model;


import java.time.LocalDateTime;

public class Vote {
    private int id;
    private int election_id;
    private int candidat_id;
    private int utilisateur_id;
    private LocalDateTime date_vote;

    public Vote(){}

    public Vote(int id, int election_id, int candidat_id, int utilisateur_id, LocalDateTime date_vote) {
        this.id = id;
        this.election_id = election_id;
        this.candidat_id = candidat_id;
        this.utilisateur_id = utilisateur_id;
        this.date_vote = date_vote;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getElection_id() {return election_id;}
    public void setElection_id(int election_id) {this.election_id = election_id;}

    public int getCandidat_id() {return candidat_id;}
    public void setCandidat_id(int candidat_id) {this.candidat_id = candidat_id;}

    public int getUtilisateur_id() {return utilisateur_id;}
    public void setUtilisateur_id(int utilisateur_id) {this.utilisateur_id = utilisateur_id;}

    public LocalDateTime getDate_vote() {return date_vote;}
    public void setDate_vote(LocalDateTime date_vote) {this.date_vote = date_vote;}
    
}