package exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeamExceptionTest {

    @Test
    void testTeamExceptionMessage() {
        String expectedMessage = "This is a custom exception message";
        TeamException exception = new TeamException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testTeamExceptionInstance() {
        TeamException exception = new TeamException("Test message");

        assertTrue(exception instanceof TeamException);
    }
}
