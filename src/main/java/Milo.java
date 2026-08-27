import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
    private static final Path DATA_FILE = Path.of("data", "duke.txt");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final List<Task> TASKS = new ArrayList<>();
    private static final Pattern TODO_COMMAND = Pattern.compile("^todo\\s+(.+)$");
    private static final Pattern DEADLINE_COMMAND = Pattern.compile(
            "^deadline\\s+(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_COMMAND = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");

    public static void main(String[] args) {
        TASKS.clear();
        String chatbotName = "Milo";
        String greeting = DIVIDER + "\n"
                + "Hello! I'm " + chatbotName + ".\n"
                + "How can I help you?\n"
                + DIVIDER;
        String banner = " __  __ _ _       \n"
                      + "|  \\/  (_) | ___  \n"
                      + "| |\\/| | | |/ _ \\ \n"
                      + "| |  | | | | (_) |\n"
                      + "|_|  |_|_|_|\\___/ \n";
        System.out.println(banner + greeting);

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                CommandType commandType = CommandType.from(command);

                System.out.println(INDENTBLOCK + DIVIDER);

                try {
                    switch (commandType) {
                    case BYE:
                        System.out.println(INDENTBLOCK + "Bye, see you later!");
                        System.out.println(INDENTBLOCK + DIVIDER);
                        return;
                    case LIST:
                        System.out.println(INDENTBLOCK + "Here are the tasks in your list:");
                        for (int i = 0; i < TASKS.size(); i++) {
                            System.out.println(INDENTBLOCK + (i + 1) + "." + TASKS.get(i));
                        }
                        break;
                    case MARK:
                        setTaskDoneStatus(command, true);
                        break;
                    case UNMARK:
                        setTaskDoneStatus(command, false);
                        break;
                    case DELETE:
                        deleteTask(command);
                        break;
                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        Task newTask = createTask(command, commandType);
                        TASKS.add(newTask);
                        try {
                            saveTasks();
                        } catch (MiloException exception) {
                            TASKS.remove(TASKS.size() - 1);
                            throw exception;
                        }
                        printTaskAdded(newTask);
                        break;
                    case UNKNOWN:
                        throw new MiloException("I don't recognize that command :-(");
                    default:
                        throw new IllegalStateException("Unhandled command type: " + commandType);
                    }
                } catch (MiloException exception) {
                    System.out.println(INDENTBLOCK + "OOPS!!! " + exception.getMessage());
                }

                System.out.println(INDENTBLOCK + DIVIDER);
            }
        }
    }

    /**
     * Creates a typed task from a todo, deadline, or event command.
     *
     * @param command full command entered by the user
     * @param commandType type of task command being parsed
     * @return the parsed task
     * @throws MiloException if the command lacks required task details
     */
    private static Task createTask(String command, CommandType commandType)
            throws MiloException {
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

    /** Parses a date in ISO format and converts malformed input into a user-facing error. */
    private static LocalDate parseDate(String dateText) throws MiloException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new MiloException("Dates must use the format yyyy-MM-dd.");
        }
    }

    /**
     * Prints confirmation that a task was added and shows the new list size.
     *
     * @param task task that was added
     */
    private static void printTaskAdded(Task task) {
        System.out.println(INDENTBLOCK + "Got it. I've added this task:");
        System.out.println(INDENTBLOCK + "  " + task);
        System.out.println(INDENTBLOCK + "Now you have " + TASKS.size()
                + " tasks in the list.");
    }

    /**
     * Updates the completion state of the task number supplied in a mark or unmark command.
     *
     * @param command full command entered by the user
     * @param isDone new completion state for the selected task
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private static void setTaskDoneStatus(String command, boolean isDone)
            throws MiloException {
        String action = isDone ? "mark" : "unmark";
        int taskIndex = parseTaskIndex(command, action);
        Task selectedTask = TASKS.get(taskIndex);
        if (isDone) {
            selectedTask.markAsDone();
            System.out.println(INDENTBLOCK + "Nice! I've marked this task as done:");
        } else {
            selectedTask.markAsNotDone();
            System.out.println(INDENTBLOCK + "OK, I've marked this task as not done yet:");
        }
        try {
            saveTasks();
        } catch (MiloException exception) {
            if (isDone) {
                selectedTask.markAsNotDone();
            } else {
                selectedTask.markAsDone();
            }
            throw exception;
        }
        System.out.println(INDENTBLOCK + "  " + selectedTask);
    }

    /**
     * Removes the selected task; the list shifts later tasks to keep numbering contiguous.
     *
     * @param command full delete command entered by the user
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private static void deleteTask(String command) throws MiloException {
        int taskIndex = parseTaskIndex(command, "delete");
        Task deletedTask = TASKS.remove(taskIndex);
        try {
            saveTasks();
        } catch (MiloException exception) {
            TASKS.add(taskIndex, deletedTask);
            throw exception;
        }
        System.out.println(INDENTBLOCK + "Noted. I've removed this task:");
        System.out.println(INDENTBLOCK + "  " + deletedTask);
        System.out.println(INDENTBLOCK + "Now you have " + TASKS.size()
                + " tasks in the list.");
    }

    /**
     * Parses and validates the one-based task number used by a task command.
     *
     * @param command full command entered by the user
     * @param action command word whose argument should be parsed
     * @return zero-based index of the selected task
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private static int parseTaskIndex(String command, String action)
            throws MiloException {
        String taskNumberText = command.substring(action.length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new MiloException("Please specify a task number, for example: "
                    + action + " 2");
        }

        if (taskNumber < 1 || taskNumber > TASKS.size()) {
            throw new MiloException("There is no task numbered " + taskNumber + ".");
        }
        return taskNumber - 1;
    }

    /** Saves the current task list in a simple line-based format. */
    private static void saveTasks() throws MiloException {
        Path temporaryFile = DATA_FILE.resolveSibling(DATA_FILE.getFileName() + ".tmp");
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> taskLines = TASKS.stream().map(Task::toString).toList();
            Files.write(temporaryFile, taskLines);
            try {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            throw new MiloException("I couldn't save your tasks.");
        } finally {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException | SecurityException exception) {
                // The next save will replace the temporary file.
            }
        }
    }
}
