package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HackerTest {

    private Hacker hacker;
    private Node start;
    private Node nodeKey;
    private Node nodeNoKey;

    @BeforeEach
    public void setUp() {
        start = new Node("Start", 0, 0);
        nodeKey = new Node("Key1", 1, 1);
        nodeNoKey = new Node("Node2", 2, 2);
        hacker = new Hacker(start);
    }

    @Test
    public void testInitialPosition() {
        assertEquals(start, hacker.getCurrentPosition());
        assertEquals(1, hacker.getVisitedNodes().size());
        assertTrue(hacker.getVisitedNodes().contains(start));
    }

    @Test
    public void testMoveToRegularNode() {
        hacker.moveTo(nodeNoKey);
        assertEquals(nodeNoKey, hacker.getCurrentPosition());
        assertTrue(hacker.getVisitedNodes().contains(nodeNoKey));
        assertTrue(hacker.getCollectedKeys().isEmpty());
    }

    @Test
    public void testMoveToKeyNodeAddsKey() {
        hacker.moveTo(nodeKey);
        assertEquals(nodeKey, hacker.getCurrentPosition());
        assertTrue(hacker.getCollectedKeys().contains(nodeKey));
        assertTrue(hacker.getVisitedNodes().contains(nodeKey));
    }

    @Test
    public void testMoveToNullDoesNothing() {
        hacker.moveTo(null);
        assertEquals(start, hacker.getCurrentPosition());
        assertEquals(1, hacker.getVisitedNodes().size());
        assertTrue(hacker.getCollectedKeys().isEmpty());
    }

    @Test
    public void testHasKeyTrue() {
        Node keyNode = new Node("KEYroom", 3, 3);
        assertTrue(hacker.hasKey(keyNode));
    }

    @Test
    public void testHasKeyFalse() {
        Node normalNode = new Node("Lab1", 4, 4);
        assertFalse(hacker.hasKey(normalNode));
    }

    @Test
    public void testResetResetsEverything() {
        hacker.moveTo(nodeKey);
        hacker.reset(nodeNoKey);
        assertEquals(nodeNoKey, hacker.getCurrentPosition());
        assertTrue(hacker.getCollectedKeys().isEmpty());
        assertEquals(1, hacker.getVisitedNodes().size());
        assertEquals(nodeNoKey, hacker.getVisitedNodes().get(0));
    }

    @Test
    public void testSettersUpdateCorrectly() {
        Node newStart = new Node("Home", 5, 5);
        List<Node> keys = List.of(newStart);
        List<Node> visited = List.of(start, nodeNoKey);

        hacker.setCurrentPosition(newStart);
        hacker.setCollectedKeys(keys);
        hacker.setVisitedNodes(visited);

        assertEquals(newStart, hacker.getCurrentPosition());
        assertEquals(keys, hacker.getCollectedKeys());
        assertEquals(visited, hacker.getVisitedNodes());
    }
}
