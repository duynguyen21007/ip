import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Saves Milo's task list to a file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes tasks to the specified file.
     *
     * @param filePath path of the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves all tasks using an atomic replacement when the file system supports it.
     *
     * @param tasks task list to save
     * @throws MiloException if the tasks cannot be saved
     */
    public void save(TaskList tasks) throws MiloException {
        Path temporaryFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            List<String> taskLines = tasks.getTasks().stream().map(Task::toString).toList();
            Files.write(temporaryFile, taskLines);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            throw new MiloException("I couldn't save your tasks.");
        } finally {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException | SecurityException exception) {
                // The next save will replace the temporary file.
            }
        }
    }
}
