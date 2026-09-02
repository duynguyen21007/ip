package milo.ui;

import java.io.PrintStream;
import java.util.Scanner;

import milo.task.Task;
import milo.task.TaskList;

/**
 * Handles Milo's console input and output.
 */
public class Ui implements AutoCloseable {
    private static final String CHATBOT_NAME = "Milo";
    private static final String DIVIDER = "-----------------------------------";
    private static final String INDENT_BLOCK = "                   ";
    private static final String BANNER = " __  __ _ _       \n"
            + "|  \\/  (_) | ___  \n"
            + "| |\\/| | | |/ _ \\ \n"
            + "| |  | | | | (_) |\n"
            + "|_|  |_|_|_|\\___/ \n";

    private final Scanner scanner;
    private final PrintStream output;
    private final String indentBlock;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out;
        this.indentBlock = INDENT_BLOCK;
    }

    /**
     * Creates an output-only UI used to collect a command response.
     *
     * @param output destination for the response.
     */
    public Ui(PrintStream output) {
        this.scanner = null;
        this.output = output;
        this.indentBlock = "";
    }

    /** Shows Milo's banner and greeting. */
    public void showWelcome() {
        String greeting = DIVIDER + "\n"
                + "Hello! I'm " + CHATBOT_NAME + ".\n"
                + "How can I help you?\n"
                + DIVIDER;
        output.println(BANNER + greeting);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another command is available, otherwise {@code false}.
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Returns the next command with surrounding whitespace removed.
     *
     * @return next trimmed command.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not accept console input.");
        }
        return scanner.nextLine().trim();
    }

    /** Shows the divider between Milo's responses. */
    public void showLine() {
        output.println(indentBlock + DIVIDER);
    }

    /** Shows Milo's farewell message. */
    public void showGoodbye() {
        output.println(indentBlock + "Bye, see you later!");
    }

    /**
     * Shows every task with its one-based list number.
     *
     * @param tasks tasks to show.
     */
    public void showTaskList(TaskList tasks) {
        output.println(indentBlock + "Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows tasks matching a find command with new one-based result numbers.
     *
     * @param matchingTasks matching tasks to show.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        output.println(indentBlock + "Here are the matching tasks in your list:");
        showNumberedTasks(matchingTasks);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(indentBlock + "Got it. I've added this task:");
        showTask(task);
        output.println(indentBlock + "Now you have " + taskCount
                + " tasks in the list.");
    }

    /** Shows the heading for a task that was marked as done. */
    public void showTaskMarkedHeader() {
        output.println(indentBlock + "Nice! I've marked this task as done:");
    }

    /** Shows the heading for a task that was marked as not done. */
    public void showTaskUnmarkedHeader() {
        output.println(indentBlock + "OK, I've marked this task as not done yet:");
    }

    /**
     * Shows a task indented beneath a response heading.
     *
     * @param task task to show.
     */
    public void showTask(Task task) {
        output.println(indentBlock + "  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(indentBlock + "Noted. I've removed this task:");
        showTask(task);
        output.println(indentBlock + "Now you have " + taskCount
                + " tasks in the list.");
    }

    /**
     * Shows a recoverable error to the user.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        output.println(indentBlock + "OOPS!!! " + message);
    }

    /** Shows tasks with one-based numbers. */
    private void showNumberedTasks(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.println(indentBlock + (i + 1) + "." + tasks.get(i));
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
