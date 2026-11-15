package utils;

import java.util.Objects;

/**
 * Represents a zero-indexed coordinate (row, column) on the 8x8 chessboard
 * and provides conversion utilities for standard algebraic notation (e.g., "E2").
 * Row 0 corresponds to Rank 8 (top), and Row 7 corresponds to Rank 1 (bottom).
 * Column 0 corresponds to File A (left), and Column 7 corresponds to File H (right).
 */
public class Position {

    /** The zero-indexed row on the board (0 to 7, where 0 is rank 8). */
    private final int row;

    /** The zero-indexed column on the board (0 to 7, where 0 is file A). */
    private final int col;

    /**
     * Constructs a Position with the specified row and column indices.
     *
     * @param row the row index (0 to 7)
     * @param col the column index (0 to 7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructs a Position from standard two-character chess notation (e.g., "E2").
     *
     * @param notation a two-character string where the first character is the file ('A'-'H')
     *                 and the second character is the rank ('1'-'8')
     * @throws IllegalArgumentException if the notation is null, improperly formatted, or out of bounds
     */
    public static Position fromAlgebraic(String notation) {
        if (notation == null || notation.trim().length() != 2) {
            throw new IllegalArgumentException("Invalid chess notation: " + notation);
        }
        String clean = notation.trim().toUpperCase();
        char fileChar = clean.charAt(0);
        char rankChar = clean.charAt(1);

        int col = fileChar - 'A';
        int row = 8 - (rankChar - '0');

        Position pos = new Position(row, col);
        if (!pos.isValid()) {
            throw new IllegalArgumentException("Position out of board bounds: " + notation);
        }
        return pos;
    }

    /**
     * Gets the row index of this position.
     *
     * @return the row index (0 to 7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of this position.
     *
     * @return the column index (0 to 7)
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks whether this position falls within the standard 8x8 chessboard boundaries.
     *
     * @return {@code true} if both row and col are between 0 and 7 inclusive; {@code false} otherwise
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Returns a new Position offset from the current position by the given row and column deltas.
     *
     * @param dRow the change in row
     * @param dCol the change in column
     * @return a new {@code Position} shifted by {@code (dRow, dCol)}
     */
    public Position offset(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Converts this coordinate into standard algebraic notation (e.g., "E2").
     *
     * @return the two-character algebraic coordinate string
     */
    @Override
    public String toString() {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Compares this position to another object for equality based on row and column.
     *
     * @param obj the object to compare against
     * @return {@code true} if the other object is a {@code Position} with identical row and column
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    /**
     * Generates a hash code for this position based on its row and column.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}