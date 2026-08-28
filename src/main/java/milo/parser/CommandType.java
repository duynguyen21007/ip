package milo.parser;

/**
 * Identifies the finite set of commands understood by Milo.
 */
public enum CommandType {
    BYE,
    LIST,
    FIND,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    UNKNOWN
}
