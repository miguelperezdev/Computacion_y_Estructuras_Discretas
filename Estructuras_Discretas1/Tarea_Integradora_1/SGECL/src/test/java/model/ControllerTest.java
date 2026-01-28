package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exceptions.TeamException;
import exceptions.MatchException;
import structures.*;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
    }

    @Test
    public void testAddTeamSuccess() {
        String result = controller.addTeam("Barcelona", "Spain", 5, 100);
        assertEquals("Team Barcelona added successfully.", result);
    }

    @Test
    public void testAddTeamDuplicate() {
        controller.addTeam("Real Madrid", "Spain", 13, 120);
        String result = controller.addTeam("Real Madrid", "Spain", 13, 120);
        assertEquals("The team Real Madrid already exists.", result);
    }

    @Test
    public void testAddMatchSuccess() {
        controller.addTeam("Bayern", "Germany", 6, 110);
        controller.addTeam("Dortmund", "Germany", 1, 90);

        String result = controller.addMatch("Bayern", "Dortmund", 2, 1, "2023-05-01");
        assertTrue(result.startsWith("Match Match 0 added successfully"));
    }

    @Test
    public void testAddMatchNegativeGoals() {
        controller.addTeam("PSG", "France", 0, 95);
        controller.addTeam("Lyon", "France", 0, 80);

        String result = controller.addMatch("PSG", "Lyon", -1, 2, "2023-05-02");
        assertEquals("The goals can´t be negative", result);
    }

    @Test
    public void testAddMatchTeamNotExist() {
        controller.addTeam("Chelsea", "England", 2, 105);

        String result = controller.addMatch("Chelsea", "Arsenal", 1, 1, "2023-05-03");
        assertEquals("One or both teams are not registered.", result);
    }

    @Test
    public void testUndoAddTeam() {
        controller.addTeam("Juventus", "Italy", 2, 98);
        String result = controller.undo();
        assertEquals("The team: Juventus has been deleted successfully.", result);
    }

    @Test
    public void testUndoAddMatch() {
        controller.addTeam("Inter", "Italy", 3, 102);
        controller.addTeam("Milan", "Italy", 1, 99);
        controller.addMatch("Inter", "Milan", 0, 0, "2023-05-04");

        String result = controller.undo();
        assertTrue(result.startsWith("The match : Match 0 has been deleted and the points has been reverted"));
    }

    @Test
    public void testUndoNoActions() {
        String result = controller.undo();
        assertEquals("No actions to undo", result);
    }

    @Test
    public void testEnqueueMatchSuccess() {
        controller.addTeam("Liverpool", "England", 6, 115);
        controller.addTeam("Everton", "England", 0, 75);
        controller.addMatch("Liverpool", "Everton", 2, 0, "2023-05-05");

        String result = controller.enqueueMatch("Match 0");
        assertEquals("Match Match 0 enqueued successfully.", result);
    }

    @Test
    public void testEnqueueMatchNotFound() {
        String result = controller.enqueueMatch("Match 99");
        assertEquals("No match no found with ID Match 99", result);
    }

    @Test
    public void testMatchScheduleEmpty() {
        String result = controller.matchSchedule();
        assertEquals("No matches in the schedule", result);
    }

    @Test
    public void testMatchScheduleWithMatches() {
        controller.addTeam("Man City", "England", 1, 118);
        controller.addTeam("Man United", "England", 3, 108);
        controller.addMatch("Man City", "Man United", 3, 1, "2023-05-06");
        controller.enqueueMatch("Match 0");

        String result = controller.matchSchedule();
        assertTrue(result.startsWith("Upcoming matches:"));
        assertTrue(result.contains("Man City"));
        assertTrue(result.contains("Man United"));
    }

    @Test
    public void testTeamRankingEmpty() {
        String result = controller.teamRanking();
        assertNotNull(result);
        assertTrue(result.isEmpty() || result.contains("No teams"));
    }

    @Test
    public void testTeamRankingWithTeams() {
        controller.addTeam("Ajax", "Netherlands", 4, 92);
        controller.addTeam("PSV", "Netherlands", 1, 88);

        String result = controller.teamRanking();
        assertTrue(result.contains("Ajax"));
        assertTrue(result.contains("PSV"));
    }

    @Test
    public void testPublicSearchTeamSuccess() throws TeamException {
        controller.addTeam("Benfica", "Portugal", 2, 85);
        String result = controller.publicSearchTeam("Benfica");
        assertTrue(result.startsWith("Team found:"));
        assertTrue(result.contains("Benfica"));
    }

    @Test
    public void testPublicSearchTeamNotFound() {
        assertThrows(TeamException.class, () -> {
            controller.publicSearchTeam("Porto");
        });
    }

    @Test
    public void testPublicSearchMatchSuccess() throws MatchException {
        controller.addTeam("Atletico", "Spain", 0, 97);
        controller.addTeam("Valencia", "Spain", 0, 82);
        controller.addMatch("Atletico", "Valencia", 1, 0, "2023-05-07");
        String result = controller.publicSearchMatch("Match 0");
        assertTrue(result.contains("Atletico"));
        assertTrue(result.contains("Valencia"));
    }

    @Test
    public void testPublicSearchMatchNotFound() {
        assertThrows(MatchException.class, () -> {
            controller.publicSearchMatch("Match 99");
        });
    }

    @Test
    public void testSearchTeamInternal() {
        controller.addTeam("Sevilla", "Spain", 0, 87);
        Team team = controller.searchTeam("Sevilla");
        assertNotNull(team);
        assertEquals("Sevilla", team.getName());
    }

    @Test
    public void testSearchTeamInternalNotFound() {
        Team team = controller.searchTeam("Betis");
        assertNull(team);
    }

    @Test
    public void testSearchMatchInternal() {
        controller.addTeam("Leipzig", "Germany", 0, 84);
        controller.addTeam("Hoffenheim", "Germany", 0, 76);
        controller.addMatch("Leipzig", "Hoffenheim", 2, 2, "2023-05-08");

        Match match = controller.searchMatch("Match 0");
        assertNotNull(match);
        assertEquals("Match 0", match.getId());
    }

    @Test
    public void testSearchMatchInternalNotFound() {
        Match match = controller.searchMatch("Match 99");
        assertNull(match);
    }

    @Test
    public void testPointsDistributionWin() {
        controller.addTeam("Tottenham", "England", 0, 96);
        controller.addTeam("Arsenal", "England", 0, 103);
        controller.addMatch("Tottenham", "Arsenal", 2, 1, "2023-05-09");

        Team tottenham = controller.searchTeam("Tottenham");
        Team arsenal = controller.searchTeam("Arsenal");

        assertEquals(3, tottenham.getTotalPoints());
        assertEquals(0, arsenal.getTotalPoints());
    }

    @Test
    public void testPointsDistributionDraw() {
        controller.addTeam("Roma", "Italy", 0, 89);
        controller.addTeam("Lazio", "Italy", 0, 86);
        controller.addMatch("Roma", "Lazio", 0, 0, "2023-05-10");

        Team roma = controller.searchTeam("Roma");
        Team lazio = controller.searchTeam("Lazio");

        assertEquals(1, roma.getTotalPoints());
        assertEquals(1, lazio.getTotalPoints());
    }
}