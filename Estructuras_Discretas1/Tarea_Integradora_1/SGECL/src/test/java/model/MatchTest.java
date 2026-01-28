package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private Match match;
    private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        homeTeam = new Team("Real Madrid", "Spain", 35, 15);
        awayTeam = new Team("Barcelona", "Spain", 30, 10);
        match = new Match(homeTeam, awayTeam, 2, 1, "2025-05-10", "M001");
    }

    @Test
    void testGetHomeTeamPositive() {
        assertEquals(homeTeam, match.getHomeTeam());
    }

    @Test
    void testSetHomeTeamNegative() {
        Team newTeam = new Team("PSG", "France", 20, 10);
        match.setHomeTeam(newTeam);
        assertNotEquals(homeTeam, match.getHomeTeam());
    }

    @Test
    void testSetHomeTeamInteresting() {
        Team newTeam = new Team("Bayern", "Germany", 25, 12);
        match.setHomeTeam(newTeam);
        assertEquals("Bayern", match.getHomeTeam().getName());
    }

    // === Tests para getAwayTeam y setAwayTeam ===

    @Test
    void testGetAwayTeamPositive() {
        assertEquals(awayTeam, match.getAwayTeam());
    }

    @Test
    void testSetAwayTeamNegative() {
        Team newTeam = new Team("Chelsea", "England", 18, 12);
        match.setAwayTeam(newTeam);
        assertNotEquals(awayTeam, match.getAwayTeam());
    }

    @Test
    void testSetAwayTeamInteresting() {
        Team newTeam = new Team("Juventus", "Italy", 22, 14);
        match.setAwayTeam(newTeam);
        assertEquals("Juventus", match.getAwayTeam().getName());
    }

    @Test
    void testGetHomeGoalsPositive() {
        assertEquals(2, match.getHomeGoals());
    }

    @Test
    void testSetHomeGoalsNegative() {
        match.setHomeGoals(3);
        assertNotEquals(2, match.getHomeGoals());
    }

    @Test
    void testSetHomeGoalsInteresting() {
        match.setHomeGoals(5);
        assertEquals(5, match.getHomeGoals());
    }

    @Test
    void testGetAwayGoalsPositive() {
        assertEquals(1, match.getAwayGoals());
    }

    @Test
    void testSetAwayGoalsNegative() {
        match.setAwayGoals(4);
        assertNotEquals(1, match.getAwayGoals());
    }

    @Test
    void testSetAwayGoalsInteresting() {
        match.setAwayGoals(0);
        assertEquals(0, match.getAwayGoals());
    }

    @Test
    void testGetDatePositive() {
        assertEquals("2025-05-10", match.getDate());
    }

    @Test
    void testSetDateNegative() {
        match.setDate("2026-01-01");
        assertNotEquals("2025-05-10", match.getDate());
    }

    @Test
    void testSetDateInteresting() {
        match.setDate("2024-12-31");
        assertEquals("2024-12-31", match.getDate());
    }

    @Test
    void testGetIdPositive() {
        assertEquals("M001", match.getId());
    }

    @Test
    void testGetIdNegative() {
        assertNotEquals("M999", match.getId());
    }

    @Test
    void testGetIdInteresting() {
        assertTrue(match.getId().startsWith("M"));
    }

    @Test
    void testToStringPositive() {
        String text = match.toString();
        assertTrue(text.contains("Real Madrid"));
        assertTrue(text.contains("Barcelona"));
        assertTrue(text.contains("2025-05-10"));
    }

    @Test
    void testToStringNegative() {
        String text = match.toString();
        assertFalse(text.contains("Manchester United"));
    }

    @Test
    void testToStringInteresting() {
        String text = match.toString();
        assertTrue(text.contains("away goals: 1") && text.contains("home goals: 2"));
    }
}
