package interfaces;

import model.Team;

public interface IPriorityQueue {
    void enqueue(Team team);
    Team dequeue();
    Team peek();
    boolean isEmpty();
    int getSize();
}
