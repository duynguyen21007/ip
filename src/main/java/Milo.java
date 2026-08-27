/**
 * Runs the Milo chatbot and manages its in-memory task list.
 */
public class Milo {
    private static final Storage STORAGE = new Storage("data/duke.txt");
    private static final TaskList TASKS = new TaskList();

    public static void main(String[] args) {
        TASKS.clear();
        try (Ui ui = new Ui()) {
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
                        ui.showTaskList(TASKS);
                        break;
                    case MARK:
                        setTaskDoneStatus(command, true, ui);
                        break;
                    case UNMARK:
                        setTaskDoneStatus(command, false, ui);
                        break;
                    case DELETE:
                        deleteTask(command, ui);
                        break;
                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        Task newTask = Parser.parseTask(command, commandType);
                        TASKS.add(newTask);
                        try {
                            STORAGE.save(TASKS);
                        } catch (MiloException exception) {
                            TASKS.delete(TASKS.size() - 1);
                            throw exception;
                        }
                        ui.showTaskAdded(newTask, TASKS.size());
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
        }
    }

    /**
     * Updates the completion state of the task number supplied in a mark or unmark command.
     *
     * @param command full command entered by the user
     * @param isDone new completion state for the selected task
     * @param ui console UI used to show the result
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private static void setTaskDoneStatus(String command, boolean isDone, Ui ui)
            throws MiloException {
        String action = isDone ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(command, action, TASKS.size());
        Task selectedTask;
        if (isDone) {
            selectedTask = TASKS.mark(taskIndex);
            ui.showTaskMarkedHeader();
        } else {
            selectedTask = TASKS.unmark(taskIndex);
            ui.showTaskUnmarkedHeader();
        }
        try {
            STORAGE.save(TASKS);
        } catch (MiloException exception) {
            if (isDone) {
                TASKS.unmark(taskIndex);
            } else {
                TASKS.mark(taskIndex);
            }
            throw exception;
        }
        ui.showTask(selectedTask);
    }

    /**
     * Removes the selected task; the list shifts later tasks to keep numbering contiguous.
     *
     * @param command full delete command entered by the user
     * @param ui console UI used to show the result
     * @throws MiloException if the task number is missing, invalid, or outside the list
     */
    private static void deleteTask(String command, Ui ui) throws MiloException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", TASKS.size());
        Task deletedTask = TASKS.delete(taskIndex);
        try {
            STORAGE.save(TASKS);
        } catch (MiloException exception) {
            TASKS.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, TASKS.size());
    }

}
