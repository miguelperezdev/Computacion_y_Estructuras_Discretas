package structure
        ;

import org.junit.jupiter.api.Test;
import structure.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PathFinderTest {

    //  Tests para dijkstraPath

    @Test
    public void testDijkstraPathStandard() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(b, c, 1, false));
        graph.addEdge(new Edge(a, c, 10, false));

        List<Node> path = PathFinder.dijkstraPath(graph, a, c);
        assertEquals(List.of(a, b, c), path);
    }

    @Test
    public void testDijkstraPathUnreachable() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addEdge(new Edge(a, b, 1, false)); // c está aislado

        List<Node> path = PathFinder.dijkstraPath(graph, a, c);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testDijkstraPathInteresting() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addEdge(new Edge(a, c, 10, false));
        graph.addEdge(new Edge(a, b, 2, false));
        graph.addEdge(new Edge(b, c, 2, false));

        List<Node> path = PathFinder.dijkstraPath(graph, a, c);
        assertEquals(List.of(a, b, c), path);
    }


    // Tests para dijkstraPathMatrix

    @Test
    public void testDijkstraPathMatrixStandard() {
        AdjacencyMatrix matrix = new AdjacencyMatrix();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        matrix.addNode(a);
        matrix.addNode(b);
        matrix.addNode(c);
        matrix.addEdge(new Edge(a, b, 1, false));
        matrix.addEdge(new Edge(b, c, 1, false));
        matrix.addEdge(new Edge(a, c, 10, false));

        List<Node> path = PathFinder.dijkstraPathMatrix(matrix, a, c);
        assertEquals(List.of(a, b, c), path);
    }

    @Test
    public void testDijkstraPathMatrixUnreachable() {
        AdjacencyMatrix matrix = new AdjacencyMatrix();
        Node a = new Node("A", 0, 0);
        matrix.addNode(a); // solo A

        List<Node> path = PathFinder.dijkstraPathMatrix(matrix, a, a);
        assertEquals(List.of(a), path); // camino a sí mismo
    }

    @Test
    public void testDijkstraPathMatrixInteresting() {
        AdjacencyMatrix matrix = new AdjacencyMatrix();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        matrix.addNode(a);
        matrix.addNode(b);
        matrix.addNode(c);
        matrix.addEdge(new Edge(a, c, 10, false));
        matrix.addEdge(new Edge(a, b, 1, false));
        matrix.addEdge(new Edge(b, c, 1, false));

        List<Node> path = PathFinder.dijkstraPathMatrix(matrix, a, c);
        assertEquals(List.of(a, b, c), path);
    }


    // Tests para bfsPath


    @Test
    public void testBfsPathStandard() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        Node d = new Node("D", 3, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addNode(d);
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(b, d, 1, false));
        graph.addEdge(new Edge(a, c, 1, false));
        graph.addEdge(new Edge(c, d, 1, false));

        List<Node> path = PathFinder.bfsPath(graph, a, d);
        assertEquals(3, path.size());
        assertEquals("A", path.get(0).getId());
        assertEquals("D", path.get(2).getId());
    }

    @Test
    public void testBfsPathUnreachable() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addEdge(new Edge(a, b, 1, false));

        List<Node> path = PathFinder.bfsPath(graph, a, c);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testBfsPathInteresting() {
        Graph graph = new Graph();
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 0);
        Node c = new Node("C", 2, 0);
        Node d = new Node("D", 3, 0);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addNode(d);
        graph.addEdge(new Edge(a, b, 1, false));
        graph.addEdge(new Edge(b, d, 1, false));
        graph.addEdge(new Edge(a, c, 1, false));
        graph.addEdge(new Edge(c, d, 1, false));

        List<Node> path = PathFinder.bfsPath(graph, a, d);
        assertEquals(3, path.size());
    }
}
