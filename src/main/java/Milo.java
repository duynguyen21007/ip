import java.util.Scanner;

/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
    private static final String[] TASKS = new String[100];

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
                String command = scanner.nextLine();

                System.out.println(INDENTBLOCK + DIVIDER);

                if (command.equals("bye")) {
                    System.out.println(INDENTBLOCK + "Bye, see you later!");
                    System.out.println(INDENTBLOCK + DIVIDER);
                    break;
                } else if (command.equals("list")) {
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENTBLOCK + (i + 1) + ". " + TASKS[i]);
                    }
                } else {
                    TASKS[taskCount] = command;
                    taskCount++;
                    System.out.println(INDENTBLOCK + "added: " + command);
                }

                System.out.println(INDENTBLOCK + DIVIDER);
            }
        }
    }
}
