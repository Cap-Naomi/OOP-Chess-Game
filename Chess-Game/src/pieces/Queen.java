package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.List;

/**
 * Represents a Queen chess piece ("wQ" for White, "bQ" for Black).
 * Queens combine the movement of a Rook and a Bishop, sliding horizontally,
 * vertically, or diagonally any number of unobstructed squares.
 */
public class Queen extends Piece {

    /** Combined orthogonal and diagonal movement vectors (8 directions). */
    private static final int[][] QUEEN_DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    /**
     * Constructs a Queen with the specified color and starting position.
     *
     * @param color    the color of the queen ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public Queen(Color color, Position position) {
        super(color, position, 'Q');
    }

    /**
     * Calculates all reachable orthogonal and diagonal squares for this Queen.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        return getSlidingMoves(board, QUEEN_DIRECTIONS);
    }
}