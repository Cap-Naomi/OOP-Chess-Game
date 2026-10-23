package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn chess piece ("wp" for White, "bp" for Black).
 * Pawns move forward one square (or two squares from their initial rank) into empty squares,
 * and capture diagonally forward one square.
 */
public class Pawn extends Piece {

    /**
     * Constructs a Pawn with the specified color and starting position.
     *
     * @param color    the color of the pawn ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public Pawn(Color color, Position position) {
        super(color, position, 'p');
    }

    /**
     * Calculates all valid forward and diagonal-capture moves for this Pawn.
     * White pawns move up the grid (row - 1); Black pawns move down the grid (row + 1).
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (this.color == Color.WHITE) ? -1 : 1;
        int startRow = (this.color == Color.WHITE) ? 6 : 1;

        // 1. Single step forward
        Position oneStep = position.offset(direction, 0);
        if (oneStep.isValid() && board.isEmpty(oneStep)) {
            moves.add(oneStep);

            // 2. Double step forward from starting rank (only if single step is also clear)
            Position twoStep = position.offset(2 * direction, 0);
            if (!this.hasMoved && position.getRow() == startRow && twoStep.isValid() && board.isEmpty(twoStep)) {
                moves.add(twoStep);
            }
        }

        // 3. Diagonal captures (left and right)
        int[] captureCols = {-1, 1};
        for (int dCol : captureCols) {
            Position diag = position.offset(direction, dCol);
            if (diag.isValid() && board.isOpponentPiece(diag, this.color)) {
                moves.add(diag);
            }
        }

        return moves;
    }
}