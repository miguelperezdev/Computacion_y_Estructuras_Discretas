package structures;

/**
 * Class representing a node in the linked list.
 *
 * @param <T> the type of data stored in the node
 */
public class Node<T> {
    T data;
    Node<T> next;


    public Node(T data) {
        this.next = null;
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }
    public void setNext(Node<T> next) {
        this.next = next;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}