package structures;

import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeapTest {

    private Heap heap;
    private Team team1;
    private Team team2;
    private Team team3;

    @BeforeEach
    void setUp() {
        heap = new Heap();
        team1 = new Team("Real Madrid", "Spain", 35, 90);
        team2 = new Team("Barcelona", "Spain", 30, 85);
        team3 = new Team("Bayern Munich", "Germany", 32, 95);
    }



    @Test
    void testInsertPositive() {
        heap.insert(team1);
        assertEquals(1, heap.getSize());
    }

    @Test
    void testInsertNegative() {
        assertEquals(0, heap.getSize());
    }

    @Test
    void testInsertInteresting() {
        heap.insert(team1);
        heap.insert(team2);
        assertEquals(team1, heap.peek());
    }


    @Test
    void testExtractMaxPositive() {
        heap.insert(team1);
        heap.insert(team2);
        assertEquals(team1, heap.extractMax());
    }

    @Test
    void testExtractMaxNegative() {
        assertNull(heap.extractMax());
    }

    @Test
    void testExtractMaxInteresting() {
        heap.insert(team2);
        heap.insert(team3);
        assertEquals(team3, heap.extractMax());
    }



    @Test
    void testPeekPositive() {
        heap.insert(team1);
        assertEquals(team1, heap.peek());
    }

    @Test
    void testPeekNegative() {
        assertNull(heap.peek());
    }

    @Test
    void testPeekInteresting() {
        heap.insert(team2);
        heap.insert(team3);
        assertEquals(team3, heap.peek());
        assertEquals(2, heap.getSize());
    }



    @Test
    void testIsEmptyPositive() {
        assertTrue(heap.isEmpty());
    }

    @Test
    void testIsEmptyNegative() {
        heap.insert(team1);
        assertFalse(heap.isEmpty());
    }

    @Test
    void testIsEmptyInteresting() {
        heap.insert(team1);
        heap.extractMax();
        assertTrue(heap.isEmpty());
    }



    @Test
    void testRemovePositive() {
        heap.insert(team1);
        heap.remove(team1);
        assertTrue(heap.isEmpty());
    }

    @Test
    void testRemoveNegative() {
        heap.remove(team1);
        assertEquals(0, heap.getSize());
    }

    @Test
    void testRemoveInteresting() {
        heap.insert(team1);
        heap.insert(team2);
        heap.insert(team3);
        heap.remove(team3);
        assertEquals(team1, heap.peek());
    }
}
