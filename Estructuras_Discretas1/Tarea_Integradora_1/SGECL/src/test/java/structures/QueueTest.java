package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueueTest {

    @Test
    public void testEmptyQueue() {
        Queue<Integer> queue = new Queue<>();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());
        assertNull(queue.front());
        assertNull(queue.dequeue());
    }

    @Test
    public void testEnqueueAndFront() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("first");

        assertFalse(queue.isEmpty());
        assertEquals(1, queue.getSize());
        assertEquals("first", queue.front());

        queue.enqueue("second");
        assertEquals("first", queue.front());
        assertEquals(2, queue.getSize());
    }

    @Test
    public void testDequeue() {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        assertEquals(10, queue.dequeue());
        assertEquals(2, queue.getSize());

        assertEquals(20, queue.dequeue());
        assertEquals(1, queue.getSize());

        assertEquals(30, queue.dequeue());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());
    }

    @Test
    public void testDequeueEmptyQueue() {
        Queue<String> queue = new Queue<>();
        assertNull(queue.dequeue());
    }

    @Test
    public void testFrontEmptyQueue() {
        Queue<Double> queue = new Queue<>();
        assertNull(queue.front());
    }

    @Test
    public void testRemoveFromEmptyQueue() {
        Queue<Character> queue = new Queue<>();
        queue.remove('a');
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testRemoveFirstElement() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("remove");
        queue.enqueue("keep");

        queue.remove("remove");
        assertEquals(1, queue.getSize());
        assertEquals("keep", queue.front());
    }

    @Test
    public void testRemoveMiddleElement() {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        queue.remove(2);
        assertEquals(2, queue.getSize());
        assertEquals(1, queue.dequeue());
        assertEquals(3, queue.dequeue());
    }

    @Test
    public void testRemoveLastElement() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("first");
        queue.enqueue("last");

        queue.remove("last");
        assertEquals(1, queue.getSize());
        assertEquals("first", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testRemoveNonExistentElement() {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(100);
        queue.enqueue(200);

        queue.remove(300);
        assertEquals(2, queue.getSize());
    }

    @Test
    public void testHeadTailAfterOperations() {
        Queue<String> queue = new Queue<>();
        queue.enqueue("head");
        assertEquals("head", queue.getHead().getData());
        assertEquals("head", queue.getTail().getData());

        queue.enqueue("tail");
        assertEquals("head", queue.getHead().getData());
        assertEquals("tail", queue.getTail().getData());

        queue.dequeue();
        assertEquals("tail", queue.getHead().getData());
        assertEquals("tail", queue.getTail().getData());

        queue.dequeue();
        assertNull(queue.getHead());
        assertNull(queue.getTail());
    }

    @Test
    public void testSetHeadTail() {
        Queue<Integer> queue = new Queue<>();
        Node<Integer> newNode = new Node<>(42);

        queue.setHead(newNode);
        queue.setTail(newNode);
        queue.setSize(1);

        assertEquals(42, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testMultipleOperations() {
        Queue<Character> queue = new Queue<>();
        queue.enqueue('a');
        queue.enqueue('b');
        queue.enqueue('c');

        assertEquals('a', queue.dequeue());
        queue.enqueue('d');
        queue.remove('c');

        assertEquals(2, queue.getSize());
        assertEquals('b', queue.dequeue());
        assertEquals('d', queue.dequeue());
        assertTrue(queue.isEmpty());
    }
}
