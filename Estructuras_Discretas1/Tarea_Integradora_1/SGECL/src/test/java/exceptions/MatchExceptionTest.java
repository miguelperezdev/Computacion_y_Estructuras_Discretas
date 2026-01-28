package exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchExceptionTest {

    @Test
    void testMatchExceptionMessage() {
        String expectedMessage = "Match cannot be played due to some issue";
        MatchException exception = new MatchException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testMatchExceptionInstance() {
        MatchException exception = new MatchException("Test message");

        assertTrue(exception instanceof MatchException);
    }
}
