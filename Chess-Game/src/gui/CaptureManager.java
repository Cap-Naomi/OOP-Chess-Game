package gui;

import board.Board;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pieces.Piece;
import utils.Color;
import utils.Position;

/**
 * Manages validated piece movement and capturing logic for the chess GUI.
 * Delegates move legality to {@link Board#movePiece(Position, Position)}
 * and tracks captured pieces for both players.
 */
public class CaptureManager {

    /**
     * Immutable result of an attempted move on the board.
     */
    public static class MoveResult {
        private final boolean moved;
        private final Piece capturedPiece;
        private final String message;

        /**
         * Constructs a MoveResult.
         *
         * @param moved         whether the move was valid and executed
         * @param capturedPiece the captured opponent piece, or {@code null} if none
         * @param message       a status message describing the outcome
         */
        public MoveResult(boolean moved, Piece capturedPiece, String message) {
            this.moved = moved;
            this.capturedPiece = capturedPiece;
            this.message = message;
        }

        /** @return {@code true} if the move succeeded */
        public boolean isMoved() {
            return moved;
        }

        /** @return the captured piece, or {@code null} if no capture occurred */
        public Piece getCapturedPiece() {
            return capturedPiece;
        }

        /** @return the status message for the GUI */
        public String getMessage() {
            return message;
        }
    }

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
     * Checks whether moving a piece onto a destination square targets an opponent's piece.
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
     * Checks whether the destination square is occupied by a friendly piece.
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
     * Validates turn ownership, checks piece movement/capturing rules via {@link Board},
     * executes the move, and records any captured opponent piece.
     *
     * @param board       the active game board
     * @param from        the starting coordinate
     * @param destination the target coordinate
     * @param currentTurn the color of the player whose turn it is
     * @return a {@link MoveResult} indicating whether the move succeeded and any captured piece
     */
    public MoveResult executeMove(Board board, Position from, Position destination, Color currentTurn) {
        if (board == null || from == null || destination == null
                || !from.isValid() || !destination.isValid()
                || from.equals(destination)) {
            return new MoveResult(false, null, "Invalid move selection.");
        }

        Piece movingPiece = board.getPiece(from);
        if (movingPiece == null) {
            return new MoveResult(false, null, "There is no piece on " + from + ".");
        }

        if (movingPiece.getColor() != currentTurn) {
            return new MoveResult(false, null, "It is " + currentTurn + "'s turn.");
        }

        Piece targetPiece = board.getPiece(destination);
        if (isFriendlyOccupied(movingPiece, targetPiece)) {
            return new MoveResult(false, null, "Cannot capture your own piece.");
        }

        boolean moved = board.movePiece(from, destination);
        if (!moved) {
            return new MoveResult(false, null,
                    "Illegal move: " + movingPiece.getClass().getSimpleName() + " cannot move to " + destination + ".");
        }

        if (isOpponentCapture(movingPiece, targetPiece)) {
            recordCapture(currentTurn, targetPiece);
            return new MoveResult(true, targetPiece,
                    currentTurn + " captured " + targetPiece.getColor() + " " + targetPiece.getClass().getSimpleName() + "!");
        }

        return new MoveResult(true, null,
                currentTurn + " moved " + movingPiece.getClass().getSimpleName() + " from " + from + " to " + destination + ".");
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
    /**
     * Clears all recorded captured pieces for both players.
     */
    public void reset() {
        capturedByWhite.clear();
        capturedByBlack.clear();
    }
}