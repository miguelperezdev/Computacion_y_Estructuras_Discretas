package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    @Test
    void testActionCreationAndGetters() {
        Action<String> action = new Action<>("CREATE", "Test Object");

        assertEquals("CREATE", action.getType());
        assertEquals("Test Object", action.getObject());
    }

    @Test
    void testActionSetters() {
        Action<Integer> action = new Action<>("UPDATE", 123);

        action.setType("DELETE");
        action.setObject(456);

        assertEquals("DELETE", action.getType());
        assertEquals(456, action.getObject());
    }

    @Test
    void testActionWithCustomObject() {
        Team team = new Team("Test Team", "TT", 4, 5);
        Action<Team> action = new Action<>("ADD_TEAM", team);

        assertEquals("ADD_TEAM", action.getType());
        assertEquals(team, action.getObject());
        assertEquals("Test Team", action.getObject().getName());
    }
}
