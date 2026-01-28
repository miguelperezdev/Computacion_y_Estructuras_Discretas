package model;

import structures.PriorityQueue;
/**
 * Manages team rankings based on their points using a priority queue.
 * Teams are automatically sorted by their total points (descending order)
 * and coefficient (in case of ties).
 */

public class Ranking {

    private PriorityQueue priorityQueue;

    public Ranking() {
        this.priorityQueue = new PriorityQueue();
    }
    /**
     * Adds a team to the ranking system.
     *
     * @param team the team to be added to the rankings
     */
    public void addTeam(Team team){
        priorityQueue.enqueue(team);
    }
    /**
     * Generates a formatted string representation of the current rankings.
     *
     * @return formatted ranking table or message if no teams are registered
     */
    public String takeRankingAsString() {
        String result = "";

        if (priorityQueue.isEmpty()) {
            return "No teams registered.";
        }

        PriorityQueue priorityQueue2 = new PriorityQueue();


        PriorityQueue temp = new PriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Team team = priorityQueue.dequeue();
            temp.enqueue(team);
            priorityQueue2.enqueue(team);
        }

        while (!temp.isEmpty()) {
            priorityQueue.enqueue(temp.dequeue());
        }


        int pos = 1;
        while (!priorityQueue2.isEmpty()) {
            Team team = priorityQueue2.dequeue();
            result += String.format("| %-2d | %-15s | %-6d |\n", pos++, team.getName(), team.getTotalPoints());
        }

        return result;
    }

    /**
     * Gets the top-ranked team without removing it.
     *
     * @return the team with highest points, or null if ranking is empty
     */
    public Team getTopTeam(){
        return priorityQueue.peek();
    }
    /**
     * Checks if the ranking is empty.
     *
     * @return true if no teams are registered, false otherwise
     */
    public boolean isEmpty(){
        return priorityQueue.isEmpty();
    }
    /**
     * Removes a team from the rankings.
     *
     * @param team the team to be removed
     */
    public void removeTeam(Team team){
        priorityQueue.remove(team);
    }
}