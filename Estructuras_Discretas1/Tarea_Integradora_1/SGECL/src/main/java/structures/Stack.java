package structures;

import interfaces.IStack;
/**
 * Implementation of a generic LIFO (Last-In-First-Out) Stack using linked nodes.
 *
 * @param <T> the type of elements held in this stack
 */
public class Stack<T> implements IStack<T> {

    private Node<T> top;
    private int size;

    public Stack() {
        top = null;
        size = 0;
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Node<T> getTop() {
        return top;
    }

    public void setTop(Node<T> top) {
        this.top = top;
    }
    /**
     * Pushes an element onto the top of the stack.
     *
     * @param element the element to be pushed
     */
    public void push(T element) {
        Node<T> newNode = new Node<>(element);
        newNode.setNext(top);
        top = newNode;
        size++;
    }
    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the element at the top of the stack, or null if empty
     */

    public T pop() {
        if(isEmpty()){
            return null;
        }
        T data = top.getData();
        top = top.next;
        size--;
        return data;
    }
    /**
     * Retrieves, but does not remove, the element at the top of the stack.
     *
     * @return the element at the top of the stack, or null if empty
     */

    @Override
    public T peek() {
        if(isEmpty()){
            return null;
        }else{
            return getTop().data;
        }
    }
    /**
     * Retrieves, but does not remove, the element at the top of the stack.
     * (Alternative method to peek with identical functionality)
     *
     * @return the element at the top of the stack, or null if empty
     */

    public T top() {
        if(isEmpty()){
            return null;
        }
        return top.getData();
    }
    /**
     * Checks whether the stack is empty.
     *
     * @return true if the stack contains no elements, false otherwise
     */
    public boolean isEmpty() {
        return top == null;
    }
}
