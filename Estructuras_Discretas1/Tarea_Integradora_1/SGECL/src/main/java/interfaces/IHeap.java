package interfaces;

import model.Team;

public interface IHeap {
    void insert(Team element);
    Team extractMax();
    Team peek();
    boolean isEmpty();
    int getSize();
}
