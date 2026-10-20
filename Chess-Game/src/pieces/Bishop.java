package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.List;

/**
 * Represents a Bishop chess piece ("wB" for White, "bB" for Black).
 * Bishops slide diagonally any number of unobstructed squares.
 */
public class Bishop extends Piece {

    /** Diagonal movement vectors (north-west, north-east, south-west, south-east). */
    private static final int[][] BISHOP_DIRECTIONS = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    /**
     * Constructs a Bishop with the specified color and starting position.
     *
     * @param color    the color of the bishop ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public Bishop(Color color, Position position) {
        super(color, position, 'B');
    }

    /**
     * Calculates all reachable diagonal squares for this Bishop.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        return getSlidingMoves(board, BISHOP_DIRECTIONS);
    }
}