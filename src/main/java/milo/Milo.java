package milo;

import milo.exception.MiloException;
import milo.parser.CommandType;
import milo.parser.Parser;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Coordinates Milo's storage, task list, parser, and console UI.
 */
public class Milo {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Milo and loads its saved tasks.
     *
     * @param filePath path of the task data file
     */
    public Milo(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MiloException exception) {
            ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
    }

    /** Starts Milo's command-reading loop. */
    public void run() {
        try {
            ui.showWelcome();
            while (ui.hasNextCommand()) {
                String command = ui.readCommand();
                CommandType commandType = Parser.parseCommandType(command);

                ui.showLine();

                try {
                    switch (commandType) {
                    case BYE:
                        ui.showGoodbye();
                        ui.showLine();
                        return;
                    case LIST:
                        ui.showTaskList(tasks);
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
                        tasks.add(newTask);
                        try {
                            storage.save(tasks);
                        } catch (MiloException exception) {
                            tasks.delete(tasks.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(newTask, tasks.size());
                        break;
                    case UNKNOWN:
                        throw new MiloException("I don't recognize that command :-(");
                    default:
                        throw new IllegalStateException("Unhandled command type: " + commandType);
                    }
                } catch (MiloException exception) {
                    ui.showError(exception.getMessage());
                }

                ui.showLine();
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Starts Milo using the default task data file.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Milo("data/duke.txt").run();
    }

    /**
     * Updates the completion state of the task number supplied in a mark or unmark command.
     *
     * @param command full command entered by the user
     * @param isDone new completion state for the selected task
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private void setTaskDoneStatus(String command, boolean isDone)
            throws MiloException {
        String action = isDone ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(command, action, tasks.size());
        Task selectedTask;
        if (isDone) {
            selectedTask = tasks.mark(taskIndex);
            ui.showTaskMarkedHeader();
        } else {
            selectedTask = tasks.unmark(taskIndex);
            ui.showTaskUnmarkedHeader();
        }
        try {
            storage.save(tasks);
        } catch (MiloException exception) {
            if (isDone) {
                tasks.unmark(taskIndex);
            } else {
                tasks.mark(taskIndex);
            }
            throw exception;
        }
        ui.showTask(selectedTask);
    }

    /**
     * Removes the selected task; the list shifts later tasks to keep numbering contiguous.
     *
     * @param command full delete command entered by the user
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private void deleteTask(String command) throws MiloException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (MiloException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

}
