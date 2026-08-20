/**
 * Represents a task that occurs between a specific start and end date or time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an incomplete event with its start and end date or time.
     *
     * @param description description of the event
     * @param from start date or time
     * @param to end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event with its type, completion status, start, and end.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
