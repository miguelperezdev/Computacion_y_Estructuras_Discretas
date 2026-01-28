package structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph graph;
    private Node a, b, c, d;

    @BeforeEach
    public void setUp() {
        graph = new Graph();
        a = new Node("A", 0, 0);
        b = new Node("B", 1, 0);
        c = new Node("C", 2, 0);
        d = new Node("D", 3, 0);
    }

    @Test
    public void testAddSingleNode() {
        graph.addNode(a);
        assertTrue(graph.getNodes().contains(a));
    }

    @Test
    public void testAddDuplicateNode() {
        graph.addNode(a);
        graph.addNode(a);
        assertEquals(1, graph.getNodes().size());
    }

    @Test
    public void testAddMultipleNodes() {
        graph.addNode(a);
        graph.addNode(b);
        assertEquals(2, graph.getNodes().size());
    }

    @Test
    public void testAddEdgeBetweenExistingNodes() {
        graph.addNode(a);
        graph.addNode(b);
        graph.addEdge(new Edge(a, b, 5, false));
        assertEquals(1, graph.getEdgesFrom(a).size());
    }

    @Test
    public void testAddEdgeAutoAddNodes() {
        graph.addEdge(new Edge(a, b, 2, false));
        assertTrue(graph.getNodes().contains(a));
        assertTrue(graph.getNodes().contains(b));
    }

    @Test
    public void testAddMultipleEdges() {
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(a, c, 3, false));
        assertEquals(2, graph.getEdgesFrom(a).size());
    }
    @Test
    public void testGetSingleNeighbor() {
        graph.addEdge(new Edge(a, b, 4, false));
        List<Node> neighbors = graph.getNeighbors(a);
        assertEquals(1, neighbors.size());
        assertEquals("B", neighbors.get(0).getId());
    }

    @Test
    public void testGetNoNeighbors() {
        graph.addNode(a);
        List<Node> neighbors = graph.getNeighbors(a);
        assertTrue(neighbors.isEmpty());
    }

    @Test
    public void testGetMultipleNeighbors() {
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(a, c, 2, false));
        List<Node> neighbors = graph.getNeighbors(a);
        assertEquals(2, neighbors.size());
    }
    @Test
    public void testValidEdgeWeight() {
        graph.addEdge(new Edge(a, b, 7, false));
        assertEquals(7, graph.getEdgeWeight(a, b));
    }

    @Test
    public void testEdgeWeightToUnconnectedNode() {
        graph.addNode(a);
        graph.addNode(d);
        assertEquals(Double.POSITIVE_INFINITY, graph.getEdgeWeight(a, d));
    }

    @Test
    public void testEdgeWeightMultipleEdges() {
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(a, c, 2, false));
        assertEquals(1, graph.getEdgeWeight(a, b));
        assertEquals(2, graph.getEdgeWeight(a, c));
    }
}
