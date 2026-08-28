package milo.parser;

/**
 * Identifies the finite set of commands understood by Milo.
 */
public enum CommandType {
    /** Exit command. */
    BYE,
    /** List command. */
    LIST,
    /** Mark command. */
    MARK,
    /** Unmark command. */
    UNMARK,
    /** Delete command. */
    DELETE,
    /** Todo creation command. */
    TODO,
    /** Deadline creation command. */
    DEADLINE,
    /** Event creation command. */
    EVENT,
    /** Unrecognized command. */
    UNKNOWN
}
