package milo;

/**
 * Provides the legacy Duke banner entry point retained from the starter project.
 */
public class Duke {
    /** Creates the legacy Duke entry point. */
    public Duke() {
    }

    /**
     * Prints the Duke banner.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(banner);
    }
}
