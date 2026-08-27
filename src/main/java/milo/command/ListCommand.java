package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
