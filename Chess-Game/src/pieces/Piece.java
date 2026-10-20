package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a generic chess piece on the board.
 * Defines shared attributes (color, position, movement history, and type symbol)
 * and provides helper methods for calculating valid movement paths.
 */
public abstract class Piece {

    /** The color of this chess piece (WHITE or BLACK). */
    protected final Color color;

    /** The current coordinate of this chess piece on the board. */
    protected Position position;

    /** Tracks whether this piece has moved at least once during the game (used for Pawns, Kings, and Rooks). */
    protected boolean hasMoved;

    /** The single-character type identifier for this piece (e.g., 'p', 'R', 'N', 'B', 'Q', 'K'). */
    protected final char typeChar;

    /**
     * Constructs a Piece with the given color, starting position, and character identifier.
     *
     * @param color    the team color of the piece ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the initial position of the piece on the chessboard
     * @param typeChar the character representing the piece type ('p', 'R', 'N', 'B', 'Q', 'K')
     */
    public Piece(Color color, Position position, char typeChar) {
        this.color = color;
        this.position = position;
        this.typeChar = typeChar;
        this.hasMoved = false;
    }

    /**
     * Calculates and returns a list of all pseudo-legal target positions this piece can move to
     * from its current position, given the current state of the board.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} objects
     */
    public abstract List<Position> possibleMoves(Board board);

    /**
     * Checks if moving this piece to the target position is valid according to its movement rules.
     *
     * @param target the destination coordinate to test
     * @param board  the current chessboard state
     * @return {@code true} if {@code target} is in {@code possibleMoves(board)}; {@code false} otherwise
     */
    public boolean isValidMove(Position target, Board board) {
        return possibleMoves(board).contains(target);
    }

    /**
     * Helper method for sliding pieces (Rook, Bishop, Queen) to collect all reachable squares
     * along a given set of directional vectors until blocked by the board edge or another piece.
     *
     * @param board      the current chessboard state
     * @param directions a 2D array of {@code {dRow, dCol}} step vectors
     * @return a {@code List} of reachable {@code Position} coordinates along those directions
     */
    protected List<Position> getSlidingMoves(Board board, int[][] directions) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            Position next = position.offset(dRow, dCol);

            while (next.isValid()) {
                Piece occupant = board.getPiece(next);
                if (occupant == null) {
                    moves.add(next);
                } else {
                    if (occupant.getColor() != this.color) {
                        moves.add(next); // Can capture opponent piece
                    }
                    break; // Blocked by a piece; cannot slide further in this direction
                }
                next = next.offset(dRow, dCol);
            }
        }
        return moves;
    }

    /**
     * Gets the color of this piece.
     *
     * @return the {@code Color} of the piece
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the current board position of this piece.
     *
     * @return the current {@code Position}
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Updates the board position of this piece and marks it as having moved.
     *
     * @param position the new {@code Position} of the piece
     */
    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }

    /**
     * Checks whether this piece has moved since the start of the game.
     *
     * @return {@code true} if the piece has moved; {@code false} otherwise
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Sets the movement history flag of this piece.
     *
     * @param hasMoved {@code true} to mark the piece as moved; {@code false} otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Returns the two-character console representation of this piece (e.g., "wp", "bK").
     *
     * @return a two-character string combining the color prefix and piece type character
     */
    public String getSymbol() {
        return color.getPrefix() + typeChar;
    }

    /**
     * Returns the string representation of this piece for console rendering.
     *
     * @return the two-character piece symbol
     */
    @Override
    public String toString() {
        return getSymbol();
    }
}