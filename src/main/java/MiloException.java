/**
 * Represents an input error that Milo can explain to the user and recover from.
 */
public class MiloException extends Exception {
    /**
     * Creates an exception carrying a user-friendly explanation of the input error.
     *
     * @param message explanation to display to the user
     */
    public MiloException(String message) {
        super(message);
    }
}
