package map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.Graph;
import structure.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorGraphMapTest {

    private GeneratorGraphMap generator;

    @BeforeEach
    void setUp() {
        generator = new GeneratorGraphMap();
    }

    @Test
    void testGetNodes_NotNull() {
        List<Node> nodes = generator.getNodes();
        assertNotNull(nodes);
    }

    @Test
    void testGetNodes_Size() {
        List<Node> nodes = generator.getNodes();
        assertEquals(80, nodes.size());
    }

    @Test
    void testGetNodes_Content() {
        List<Node> nodes = generator.getNodes();
        assertTrue(nodes.stream().anyMatch(n -> n.getId().equals("Corner1")));
        assertTrue(nodes.stream().anyMatch(n -> n.getId().equals("Corner80")));
    }

    @Test
    void testGenerateGraph_NotNull() {
        Graph graph = generator.generateGraph();
        assertNotNull(graph);
    }

    @Test
    void testGenerateGraph_NodePresence() {
        Graph graph = generator.generateGraph();
        assertEquals(80, graph.getNodes().size());
    }

    @Test
    void testGenerateGraph_EdgesExist() {
        Graph graph = generator.generateGraph();
        Node corner1 = generator.getNodes().stream().filter(n -> n.getId().equals("Corner1")).findFirst().orElse(null);
        Node corner2 = generator.getNodes().stream().filter(n -> n.getId().equals("Corner2")).findFirst().orElse(null);
        assertTrue(graph.getNeighbors(corner1).contains(corner2));
        assertTrue(graph.getNeighbors(corner2).contains(corner1));
    }
}
