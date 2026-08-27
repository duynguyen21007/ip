package milo.command;

import milo.exception.MiloException;
import milo.task.TaskList;

/**
 * Represents a command that operates on one task selected by its list number.
 */
abstract class TaskCommand extends Command {
    private final int taskNumber;

    /** Creates a command that targets the given one-based task number. */
    TaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Returns the validated zero-based index of the selected task. */
    protected int getTaskIndex(TaskList tasks) throws MiloException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MiloException("There is no task numbered " + taskNumber + ".");
        }
        return taskNumber - 1;
    }
}
