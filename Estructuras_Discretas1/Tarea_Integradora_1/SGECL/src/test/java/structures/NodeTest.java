package structures;

import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    private Node<Team> node;
    private Team realMadrid;
    private Team barcelona;

    @BeforeEach
    void setUp() {
        realMadrid = new Team("Real Madrid", "Spain", 35, 90);
        barcelona = new Team("Barcelona", "Spain", 30, 85);
        node = new Node<>(realMadrid);
    }



    @Test
    void testGetDataPositive() {
        assertEquals(realMadrid, node.getData());
    }

    @Test
    void testSetDataNegative() {
        node.setData(barcelona);
        assertNotEquals(realMadrid, node.getData());
    }

    @Test
    void testSetDataInteresting() {
        Team bayern = new Team("Bayern Munich", "Germany", 30, 88);
        node.setData(bayern);
        assertEquals("Bayern Munich", node.getData().getName());
    }

    @Test
    void testGetNextPositive() {
        assertNull(node.getNext());
    }

    @Test
    void testSetNextNegative() {
        Node<Team> nextNode = new Node<>(barcelona);
        node.setNext(nextNode);
        assertNotNull(node.getNext());
        assertNotEquals(realMadrid, node.getNext().getData());
    }

    @Test
    void testSetNextInteresting() {
        Node<Team> nextNode = new Node<>(barcelona);
        node.setNext(nextNode);
        assertEquals("Barcelona", node.getNext().getData().getName());
    }
}
