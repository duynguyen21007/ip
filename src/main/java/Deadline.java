/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates an incomplete deadline with its due date or time.
     *
     * @param description description of the deadline
     * @param by due date or time
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline with its type, completion status, and due date or time.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
