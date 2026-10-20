package utils;

/**
 * Represents the two player colors in a standard game of chess.
 */
public enum Color {
    /** The White player, starting at the bottom of the board (ranks 1 and 2). */
    WHITE("w"),

    /** The Black player, starting at the top of the board (ranks 7 and 8). */
    BLACK("b");

    /** The single-character prefix used when rendering pieces in the console. */
    private final String prefix;

    /**
     * Constructs a Color enum with its corresponding console display prefix.
     *
     * @param prefix the character prefix ("w" for White, "b" for Black)
     */
    Color(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the display prefix for this color ("w" or "b").
     *
     * @return the single-character string prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the opposite player color.
     *
     * @return {@code BLACK} if this color is {@code WHITE}, or {@code WHITE} otherwise
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}