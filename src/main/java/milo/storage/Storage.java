package milo.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import milo.exception.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.Todo;

/**
 * Loads and saves Milo's task list in a line-based file format.
 */
public class Storage {
    private static final String STORED_DATE_PATTERN = "[A-Z][a-z]{2} \\d{2} \\d{4}";
    private static final DateTimeFormatter STORED_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final Pattern TODO_LINE = Pattern.compile("^\\[T]\\[([ X])] (.+)$");
    private static final Pattern DEADLINE_LINE = Pattern.compile(
            "^\\[D]\\[([ X])] (.+) \\(by: (" + STORED_DATE_PATTERN + ")\\)$");
    private static final Pattern EVENT_LINE = Pattern.compile(
            "^\\[E]\\[([ X])] (.+) \\(from: (" + STORED_DATE_PATTERN
                    + ") to: (" + STORED_DATE_PATTERN + ")\\)$");

    private final Path filePath;

    /**
     * Creates storage that reads and writes tasks at the specified file.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the data file, or returns an empty list when it does not exist.
     *
     * @return tasks stored in the file.
     * @throws MiloException if the task file cannot be read or contains malformed data.
     */
    public List<Task> load() throws MiloException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            List<Task> tasks = new ArrayList<>();
            for (String taskLine : Files.readAllLines(filePath)) {
                tasks.add(parseStoredTask(taskLine));
            }
            return tasks;
        } catch (IOException | SecurityException | IllegalArgumentException exception) {
            throw new MiloException("I couldn't load your tasks.");
        }
    }

    /**
     * Saves all tasks using an atomic replacement when the file system supports it.
     *
     * @param tasks task list to save.
     * @throws MiloException if the tasks cannot be saved.
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

    /** Converts a stored task line back into its concrete task type and state. */
    private Task parseStoredTask(String taskLine) {
        Matcher todoMatcher = TODO_LINE.matcher(taskLine);
        if (todoMatcher.matches()) {
            return restoreDoneStatus(new Todo(todoMatcher.group(2)), todoMatcher.group(1));
        }

        Matcher deadlineMatcher = DEADLINE_LINE.matcher(taskLine);
        if (deadlineMatcher.matches()) {
            Task deadline = new Deadline(deadlineMatcher.group(2),
                    LocalDate.parse(deadlineMatcher.group(3), STORED_DATE_FORMAT));
            return restoreDoneStatus(deadline, deadlineMatcher.group(1));
        }

        Matcher eventMatcher = EVENT_LINE.matcher(taskLine);
        if (eventMatcher.matches()) {
            Task event = new Event(eventMatcher.group(2),
                    LocalDate.parse(eventMatcher.group(3), STORED_DATE_FORMAT),
                    LocalDate.parse(eventMatcher.group(4), STORED_DATE_FORMAT));
            return restoreDoneStatus(event, eventMatcher.group(1));
        }

        throw new IllegalArgumentException("Malformed task line");
    }

    /** Restores the persisted completion state and returns the task. */
    private Task restoreDoneStatus(Task task, String statusIcon) {
        if (statusIcon.equals("X")) {
            task.markAsDone();
        }
        return task;
    }
}
