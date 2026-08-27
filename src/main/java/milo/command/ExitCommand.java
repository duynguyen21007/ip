package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Ends the current Milo session.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
