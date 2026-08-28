package milo.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import milo.command.AddCommand;
import milo.command.Command;
import milo.command.DeleteCommand;
import milo.command.ExitCommand;
import milo.command.ListCommand;
import milo.command.MarkCommand;
import milo.command.UnmarkCommand;
import milo.exception.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.Todo;

/**
 * Interprets user commands and converts their arguments into domain values.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern TODO_COMMAND = Pattern.compile("^todo\\s+(.+)$");
    private static final Pattern DEADLINE_COMMAND = Pattern.compile(
            "^deadline\\s+(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_COMMAND = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");

    /** Creates a parser. */
    public Parser() {
    }

    /**
     * Interprets a full user command and creates the command that can execute it.
     *
     * @param command trimmed command entered by the user
     * @return executable command
     * @throws MiloException if the command or its arguments are invalid
     */
    public static Command parse(String command) throws MiloException {
        CommandType commandType = parseCommandType(command);
        switch (commandType) {
        case BYE:
            return new ExitCommand();
        case LIST:
            return new ListCommand();
        case MARK:
            return new MarkCommand(parseTaskNumber(command, "mark"));
        case UNMARK:
            return new UnmarkCommand(parseTaskNumber(command, "unmark"));
        case DELETE:
            return new DeleteCommand(parseTaskNumber(command, "delete"));
        case TODO:
        case DEADLINE:
        case EVENT:
            return new AddCommand(parseTask(command, commandType));
        case UNKNOWN:
            throw new MiloException("I don't recognize that command :-(");
        default:
            throw new IllegalStateException("Unhandled command type: " + commandType);
        }
    }

    /** Classifies a full user command without interpreting its arguments. */
    private static CommandType parseCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (hasCommandWord(command, "mark")) {
            return CommandType.MARK;
        } else if (hasCommandWord(command, "unmark")) {
            return CommandType.UNMARK;
        } else if (hasCommandWord(command, "delete")) {
            return CommandType.DELETE;
        } else if (hasCommandWord(command, "todo")) {
            return CommandType.TODO;
        } else if (hasCommandWord(command, "deadline")) {
            return CommandType.DEADLINE;
        } else if (hasCommandWord(command, "event")) {
            return CommandType.EVENT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Creates a typed task from a todo, deadline, or event command.
     *
     * @param command full command entered by the user
     * @param commandType type of task command being parsed
     * @return parsed task
     * @throws MiloException if the command lacks required task details
     */
    private static Task parseTask(String command, CommandType commandType) throws MiloException {
        Matcher todoMatcher = TODO_COMMAND.matcher(command);
        if (todoMatcher.matches()) {
            return new Todo(todoMatcher.group(1));
        }

        Matcher deadlineMatcher = DEADLINE_COMMAND.matcher(command);
        if (deadlineMatcher.matches()) {
            return new Deadline(deadlineMatcher.group(1), parseDate(deadlineMatcher.group(2)));
        }

        Matcher eventMatcher = EVENT_COMMAND.matcher(command);
        if (eventMatcher.matches()) {
            return new Event(eventMatcher.group(1), parseDate(eventMatcher.group(2)),
                    parseDate(eventMatcher.group(3)));
        }

        if (commandType == CommandType.TODO) {
            throw new MiloException("A todo needs a description.");
        } else if (commandType == CommandType.DEADLINE) {
            throw new MiloException(
                    "A deadline needs a description followed by /by and a date or time.");
        } else if (commandType == CommandType.EVENT) {
            throw new MiloException(
                    "An event needs a description, /from start, and /to end.");
        }
        throw new IllegalStateException("Not a task command: " + commandType);
    }

    /**
     * Parses the one-based task number used by a task command.
     *
     * @param command full command entered by the user
     * @param action command word whose argument should be parsed
     * @return one-based number of the selected task
     * @throws MiloException if the task number is missing or invalid
     */
    private static int parseTaskNumber(String command, String action) throws MiloException {
        String taskNumberText = command.substring(action.length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new MiloException("Please specify a task number, for example: "
                    + action + " 2");
        }

        return taskNumber;
    }

    /** Returns whether the command is a keyword alone or followed by arguments. */
    private static boolean hasCommandWord(String command, String keyword) {
        return command.equals(keyword)
                || command.startsWith(keyword)
                && command.length() > keyword.length()
                && Character.isWhitespace(command.charAt(keyword.length()));
    }

    /** Parses a date in ISO format and converts malformed input into a user-facing error. */
    private static LocalDate parseDate(String dateText) throws MiloException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new MiloException("Dates must use the format yyyy-MM-dd.");
        }
    }
}
