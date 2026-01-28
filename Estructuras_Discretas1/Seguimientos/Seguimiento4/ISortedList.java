package controller;

// Definición de la interfaz controller. ISortedList<T>
public interface ISortedList<T extends Comparable<T>> {
    void insert(T element);
    void delete(T element);
    boolean search(T element);
    boolean isEmpty();
    int size();
}

// Implementación de la clase controller. SortedList<T>
class SortedList<T extends Comparable<T>> implements ISortedList<T> {
    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public SortedList() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void insert(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null || head.data.compareTo(element) > 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null && current.next.data.compareTo(element) < 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    @Override
    public void delete(T element) {
        if (head == null) return;
        if (head.data.equals(element)) {
            head = head.next;
            size--;
            return;
        }
        Node<T> current = head;
        while (current.next != null && !current.next.data.equals(element)) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next;
            size--;
        }
    }

    @Override
    public boolean search(T element) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data).append(" ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}