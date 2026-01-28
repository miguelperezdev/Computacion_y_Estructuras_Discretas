package structures;

import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, Team> hashTable;
    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>();
        team1 = new Team("Real Madrid", "Spain", 35, 90);
        team2 = new Team("Barcelona", "Spain", 30, 85);
    }



    @Test
    void testInsertPositive() {
        hashTable.insert(team1.getName(), team1);
        assertEquals(1, hashTable.getSize());
    }

    @Test
    void testInsertNegative() {
        assertEquals(0, hashTable.getSize());
    }

    @Test
    void testInsertInteresting() {
        hashTable.insert(team1.getName(), team1);
        hashTable.insert(team1.getName(), team2);
        assertEquals(1, hashTable.getSize());
    }



    @Test
    void testObtainPositive() {
        hashTable.insert(team1.getName(), team1);
        assertEquals(team1, hashTable.obtain("Real Madrid"));
    }

    @Test
    void testObtainNegative() {
        assertNull(hashTable.obtain("PSG"));
    }

    @Test
    void testObtainInteresting() {
        hashTable.insert(team1.getName(), team1);
        hashTable.insert(team2.getName(), team2);
        assertNotNull(hashTable.obtain("Barcelona"));
    }



    @Test
    void testDeletePositive() {
        hashTable.insert(team1.getName(), team1);
        hashTable.delete("Real Madrid");
        assertNull(hashTable.obtain("Real Madrid"));
    }

    @Test
    void testDeleteNegative() {
        hashTable.delete("PSG");
        assertEquals(0, hashTable.getSize());
    }

    @Test
    void testDeleteInteresting() {
        hashTable.insert(team1.getName(), team1);
        hashTable.insert(team2.getName(), team2);
        hashTable.delete("Real Madrid");
        assertNotNull(hashTable.obtain("Barcelona"));
    }



    @Test
    void testIsEmptyPositive() {
        assertTrue(hashTable.isEmpty());
    }

    @Test
    void testIsEmptyNegative() {
        hashTable.insert(team1.getName(), team1);
        assertFalse(hashTable.isEmpty());
    }

    @Test
    void testIsEmptyInteresting() {
        hashTable.insert(team1.getName(), team1);
        hashTable.delete("Real Madrid");
        assertTrue(hashTable.isEmpty());
    }



    @Test
    void testGetSizePositive() {
        hashTable.insert(team1.getName(), team1);
        assertEquals(1, hashTable.getSize());
    }

    @Test
    void testGetSizeNegative() {
        assertNotEquals(1, hashTable.getSize());
    }

    @Test
    void testGetSizeInteresting() {
        hashTable.insert(team1.getName(), team1);
        hashTable.insert(team2.getName(), team2);
        assertEquals(2, hashTable.getSize());
    }
}
