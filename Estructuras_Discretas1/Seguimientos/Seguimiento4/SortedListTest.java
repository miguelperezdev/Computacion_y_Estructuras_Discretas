package controller;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SortedListTest {
    private ISortedList<Integer> sortedList;

    @Before
    public void setUp() {
        sortedList = new SortedList<>();
    }

    @Test
    public void testInsertEmptyList() {
        sortedList.insert(5);
        assertEquals("[ 5 ]", sortedList.toString());
    }

    @Test
    public void testInsertAtBeginning() {
        sortedList.insert(5);
        sortedList.insert(3);
        assertEquals("[ 3 5 ]", sortedList.toString());
    }

    @Test
    public void testInsertAtEnd() {
        sortedList.insert(5);
        sortedList.insert(8);
        assertEquals("[ 5 8 ]", sortedList.toString());
    }

    // Pruebas para el método delete()

    @Test
    public void testDeleteExistingElement() {
        sortedList.insert(5);
        sortedList.insert(3);
        sortedList.insert(8);
        sortedList.delete(3);
        assertEquals("[ 5 8 ]", sortedList.toString());
    }

    @Test
    public void testDeleteNonExistingElement() {
        sortedList.insert(5);
        sortedList.insert(3);
        sortedList.insert(8);
        sortedList.delete(6);
        assertEquals("[ 3 5 8 ]", sortedList.toString());
    }

    @Test
    public void testDeleteFromEmptyList() {
        sortedList.delete(5);
        assertEquals("[ ]", sortedList.toString());
    }

    // Pruebas para el método search()

    @Test
    public void testSearchExistingElement() {
        sortedList.insert(5);
        sortedList.insert(3);
        sortedList.insert(8);
        assertTrue(sortedList.search(3));
    }

    @Test
    public void testSearchNonExistingElement() {
        sortedList.insert(5);
        sortedList.insert(3);
        sortedList.insert(8);
        assertFalse(sortedList.search(6));
    }

    @Test
    public void testSearchInEmptyList() {
        assertFalse(sortedList.search(5));
    }

    // Pruebas para el método isEmpty()

    @Test
    public void testIsEmptyWithElements() {
        sortedList.insert(5);
        assertFalse(sortedList.isEmpty());
    }

    @Test
    public void testIsEmptyWithoutElements() {
        assertTrue(sortedList.isEmpty());
    }

    @Test
    public void testIsEmptyAfterDeletion() {
        sortedList.insert(5);
        sortedList.delete(5);
        assertTrue(sortedList.isEmpty());
    }

    // Pruebas para el método size()

    @Test
    public void testSizeWithElements() {
        sortedList.insert(5);
        sortedList.insert(3);
        assertEquals(2, sortedList.size());
    }

    @Test
    public void testSizeWithoutElements() {
        assertEquals(0, sortedList.size());
    }

    @Test
    public void testSizeAfterDeletion() {
        sortedList.insert(5);
        sortedList.delete(5);
        assertEquals(0, sortedList.size());
    }
}
