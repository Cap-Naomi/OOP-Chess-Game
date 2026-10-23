package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.List;

/**
 * Represents a Rook chess piece ("wR" for White, "bR" for Black).
 * Rooks slide horizontally and vertically any number of unobstructed squares.
 */
public class Rook extends Piece {

    /** Orthogonal movement vectors (up, down, left, right). */
    private static final int[][] ROOK_DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    /**
     * Constructs a Rook with the specified color and starting position.
     *
     * @param color    the color of the rook ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public Rook(Color color, Position position) {
        super(color, position, 'R');
    }

    /**
     * Calculates all reachable horizontal and vertical squares for this Rook.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        return getSlidingMoves(board, ROOK_DIRECTIONS);
    }
}