package pieces;

import board.Board;
import utils.Color;
import utils.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King chess piece ("wK" for White, "bK" for Black).
 * The King moves one square in any direction (horizontally, vertically, or diagonally)
 * and may also perform castling if neither the King nor the corresponding Rook has moved.
 */
public class King extends Piece {

    /** All eight single-step directional offsets surrounding the King. */
    private static final int[][] KING_OFFSETS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    /**
     * Constructs a King with the specified color and starting position.
     *
     * @param color    the color of the king ({@code Color.WHITE} or {@code Color.BLACK})
     * @param position the starting board coordinate
     */
    public King(Color color, Position position) {
        super(color, position, 'K');
    }

    /**
     * Calculates all valid single-step moves (and basic castling squares) for this King.
     *
     * @param board the current chessboard state
     * @return a {@code List} of valid destination {@code Position} coordinates
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // Standard 1-square moves in all 8 directions
        for (int[] offset : KING_OFFSETS) {
            Position target = position.offset(offset[0], offset[1]);
            if (target.isValid()) {
                Piece occupant = board.getPiece(target);
                if (occupant == null || occupant.getColor() != this.color) {
                    moves.add(target);
                }
            }
        }

        // Optional Castling path check (kingside and queenside)
        if (!this.hasMoved) {
            int row = position.getRow();
            // Kingside castling (col 4 -> col 6, rook at col 7)
            if (canCastleWithRook(board, new Position(row, 7), new int[]{5, 6})) {
                moves.add(new Position(row, 6));
            }
            // Queenside castling (col 4 -> col 2, rook at col 0)
            if (canCastleWithRook(board, new Position(row, 0), new int[]{1, 2, 3})) {
                moves.add(new Position(row, 2));
            }
        }

        return moves;
    }

    /**
     * Helper method to verify whether the King can castle with the Rook at the specified corner,
     * ensuring the Rook has not moved and all squares between them are empty.
     *
     * @param board       the current chessboard state
     * @param rookPos     the expected starting coordinate of the friendly Rook
     * @param emptyCols   the column indices between the King and Rook that must be unoccupied
     * @return {@code true} if the Rook is unmoved and the intervening path is clear; {@code false} otherwise
     */
    private boolean canCastleWithRook(Board board, Position rookPos, int[] emptyCols) {
        Piece cornerPiece = board.getPiece(rookPos);
        if (!(cornerPiece instanceof Rook) || cornerPiece.getColor() != this.color || cornerPiece.hasMoved()) {
            return false;
        }
        for (int col : emptyCols) {
            if (!board.isEmpty(new Position(position.getRow(), col))) {
                return false;
            }
        }
        return true;
    }
}