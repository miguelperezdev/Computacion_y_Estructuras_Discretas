package structures;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    private LinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new LinkedList<>();
    }


    @Test
    void testAddPositive() {
        list.add("Element1");
        assertEquals(0, list.getSize());
        assertEquals("Element1", list.getFirst().getData());
    }

    @Test
    void testAddNegative() {
        assertNull(list.getFirst());
        assertEquals(0, list.getSize());
    }

    @Test
    void testAddInteresting() {
        list.add("Element1");
        list.add("Element2");
        assertEquals(1, list.getSize());
        assertEquals("Element1", list.getFirst().getData());
    }



    @Test
    void testSearchPositive() {
        list.add("A");
        assertNotNull(list.search("A"));
    }

    @Test
    void testSearchNegative() {
        list.add("A");
        assertNull(list.search("B"));
    }

    @Test
    void testSearchInteresting() {
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("B", list.search("B").getData());
    }



    @Test
    void testRemovePositive() {
        list.add("A");
        assertTrue(list.remove("A"));
        assertEquals(-1, list.getSize());
    }

    @Test
    void testRemoveNegative() {
        assertFalse(list.remove("NonExisting"));
    }

    @Test
    void testRemoveInteresting() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.remove("B");
        assertNull(list.search("B"));
        assertEquals(1, list.getSize());
    }
}
