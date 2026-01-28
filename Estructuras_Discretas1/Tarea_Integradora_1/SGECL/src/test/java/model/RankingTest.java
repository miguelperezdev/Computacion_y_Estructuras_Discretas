package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RankingTest {

    private Ranking ranking;
    private Team team1;
    private Team team2;
    private Team team3;

    @BeforeEach
    void setUp() {
        ranking = new Ranking();
        team1 = new Team("Real Madrid", "Spain", 35, 15);
        team2 = new Team("Barcelona", "Spain", 30, 10);
        team3 = new Team("Bayern Munich", "Germany", 25, 20);
    }

    @Test
    void testAddTeamPositive() {
        ranking.addTeam(team1);
        assertFalse(ranking.isEmpty());
    }

    @Test
    void testAddTeamNegative() {
        assertTrue(ranking.isEmpty());
    }

    @Test
    void testAddTeamInteresting() {
        ranking.addTeam(team1);
        ranking.addTeam(team2);
        ranking.addTeam(team3);
        assertEquals("Bayern Munich", ranking.getTopTeam().getName());
    }

    @Test
    void testTakeRankingAsStringPositive() {
        ranking.addTeam(team1);
        String result = ranking.takeRankingAsString();
        assertTrue(result.contains("Real Madrid"));
    }

    @Test
    void testTakeRankingAsStringNegative() {
        String result = ranking.takeRankingAsString();
        assertEquals("No teams registered.", result);
    }

    @Test
    void testTakeRankingAsStringInteresting() {
        ranking.addTeam(team2);
        ranking.addTeam(team3);
        String result = ranking.takeRankingAsString();
        assertTrue(result.contains("Barcelona") && result.contains("Bayern Munich"));
    }

    @Test
    void testGetTopTeamPositive() {
        ranking.addTeam(team1);
        ranking.addTeam(team2);
        assertEquals("Real Madrid", ranking.getTopTeam().getName());
    }

    @Test
    void testGetTopTeamNegative() {
        assertNull(ranking.getTopTeam());
    }

    @Test
    void testGetTopTeamInteresting() {
        ranking.addTeam(team2);
        ranking.addTeam(team3);
        assertEquals("Bayern Munich", ranking.getTopTeam().getName());
    }

    @Test
    void testIsEmptyPositive() {
        assertTrue(ranking.isEmpty());
    }

    @Test
    void testIsEmptyNegative() {
        ranking.addTeam(team1);
        assertFalse(ranking.isEmpty());
    }

    @Test
    void testIsEmptyInteresting() {
        ranking.addTeam(team1);
        ranking.removeTeam(team1);
        assertTrue(ranking.isEmpty());
    }

    @Test
    void testRemoveTeamPositive() {
        ranking.addTeam(team1);
        ranking.removeTeam(team1);
        assertTrue(ranking.isEmpty());
    }

    @Test
    void testRemoveTeamNegative() {
        ranking.removeTeam(team2); // quitar un equipo que nunca se añadió
        assertTrue(ranking.isEmpty());
    }

    @Test
    void testRemoveTeamInteresting() {
        ranking.addTeam(team1);
        ranking.addTeam(team2);
        ranking.removeTeam(team1);
        assertEquals("Barcelona", ranking.getTopTeam().getName());
    }
}
