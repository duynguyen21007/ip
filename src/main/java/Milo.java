import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
    private static final Task[] TASKS = new Task[100];
    private static final Pattern TODO_COMMAND = Pattern.compile("^todo\\s+(.+)$");
    private static final Pattern DEADLINE_COMMAND = Pattern.compile(
            "^deadline\\s+(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_COMMAND = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");

    public static void main(String[] args) {
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
            int taskCount = 0;
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();

                System.out.println(INDENTBLOCK + DIVIDER);

                if (command.equals("bye")) {
                    System.out.println(INDENTBLOCK + "Bye, see you later!");
                    System.out.println(INDENTBLOCK + DIVIDER);
                    break;
                } else if (command.equals("list")) {
                    System.out.println(INDENTBLOCK + "Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENTBLOCK + (i + 1) + "." + TASKS[i]);
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    setTaskDoneStatus(command, taskCount, true);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    setTaskDoneStatus(command, taskCount, false);
                } else {
                    Task newTask = createTask(command);
                    if (newTask != null) {
                        TASKS[taskCount] = newTask;
                        taskCount++;
                        printTaskAdded(newTask, taskCount);
                    }
                }

                System.out.println(INDENTBLOCK + DIVIDER);
            }
        }
    }

    /**
     * Creates a typed task from a todo, deadline, or event command.
     *
     * @param command full command entered by the user
     * @return the parsed task, or {@code null} when the command is invalid
     */
    private static Task createTask(String command) {
        Matcher todoMatcher = TODO_COMMAND.matcher(command);
        if (todoMatcher.matches()) {
            return new Todo(todoMatcher.group(1));
        }

        Matcher deadlineMatcher = DEADLINE_COMMAND.matcher(command);
        if (deadlineMatcher.matches()) {
            return new Deadline(deadlineMatcher.group(1), deadlineMatcher.group(2));
        }

        Matcher eventMatcher = EVENT_COMMAND.matcher(command);
        if (eventMatcher.matches()) {
            return new Event(eventMatcher.group(1), eventMatcher.group(2), eventMatcher.group(3));
        }

        if (command.equals("todo") || command.startsWith("todo ")) {
            System.out.println(INDENTBLOCK + "Use: todo <description>");
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            System.out.println(INDENTBLOCK + "Use: deadline <description> /by <date/time>");
        } else if (command.equals("event") || command.startsWith("event ")) {
            System.out.println(INDENTBLOCK
                    + "Use: event <description> /from <start> /to <end>");
        } else {
            System.out.println(INDENTBLOCK
                    + "Unknown command. Use todo, deadline, event, list, mark, unmark, or bye.");
        }
        return null;
    }

    /**
     * Prints confirmation that a task was added and shows the new list size.
     *
     * @param task task that was added
     * @param taskCount updated number of tasks in the list
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(INDENTBLOCK + "Got it. I've added this task:");
        System.out.println(INDENTBLOCK + "  " + task);
        System.out.println(INDENTBLOCK + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Updates the completion state of the task number supplied in a mark or unmark command.
     *
     * @param command full command entered by the user
     * @param taskCount number of tasks currently stored
     * @param isDone new completion state for the selected task
     */
    private static void setTaskDoneStatus(String command, int taskCount, boolean isDone) {
        String action = isDone ? "mark" : "unmark";
        String taskNumberText = command.substring(action.length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            System.out.println(INDENTBLOCK + "Please specify a task number, for example: "
                    + action + " 2");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            System.out.println(INDENTBLOCK + "There is no task numbered " + taskNumber + ".");
            return;
        }

        int taskIndex = taskNumber - 1;
        Task selectedTask = TASKS[taskIndex];
        if (isDone) {
            selectedTask.markAsDone();
            System.out.println(INDENTBLOCK + "Nice! I've marked this task as done:");
        } else {
            selectedTask.markAsNotDone();
            System.out.println(INDENTBLOCK + "OK, I've marked this task as not done yet:");
        }
        System.out.println(INDENTBLOCK + "  " + selectedTask);
    }
}
