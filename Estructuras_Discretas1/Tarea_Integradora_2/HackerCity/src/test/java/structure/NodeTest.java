package structure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    public void testNodeCreationWithValidData() {
        Node node = new Node("A", 10, 20);
        assertEquals("A", node.getId());
        assertEquals(10, node.getX());
        assertEquals(20, node.getY());
        assertTrue(node.isWalkable());
        assertFalse(node.isHacked());
    }

    @Test
    public void testNodeCreationWithZeroCoordinates() {
        Node node = new Node("B", 0, 0);
        assertEquals("B", node.getId());
        assertEquals(0, node.getX());
        assertEquals(0, node.getY());
        assertTrue(node.isWalkable());
        assertFalse(node.isHacked());
    }

    @Test
    public void testNodeCreationWithNegativeCoordinates() {
        Node node = new Node("Neg", -5, -10);
        assertEquals("Neg", node.getId());
        assertEquals(-5, node.getX());
        assertEquals(-10, node.getY());
        assertTrue(node.isWalkable());
        assertFalse(node.isHacked());
    }

    @Test
    public void testSetHackedTrue() {
        Node node = new Node("C", 1, 1);
        assertFalse(node.isHacked());
        node.setHacked(true);
        assertTrue(node.isHacked());
    }

    @Test
    public void testSetHackedFalseAfterTrue() {
        Node node = new Node("C", 1, 1);
        node.setHacked(true);
        assertTrue(node.isHacked());
        node.setHacked(false);
        assertFalse(node.isHacked());
    }

    @Test
    public void testSetHackedStatePersistence() {
        Node node = new Node("D", 2, 2);
        node.setHacked(true);
        node.setHacked(true);
        assertTrue(node.isHacked());
    }

    @Test
    public void testEqualsWithSameIdDifferentCoordinates() {
        Node node1 = new Node("E", 5, 5);
        Node node2 = new Node("E", 10, 10);
        assertEquals(node1, node2);
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        Node node1 = new Node("F", 5, 5);
        Node node2 = new Node("G", 5, 5);
        assertNotEquals(node1, node2);
    }

    @Test
    public void testEqualsWithNullAndDifferentClass() {
        Node node = new Node("H", 1, 1);
        assertNotEquals(node, null);
        assertNotEquals(node, "H");
    }
}
