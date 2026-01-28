package model;
/**
 * Represents a sports team with its attributes and competitive statistics.
 * Implements Comparable interface for natural ordering based on team performance.
 */
public class Team implements Comparable<Team> {
    private String name;
    private String country;
    private int titles;
    private int coefficient;
    private int totalPoints;

    public Team(String name, String country, int titles,  int coefficient) {
        this.name = name;
        this.country = country;
        this.coefficient = coefficient;
        this.titles = titles;
        this.totalPoints = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getTitles() {
        return titles;
    }

    public void setTitles(int titles) {
        this.titles = titles;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public void addPoint(int points) {
        this.totalPoints += points;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
    /**
     * Determines if this team has priority over another team.
     * Priority is determined by:
     * 1. Total points (higher first)
     * 2. Coefficient (if points are equal)
     *
     * @param other the team to compare against
     * @return true if this team has priority, false otherwise
     */
    public boolean priorityTeam(Team other){
        if(totalPoints > other.getTotalPoints()){
            return true;
        }
        else if(totalPoints == other.getTotalPoints()){
            return coefficient > other.getCoefficient();
        }else{
            return false;
        }
    }
    @Override
    public String toString(){
        return "Team: " + name + " country: " + country + " coefficient: " + coefficient + " total points: " + totalPoints;
    }
    /**
     * Compares this team to another for ordering.
     * Natural ordering is by:
     * 1. Total points (descending)
     * 2. Coefficient (descending, if points are equal)
     *
     * @param other the team to compare to
     * @return positive if this team has higher priority,
     *         negative if lower priority,
     *         0 if equal priority
     */
    public int compareTo(Team other){
        if(this.totalPoints > other.getTotalPoints()){
            return this.totalPoints - other.getTotalPoints();
        }else{
            return this.coefficient - other.getCoefficient();
        }
    }

}
