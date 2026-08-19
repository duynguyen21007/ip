import java.util.Scanner;

public class Milo {
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENTBLOCK = "                   ";
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
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                
                System.out.println(INDENTBLOCK + DIVIDER);

                if (command.equals("bye")) {
                    System.out.println(INDENTBLOCK + "Bye, see you later!");
                    System.out.println(INDENTBLOCK + DIVIDER);
                    break;
                }
                
                System.out.println(INDENTBLOCK + command);
                System.out.println(INDENTBLOCK + DIVIDER);
            }
        }
    }
}
