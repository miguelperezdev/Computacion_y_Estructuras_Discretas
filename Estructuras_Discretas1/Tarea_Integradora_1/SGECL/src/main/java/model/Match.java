package model;



public class Match {

    private Team homeTeam;
    private Team awayTeam;
    private int awayGoals;
    private int homeGoals;
    private String date;
    private String id;

    public Match(Team homeTeam, Team awayTeam, int homeGoals, int awayGoals, String date, String id) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.awayGoals = awayGoals;
        this.homeGoals = homeGoals;
        this.date = date;
        this.id = id;
    }
    public void draw (){}

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        return "Team away : " + awayTeam + " home team: "+ homeTeam + " with away goals: " + awayGoals + " and home goals: " + homeGoals + " with date: " + date + " with ID: " + id;
    }
}