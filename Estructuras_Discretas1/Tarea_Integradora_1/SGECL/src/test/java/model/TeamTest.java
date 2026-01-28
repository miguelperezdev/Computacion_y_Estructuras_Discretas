package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private Team team;
    private Team otherTeam;

    @BeforeEach
    void setUp() {
        team = new Team("Real Madrid", "Spain", 35, 90);
        otherTeam = new Team("Barcelona", "Spain", 30, 85);
    }


    @Test
    void testGetNamePositive() {
        assertEquals("Real Madrid", team.getName());
    }

    @Test
    void testSetNameNegative() {
        team.setName("PSG");
        assertNotEquals("Real Madrid", team.getName());
    }

    @Test
    void testSetNameInteresting() {
        team.setName("Liverpool");
        assertTrue(team.getName().contains("Liver"));
    }


    @Test
    void testGetCountryPositive() {
        assertEquals("Spain", team.getCountry());
    }

    @Test
    void testSetCountryNegative() {
        team.setCountry("France");
        assertNotEquals("Spain", team.getCountry());
    }

    @Test
    void testSetCountryInteresting() {
        team.setCountry("Germany");
        assertTrue(team.getCountry().startsWith("Ger"));
    }

    @Test
    void testGetTitlesPositive() {
        assertEquals(35, team.getTitles());
    }

    @Test
    void testSetTitlesNegative() {
        team.setTitles(40);
        assertNotEquals(35, team.getTitles());
    }

    @Test
    void testSetTitlesInteresting() {
        team.setTitles(50);
        assertTrue(team.getTitles() > 40);
    }

    @Test
    void testGetCoefficientPositive() {
        assertEquals(90, team.getCoefficient());
    }

    @Test
    void testSetCoefficientNegative() {
        team.setCoefficient(70);
        assertNotEquals(90, team.getCoefficient());
    }

    @Test
    void testSetCoefficientInteresting() {
        team.setCoefficient(95);
        assertTrue(team.getCoefficient() > 90);
    }

    @Test
    void testAddPointPositive() {
        team.addPoint(3);
        assertEquals(3, team.getTotalPoints());
    }

    @Test
    void testAddPointNegative() {
        team.addPoint(5);
        assertNotEquals(0, team.getTotalPoints());
    }

    @Test
    void testAddPointInteresting() {
        team.addPoint(-2);
        assertTrue(team.getTotalPoints() < 0);
    }

    @Test
    void testPriorityTeamPositive() {
        team.addPoint(10);
        assertTrue(team.priorityTeam(otherTeam));
    }

    @Test
    void testPriorityTeamNegative() {
        otherTeam.addPoint(20);
        assertFalse(team.priorityTeam(otherTeam));
    }

    @Test
    void testPriorityTeamInteresting() {
        team.addPoint(5);
        otherTeam.addPoint(5);
        assertTrue(team.priorityTeam(otherTeam)); // gana por mayor coeficiente
    }

    @Test
    void testCompareToPositive() {
        team.addPoint(10);
        assertTrue(team.compareTo(otherTeam) > 0);
    }

    @Test
    void testCompareToNegative() {
        otherTeam.addPoint(20);
        assertFalse(team.compareTo(otherTeam) < 0);
    }

    @Test
    void testCompareToInteresting() {
        team.addPoint(5);
        otherTeam.addPoint(5);
        assertTrue(team.compareTo(otherTeam) > 0);
    }

    @Test
    void testToStringPositive() {
        String result = team.toString();
        assertTrue(result.contains("Real Madrid"));
        assertTrue(result.contains("country: Spain"));
    }

    @Test
    void testToStringNegative() {
        String result = team.toString();
        assertFalse(result.contains("Barcelona"));
    }

    @Test
    void testToStringInteresting() {
        String result = team.toString();
        assertTrue(result.contains("coefficient: 90"));
    }
}
