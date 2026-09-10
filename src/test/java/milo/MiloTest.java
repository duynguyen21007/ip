package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses using isolated storage for every test. */
public class MiloTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_returnsCommandOutput() {
        Milo milo = new Milo(temporaryDirectory.resolve("valid-command.txt").toString());

        String response = milo.getResponse("todo read book");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorOutput() {
        Milo milo = new Milo(temporaryDirectory.resolve("invalid-command.txt").toString());

        String response = milo.getResponse("unknown");

        assertEquals("OOPS!!! I don't recognize that command :-(", response);
    }
}
