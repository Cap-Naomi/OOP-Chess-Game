package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight chess piece ("wN" for White, "bN" for Black).
 * Knights move in an L-shape (two squares in one orthogonal direction and one square perpendicular)
 * and can jump over intervening pieces.
 */
public class Knight extends Piece {

    /** All eight L-shaped jump offsets a Knight can make. */
    private static final int[][] KNIGHT_OFFSETS = {
        {-2, -1}, {-2, 1},
        {-1, -2}, {-1, 2},
        {1, -2},  {1, 2},
        {2, -1},  {2, 1}
    };

    /**
     * Constructs a Knight with the specified color and starting position.
     *
     * @param color    the color of the knight ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public Knight(Color color, Position position) {
        super(color, position, 'N');
    }

    /**
     * Calculates all valid L-shaped jump destinations for this Knight.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] offset : KNIGHT_OFFSETS) {
            Position target = position.offset(offset[0], offset[1]);
            if (target.isValid()) {
                Piece occupant = board.getPiece(target);
                if (occupant == null || occupant.getColor() != this.color) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}