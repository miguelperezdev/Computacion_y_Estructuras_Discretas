package interfaces;

public interface IQueue <T>{
    void enqueue(T element);
    T dequeue();
    T front();
    boolean isEmpty();
    int getSize();
}
