/**
 * Identifies the finite set of commands understood by Milo.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    UNKNOWN("", false);

    private final String keyword;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its command word and argument policy.
     *
     * @param keyword command word entered by the user
     * @param acceptsArguments whether text may follow the command word
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Classifies a full user command without interpreting its arguments.
     *
     * @param command trimmed command entered by the user
     * @return matching command type, or {@link #UNKNOWN} when none matches
     */
    public static CommandType from(String command) {
        for (CommandType commandType : values()) {
            if (commandType != UNKNOWN && commandType.matches(command)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /**
     * Checks whether a command uses this type's keyword and permitted argument form.
     *
     * @param command trimmed command entered by the user
     * @return true if the command has this type
     */
    private boolean matches(String command) {
        if (command.equals(keyword)) {
            return true;
        }
        return acceptsArguments
                && command.startsWith(keyword)
                && command.length() > keyword.length()
                && Character.isWhitespace(command.charAt(keyword.length()));
    }
}
