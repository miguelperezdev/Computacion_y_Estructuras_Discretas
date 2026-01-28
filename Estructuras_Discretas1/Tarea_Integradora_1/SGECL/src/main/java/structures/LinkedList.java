package structures;
/**
 * Implementation of a generic singly linked list.
 *
 * @param <T> the type of elements stored in the list
 */
public class LinkedList<T> {
    private Node<T> first;
    private int size;

    public LinkedList(){
        this.first = null;
        this.size = 0;

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Node<T> getFirst() {
        return first;
    }

    public void setFirst(Node<T> first) {
        this.first = first;
    }
    /**
     * Adds an element to the end of the list.
     *
     * @param data the element to be added
     */
    public void add(T data){
        Node<T> newNode = new Node<>(data);
        if(first == null){
            first = newNode;
        }else{
            Node<T> current = first;
            if(current.next != null ){
                current = current.next;
            }
            current.next = newNode;
            size++;
        }
    }
    /**
     * Searches for an element in the list.
     *
     * @param data the element to search for
     * @return the node containing the element, or null if not found
     */
    public Node<T> search(T data){
        Node<T> current = first;
        while(current != null){
            if(current.getData().equals(data)){
                return current;
            }
            current = current.next;
        }
        return null;

    }
    /**
     * Removes the first occurrence of the specified element from the list.
     *
     * @param data the element to be removed
     * @return true if the element was found and removed, false otherwise
     */
    public boolean remove(T data){
        if(first == null){
            return false;
        }
        if(first.getData().equals(data)){
            first = first.next;
            size--;
            return true;
        }

        Node<T> current = first;
        while(current.next != null){
            if(current.next.getData().equals(data)){
                current.setNext(current.next.next);
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
}

}
