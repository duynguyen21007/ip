package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MiloTest {
    @Test
    public void getResponse_validCommand_returnsCommandOutput() {
        Milo milo = new Milo("build/test-data/MiloTest/valid-command.txt");

        String response = milo.getResponse("todo read book");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorOutput() {
        Milo milo = new Milo("build/test-data/MiloTest/invalid-command.txt");

        String response = milo.getResponse("unknown");

        assertEquals("OOPS!!! I don't recognize that command :-(", response);
    }
}
