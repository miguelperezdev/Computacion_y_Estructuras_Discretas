package structures;

import interfaces.IQueue;
/**
 * Implementation of a generic FIFO (First-In-First-Out) Queue using linked nodes.
 *
 * @param <T> the type of elements held in this queue
 */
public class Queue<T> implements IQueue<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public Queue() {
        head = null;
        tail = null;
        size = 0;
    }

    // getters - setters

    public Node<T> getHead() { return head; }

    public void setHead(Node<T> head) { this.head = head; }

    public Node<T> getTail() { return tail; }

    public void setTail(Node<T> tail) { this.tail = tail; }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }

    /**
     * Adds an element to the end of the queue.
     *
     * @param element the element to be added
     */

    public void enqueue(T element) {
       Node<T> newNode = new Node<>(element);
       if(isEmpty()){
           head= newNode;
           tail = newNode;
       }else{
           tail.setNext(newNode);
           tail = newNode;

       }
       size++;
    }
    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return the element at the front of the queue, or null if empty
     */
    public T dequeue() {
        if(isEmpty()){
            return null;
        }
        T data = head.getData();
        head = head.next;
        if(head == null){
            tail = null;
        }
        size--;
        return data;
    }
    /**
     * Retrieves, but does not remove, the element at the front of the queue.
     *
     * @return the element at the front of the queue, or null if empty
     */
    public T front() {
        if(isEmpty()){
            return null;
        }
        return head.getData();
    }
    /**
     * Checks whether the queue is empty.
     *
     * @return true if the queue contains no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    /**
     * Removes the first occurrence of the specified element from the queue.
     *
     * @param data the element to be removed
     */
    public void remove(T data){
        if(isEmpty()){
            return;
        }
        if(head.getData().equals(data)){
            dequeue();
            return;

        }
        Node<T> current = head;
        while(current.getNext() != null){
            if(current.getNext().getData().equals(data)){
                current.setNext(current.getNext().getNext());
                if(current.getNext() == null){
                    tail = current;
                }
                size--;
                return;
            }
            current = current.getNext();
        }
    }

}