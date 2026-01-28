package structures;

import interfaces.IPriorityQueue;
import model.Team;
/**
 * Implementation of a priority queue using a max-heap data structure.
 * Teams are prioritized based on their total points (descending order)
 * and, in case of ties, by their coefficient (descending order).
 *
 * This class delegates all operations to an underlying Heap instance.
 */
public class PriorityQueue implements IPriorityQueue {
    private Heap heap;

    public PriorityQueue() {
        heap = new Heap();
    }
    /**
     * Inserts a team into the priority queue while maintaining the priority order.
     *
     * @param team the team to be inserted
     */
    @Override
    public void enqueue(Team team) {
        heap.insert(team);
    }
    /**
     * Removes and returns the team with the highest priority.
     *
     * @return the team with highest priority, or null if the queue is empty
     */
    @Override
    public Team dequeue() {
        return heap.extractMax();
    }
    /**
     * Returns (without removing) the team with the highest priority.
     *
     * @return the team with highest priority, or null if the queue is empty
     */
    @Override
    public Team peek() {
        return heap.peek();
    }
    /**
     * Checks whether the priority queue is empty.
     *
     * @return true if the queue contains no teams, false otherwise
     */

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    @Override
    public int getSize() {
        return heap.getSize();
    }
    /**
     * Removes a specific team from the priority queue if present.
     *
     * @param team the team to be removed
     */
    public void remove(Team team){
        heap.remove(team);
    }
}

