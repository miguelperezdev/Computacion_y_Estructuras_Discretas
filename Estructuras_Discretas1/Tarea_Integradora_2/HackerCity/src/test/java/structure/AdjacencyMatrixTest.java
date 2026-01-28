package structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AdjacencyMatrixTest {

    private AdjacencyMatrix matrix;
    private Node a, b, c;

    @BeforeEach
    public void setUp() {
        matrix = new AdjacencyMatrix();
        a = new Node("A", 0, 0);
        b = new Node("B", 1, 1);
        c = new Node("C", 2, 2);
        matrix.addNode(a);
        matrix.addNode(b);
        matrix.addNode(c);
    }

    @Test
    public void testAddNodeStandard() {
        assertEquals(3, matrix.getNodes().size());
        assertTrue(matrix.getNodes().contains(a));
    }

    @Test
    public void testAddNodeDuplicate() {
        matrix.addNode(a);
        assertEquals(3, matrix.getNodes().size());
    }

    @Test
    public void testAddNodeLimit() {
        for (int i = 0; i < 80; i++) {
            matrix.addNode(new Node("N" + i, i, i));
        }
        assertTrue(matrix.getNodes().size() <= 80);
    }

    @Test
    public void testAddEdgeStandard() {
        matrix.addEdge(new Edge(a, b, 5, true));
        assertEquals(5, matrix.getEdgeWeight(a, b));
    }

    @Test
    public void testAddEdgeUndirected() {
        matrix.addEdge(new Edge(a, b, 7, false));
        assertEquals(7, matrix.getEdgeWeight(b, a));
    }

    @Test
    public void testAddEdgeWithUnregisteredNode() {
        Node d = new Node("D", 5, 5);
        Edge edge = new Edge(a, d, 3, false);
        assertThrows(IllegalArgumentException.class, () -> matrix.addEdge(edge));
    }

    @Test
    public void testGetEdgeWeightStandard() {
        matrix.addEdge(new Edge(a, c, 4, true));
        assertEquals(4, matrix.getEdgeWeight(a, c));
    }
    @Test
    void testGetEdgeWeightNonExistent() {
        AdjacencyMatrix matrix = new AdjacencyMatrix();
        Node n1 = new Node("A", 0, 0);
        Node n2 = new Node("B", 1, 1);

        matrix.addNode(n1);
        matrix.addNode(n2);

        int expected = Integer.MAX_VALUE;
        int actual = (int) matrix.getEdgeWeight(n1, n2);

        assertEquals(expected, actual);
    }


    @Test
    public void testGetEdgeWeightSameNode() {
        assertEquals(0, matrix.getEdgeWeight(a, a));
    }

    @Test
    public void testGetNeighborsStandard() {
        matrix.addEdge(new Edge(a, b, 3, false));
        List<Node> neighbors = matrix.getNeighbors(a);
        assertTrue(neighbors.contains(b));
    }

    @Test
    public void testGetNeighborsEmpty() {
        Node d = new Node("D", 10, 10);
        matrix.addNode(d);
        List<Node> neighbors = matrix.getNeighbors(d);
        assertTrue(neighbors.isEmpty());
    }

    @Test
    public void testGetNeighborsUndirected() {
        matrix.addEdge(new Edge(a, b, 1, false));
        assertTrue(matrix.getNeighbors(b).contains(a));
    }

    @Test
    public void testClearResetsMatrix() {
        matrix.addEdge(new Edge(a, b, 5, false));
        matrix.clear();
        assertEquals(0, matrix.getNodes().size());
        assertEquals(Double.POSITIVE_INFINITY, matrix.getEdgeWeight(a, b));
    }

    @Test
    public void testClearTwice() {
        matrix.clear();
        matrix.clear();
        assertEquals(0, matrix.getNodes().size());
    }

    @Test
    public void testClearAndReuse() {
        matrix.clear();
        Node d = new Node("D", 9, 9);
        matrix.addNode(d);
        assertEquals(1, matrix.getNodes().size());
    }
}
