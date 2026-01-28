package structures;

import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    private PriorityQueue priorityQueue;
    private Team realMadrid;
    private Team barcelona;
    private Team bayern;

    @BeforeEach
    void setUp() {
        priorityQueue = new PriorityQueue();
        realMadrid = new Team("Real Madrid", "Spain", 35, 90);
        barcelona = new Team("Barcelona", "Spain", 30, 85);
        bayern = new Team("Bayern Munich", "Germany", 32, 88);
    }

    @Test
    void testEnqueuePositive() {
        priorityQueue.enqueue(realMadrid);
        assertEquals(realMadrid, priorityQueue.peek());
    }

    @Test
    void testEnqueueNegative() {
        assertNull(priorityQueue.peek());
    }

    @Test
    void testEnqueueInteresting() {
        priorityQueue.enqueue(barcelona);
        priorityQueue.enqueue(realMadrid);
        assertEquals(realMadrid, priorityQueue.peek());
    }

    @Test
    void testDequeuePositive() {
        priorityQueue.enqueue(realMadrid);
        Team removed = priorityQueue.dequeue();
        assertEquals(realMadrid, removed);
        assertTrue(priorityQueue.isEmpty());
    }

    @Test
    void testDequeueNegative() {
        assertNull(priorityQueue.dequeue());
    }

    @Test
    void testDequeueInteresting() {
        priorityQueue.enqueue(barcelona);
        priorityQueue.enqueue(bayern);
        Team top = priorityQueue.dequeue();
        assertEquals(bayern, top);
    }

    @Test
    void testIsEmptyPositive() {
        assertTrue(priorityQueue.isEmpty());
    }

    @Test
    void testIsEmptyNegative() {
        priorityQueue.enqueue(realMadrid);
        assertFalse(priorityQueue.isEmpty());
    }

    @Test
    void testIsEmptyInteresting() {
        priorityQueue.enqueue(barcelona);
        priorityQueue.dequeue();
        assertTrue(priorityQueue.isEmpty());
    }

    @Test
    void testRemovePositive() {
        priorityQueue.enqueue(realMadrid);
        priorityQueue.enqueue(bayern);
        priorityQueue.remove(realMadrid);
        assertEquals(bayern, priorityQueue.peek());
    }

    @Test
    void testRemoveNegative() {
        priorityQueue.remove(realMadrid);
        assertTrue(priorityQueue.isEmpty());
    }

    @Test
    void testRemoveInteresting() {
        priorityQueue.enqueue(barcelona);
        priorityQueue.enqueue(realMadrid);
        priorityQueue.enqueue(bayern);
        priorityQueue.remove(bayern);
        assertEquals(realMadrid, priorityQueue.peek());
    }
}
