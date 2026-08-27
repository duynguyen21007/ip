package milo;

import milo.command.Command;
import milo.exception.MiloException;
import milo.parser.Parser;
import milo.storage.Storage;
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
            boolean isExit = false;
            while (!isExit && ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                ui.showLine();
                try {
                    Command command = Parser.parse(fullCommand);
                    command.execute(tasks, ui, storage);
                    isExit = command.isExit();
                } catch (MiloException exception) {
                    ui.showError(exception.getMessage());
                } finally {
                    ui.showLine();
                }
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

}
