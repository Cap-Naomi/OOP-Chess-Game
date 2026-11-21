package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pieces.Piece;
import utils.Color;
import utils.Position;

/**
 * Manages piece capturing logic for the chess GUI.
 * Handles removing captured opponent pieces from the board grid
 * and tracking captured pieces for both players.
 */
public class CaptureManager {

    /** Pieces captured by the White player. */
    private final List<Piece> capturedByWhite;

    /** Pieces captured by the Black player. */
    private final List<Piece> capturedByBlack;

    /**
     * Constructs a new CaptureManager with empty capture lists.
     */
    public CaptureManager() {
        this.capturedByWhite = new ArrayList<>();
        this.capturedByBlack = new ArrayList<>();
    }

    /**
     * Checks whether moving a piece onto a destination square results in
     * capturing an opponent's piece.
     *
     * @param movingPiece the piece being moved
     * @param targetPiece the piece currently occupying the destination square (may be null)
     * @return {@code true} if the target square holds an opponent's piece; {@code false} otherwise
     */
    public boolean isOpponentCapture(Piece movingPiece, Piece targetPiece) {
        return movingPiece != null
                && targetPiece != null
                && movingPiece.getColor() != targetPiece.getColor();
    }

    /**
     * Checks whether the destination square is occupied by a friendly piece
     * of the same color as the moving piece.
     *
     * @param movingPiece the piece being moved
     * @param targetPiece the piece currently occupying the destination square (may be null)
     * @return {@code true} if both pieces belong to the same player; {@code false} otherwise
     */
    public boolean isFriendlyOccupied(Piece movingPiece, Piece targetPiece) {
        return movingPiece != null
                && targetPiece != null
                && movingPiece.getColor() == targetPiece.getColor();
    }

    /**
     * Executes a move on the 8x8 piece grid and captures any opponent piece
     * occupying the destination square so it is removed from the board.
     *
     * @param grid        the 8x8 array representing the current board state
     * @param from        the starting position of the moving piece
     * @param destination the target position to move the piece to
     * @return the captured opponent {@code Piece} if a capture occurred, or {@code null} otherwise
     */
    public Piece executeMoveAndCapture(Piece[][] grid, Position from, Position destination) {
        if (grid == null || from == null || destination == null
                || !from.isValid() || !destination.isValid()
                || from.equals(destination)) {
            return null;
        }

        Piece movingPiece = grid[from.getRow()][from.getCol()];
        Piece targetPiece = grid[destination.getRow()][destination.getCol()];

        if (movingPiece == null || isFriendlyOccupied(movingPiece, targetPiece)) {
            return null;
        }

        Piece capturedPiece = null;
        if (isOpponentCapture(movingPiece, targetPiece)) {
            capturedPiece = targetPiece;
            recordCapture(movingPiece.getColor(), capturedPiece);
        }

        // Remove piece from its old square and overwrite the destination square
        // so any captured piece disappears from the board.
        grid[from.getRow()][from.getCol()] = null;
        movingPiece.setPosition(destination);
        grid[destination.getRow()][destination.getCol()] = movingPiece;

        return capturedPiece;
    }

    /**
     * Records a captured piece in the capturing player's list.
     *
     * @param capturerColor the color of the player who made the capture
     * @param capturedPiece the opponent's piece that was captured
     */
    private void recordCapture(Color capturerColor, Piece capturedPiece) {
        if (capturerColor == Color.WHITE) {
            capturedByWhite.add(capturedPiece);
        } else {
            capturedByBlack.add(capturedPiece);
        }
    }

    /**
     * Returns an unmodifiable list of pieces captured by White.
     *
     * @return list of pieces captured by White
     */
    public List<Piece> getCapturedByWhite() {
        return Collections.unmodifiableList(capturedByWhite);
    }

    /**
     * Returns an unmodifiable list of pieces captured by Black.
     *
     * @return list of pieces captured by Black
     */
    public List<Piece> getCapturedByBlack() {
        return Collections.unmodifiableList(capturedByBlack);
    }
}