package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends TaskCommand {

    /**
     * Creates a command that unmarks the task at the given one-based number.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        int taskIndex = getTaskIndex(tasks);
        Task unmarkedTask = tasks.unmark(taskIndex);
        ui.showTaskUnmarkedHeader();
        try {
            storage.save(tasks);
        } catch (MiloException exception) {
            tasks.mark(taskIndex);
            throw exception;
        }
        ui.showTask(unmarkedTask);
    }
}
