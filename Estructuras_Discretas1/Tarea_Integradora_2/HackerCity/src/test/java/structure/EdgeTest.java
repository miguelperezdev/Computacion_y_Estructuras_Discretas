package structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeTest {

    private Node a, b, c;

    @BeforeEach
    public void setUp() {
        a = new Node("A", 0, 0);
        b = new Node("B", 1, 1);
        c = new Node("C", 2, 2);
    }

    @Test
    public void testGetSourceBasic() {
        Edge edge = new Edge(a, b, 5, true);
        assertEquals(a, edge.getSource());
    }

    @Test
    public void testGetSourceDifferentNodes() {
        Edge edge = new Edge(c, b, 3, false);
        assertEquals(c, edge.getSource());
    }

    @Test
    public void testGetSourceSelfEdge() {
        Edge edge = new Edge(a, a, 1, true);
        assertEquals(a, edge.getSource());
    }


    @Test
    public void testGetDestinationBasic() {
        Edge edge = new Edge(a, b, 5, true);
        assertEquals(b, edge.getDestination());
    }

    @Test
    public void testGetDestinationDifferent() {
        Edge edge = new Edge(a, c, 8, false);
        assertEquals(c, edge.getDestination());
    }

    @Test
    public void testGetDestinationSelfEdge() {
        Edge edge = new Edge(c, c, 2, true);
        assertEquals(c, edge.getDestination());
    }



    @Test
    public void testGetWeightPositive() {
        Edge edge = new Edge(a, b, 10, true);
        assertEquals(10, edge.getWeight());
    }

    @Test
    public void testGetWeightZero() {
        Edge edge = new Edge(a, b, 0, false);
        assertEquals(0, edge.getWeight());
    }

    @Test
    public void testGetWeightNegative() {
        Edge edge = new Edge(a, b, -5, false);
        assertEquals(-5, edge.getWeight());
    }



    @Test
    public void testIsDirectedTrue() {
        Edge edge = new Edge(a, b, 5, true);
        assertTrue(edge.isDirected());
    }

    @Test
    public void testIsDirectedFalse() {
        Edge edge = new Edge(a, b, 5, false);
        assertFalse(edge.isDirected());
    }

    @Test
    public void testIsDirectedConsistency() {
        Edge edge1 = new Edge(a, b, 1, true);
        Edge edge2 = new Edge(b, a, 1, false);

        assertTrue(edge1.isDirected());
        assertFalse(edge2.isDirected());
    }
}
