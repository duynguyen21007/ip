import java.util.Scanner;

/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
    private static final String[] TASKS = new String[100];
    /** Stores the completion state for the task at the matching index in {@link #TASKS}. */
    private static final boolean[] TASKS_DONE = new boolean[TASKS.length];

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
                        String statusIcon = TASKS_DONE[i] ? "X" : " ";
                        System.out.println(INDENTBLOCK + (i + 1) + ".[" + statusIcon
                                + "] " + TASKS[i]);
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    setTaskDoneStatus(command, taskCount, true);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    setTaskDoneStatus(command, taskCount, false);
                } else {
                    TASKS[taskCount] = command;
                    taskCount++;
                    System.out.println(INDENTBLOCK + "added: " + command);
                }

                System.out.println(INDENTBLOCK + DIVIDER);
            }
        }
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
        TASKS_DONE[taskIndex] = isDone;
        if (isDone) {
            System.out.println(INDENTBLOCK + "Nice! I've marked this task as done:");
        } else {
            System.out.println(INDENTBLOCK + "OK, I've marked this task as not done yet:");
        }
        String statusIcon = isDone ? "X" : " ";
        System.out.println(INDENTBLOCK + "  [" + statusIcon + "] " + TASKS[taskIndex]);
    }
}
