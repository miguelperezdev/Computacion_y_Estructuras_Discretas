package model;

import exceptions.TeamException;
import exceptions.MatchException;
import structures.*;

/**
 * Main controller class that manages all system operations including:
 * - Team management
 * - Match scheduling and results
 * - Ranking calculations
 * - Undo functionality
 * - Match queue management
 */
public class Controller {
    private HashTable<String, Team> teams;
    private HashTable<String, Match> matches;
    private Stack<Action> actions;
    private Queue<Match> matchQueue;
    private Ranking ranking;
    private int matchCounter;


    public Controller() {
        this.teams = new HashTable<>();
        this.matches = new HashTable<>();
        this.actions = new Stack<>();
        this.matchQueue = new Queue<>();
        this.ranking = new Ranking();
        this.matchCounter = 0;
    }

    /**
     * Adds a new team to the system.
     *
     * @param name The team name (must be unique)
     * @param country The team's country of origin
     * @param titles Number of titles won by the team
     * @param coefficient The team's coefficient value
     * @return Success/error message
     */
    public String addTeam(String name, String country, int titles, int coefficient) {
        if (teams.obtain(name) != null) {
            return "The team " + name + " already exists.";

        }
        Team team = new Team(name, country, titles, coefficient);
        teams.insert(name, team);

        actions.push(new Action<>("addTeam", team));
        ranking.addTeam(team);
        return "Team " + name + " added successfully.";

    }

    /**
     * Records a new match between two teams.
     *
     * @param homeTeam Name of the home team
     * @param awayTeam Name of the away team
     * @param homeGoals Goals scored by home team (must be >= 0)
     * @param awayGoals Goals scored by away team (must be >= 0)
     * @param date Date of the match
     * @return Success/error message
     */
    public String addMatch(String homeTeam, String awayTeam, int homeGoals, int awayGoals, String date) {
        if(homeGoals < 0 || awayGoals < 0) {
            return "The goals can´t be negative";
        }

        Team home = teams.obtain(homeTeam);
        Team away = teams.obtain(awayTeam);



        if (home == null || away == null) {
            return "One or both teams are not registered.";
        }

        String matchId = "Match " + matchCounter++;


        Match match = new Match(home, away, homeGoals, awayGoals, date, matchId);
        matches.insert(matchId, match);

        if (homeGoals > awayGoals) {
            home.addPoint(3);
        } else if (awayGoals > homeGoals) {
            away.addPoint(3);
        } else {
            home.addPoint(1);
            away.addPoint(1);
        }
        actions.push(new Action<>("addMatch", match));

        return "Match " + matchId + " added successfully";
    }

    /**
     * Reverts the last performed action.
     *
     * @return Status message indicating what was undone
     */
    public String undo() {
        if (actions.isEmpty()) {
            return "No actions to undo";
        }
        Action<?> lastAction = actions.pop();
        String type = lastAction.getType();
        Object object = lastAction.getObject();
        if (type.equals("addTeam") && object instanceof Team) {
            Team team = (Team) object;
            ranking.removeTeam(team);
            teams.delete(team.getName());
            return "The team: " + team.getName() + " has been deleted successfully.";

        }
        if (type.equals("addMatch") && object instanceof Match) {
            Match match = (Match) object;
            matches.delete(match.getId());
            matchCounter--;

            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();
            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();
            if (homeGoals > awayGoals) {
                homeTeam.addPoint(-3);
            } else if (awayGoals > homeGoals) {
                awayTeam.addPoint(-3);
            }
            if (awayGoals == homeGoals) {
                awayTeam.addPoint(-1);
                homeTeam.addPoint(-1);
            }
            return "The match : " + match.getId() + " has been deleted and the points has been reverted";
        }
        if (type.equals("manageMatch") && object instanceof Match) {
            Match match = (Match) object;
            matchQueue.remove(match);
            return "Undo successful: Match " + match.getId() + " removed from schedule.";
        }
        return "The type doesn't exist.";
    }
    /**
     * Adds a match to the scheduling queue.
     *
     * @param matchId The ID of the match to schedule
     * @return Success/error message
     */
    public String enqueueMatch(String matchId) {
        Match match = matches.obtain(matchId);
        if (match == null) {
            return "No match no found with ID " + matchId;
        }
        matchQueue.enqueue(match);
        actions.push(new Action<>("manageMatch", match));
        return "Match " + matchId + " enqueued successfully.";
    }
    /**
     * Generates a string representation of the match schedule.
     *
     * @return Formatted schedule or message if empty
     */
    public String matchSchedule() {
        if (matchQueue.isEmpty()) {
            return "No matches in the schedule";
        }
        String schedule = "Upcoming matches: \n";
        Queue<Match> queue = new Queue<>();
        while (!matchQueue.isEmpty()) {
            Match match = matchQueue.dequeue();
            schedule += match.toString() + "\n";
            queue.enqueue(match);
        }
        while (!queue.isEmpty()) {
            matchQueue.enqueue(queue.dequeue());
        }
        return schedule;
    }
    /**
     * Gets the current team ranking.
     *
     * @return Formatted ranking string
     */
    public String teamRanking() {
        return ranking.takeRankingAsString();
    }

    /**
     * Searches for a team by name (public facing method).
     *
     * @param name The team name to search for
     * @return Team information if found
     * @throws TeamException If team is not found
     */
    public String publicSearchTeam(String name) throws TeamException {
        Team team = teams.obtain(name);

        if (team == null) {
            throw new TeamException("No team found with ID: " + name);
        }

        return "Team found:\n" + team.toString();

    }
    /**
     * Searches for a match by ID (public facing method).
     *
     * @param id The match ID to search for
     * @return Match information if found
     * @throws MatchException If match is not found
     */

    public String publicSearchMatch(String id) throws MatchException {
        Match match = matches.obtain(id);

        if (match == null) {
            throw new MatchException("No match found with ID: " + id);
        }

        return "Match between " + match.getHomeTeam().getName() + " and " + match.getAwayTeam().getName() + " with score: "
                + match.getHomeGoals() + " - " + match.getAwayGoals() + " on " + match.getDate();
    }

    /**
     * Internal method to search for a team.
     *
     * @param name The team name to search for
     * @return Team object if found, null otherwise
     */
    Team searchTeam(String name) {
        Team team = teams.obtain(name);

        if (team != null) {
            return team;
        } else {
            return null;
        }
    }
    /**
     * Internal method to search for a match.
     *
     * @param id The match ID to search for
     * @return Match object if found, null otherwise
     */
    Match searchMatch(String id) {
        Match match = matches.obtain(id);

        if (match != null) {
            return match;
        } else {
            return null;
        }


    }
}