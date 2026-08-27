import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
    private static final Path DATA_FILE = Path.of("data", "duke.txt");
    private static final TaskList TASKS = new TaskList();

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
                CommandType commandType = Parser.parseCommandType(command);

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
                        Task newTask = Parser.parseTask(command, commandType);
                        TASKS.add(newTask);
                        try {
                            saveTasks();
                        } catch (MiloException exception) {
                            TASKS.delete(TASKS.size() - 1);
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
        int taskIndex = Parser.parseTaskIndex(command, action, TASKS.size());
        Task selectedTask;
        if (isDone) {
            selectedTask = TASKS.mark(taskIndex);
            System.out.println(INDENTBLOCK + "Nice! I've marked this task as done:");
        } else {
            selectedTask = TASKS.unmark(taskIndex);
            System.out.println(INDENTBLOCK + "OK, I've marked this task as not done yet:");
        }
        try {
            saveTasks();
        } catch (MiloException exception) {
            if (isDone) {
                TASKS.unmark(taskIndex);
            } else {
                TASKS.mark(taskIndex);
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
        int taskIndex = Parser.parseTaskIndex(command, "delete", TASKS.size());
        Task deletedTask = TASKS.delete(taskIndex);
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

    /** Saves the current task list in a simple line-based format. */
    private static void saveTasks() throws MiloException {
        Path temporaryFile = DATA_FILE.resolveSibling(DATA_FILE.getFileName() + ".tmp");
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> taskLines = TASKS.getTasks().stream().map(Task::toString).toList();
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
