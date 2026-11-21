package gui;

import pieces.Piece;
import utils.Color;
import utils.Position;

/**
 * Immutable record of a single chess move, storing all data needed
 * for move history display and undo functionality.
 */
public class MoveRecord {

    /** Sequential 1-based move number. */
    private final int moveNumber;

    /** Color of the player who made the move. */
    private final Color playerColor;

    /** The piece that was moved. */
    private final Piece movedPiece;

    /** Starting coordinate of the move. */
    private final Position from;

    /** Destination coordinate of the move. */
    private final Position to;

    /** Opponent piece captured on this move, or {@code null} if none. */
    private final Piece capturedPiece;

    /**
     * Constructs a new MoveRecord.
     *
     * @param moveNumber    the 1-based sequence number of the move
     * @param playerColor   the color of the player who moved
     * @param movedPiece    the piece that moved
     * @param from          the starting square
     * @param to            the ending square
     * @param capturedPiece the captured opponent piece, or {@code null}
     */
    public MoveRecord(int moveNumber, Color playerColor, Piece movedPiece,
                      Position from, Position to, Piece capturedPiece) {
        this.moveNumber = moveNumber;
        this.playerColor = playerColor;
        this.movedPiece = movedPiece;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    /** @return the 1-based move number */
    public int getMoveNumber() {
        return moveNumber;
    }

    /** @return the color of the player who made the move */
    public Color getPlayerColor() {
        return playerColor;
    }

    /** @return the piece that moved */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    /** @return the starting position */
    public Position getFrom() {
        return from;
    }

    /** @return the destination position */
    public Position getTo() {
        return to;
    }

    /** @return the captured piece, or {@code null} if no capture occurred */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Formats the move for display in the Move History panel.
     *
     * @return a readable string showing move number, piece, start, end, and capture
     */
    @Override
    public String toString() {
        String base = String.format("%d. %s %s: %s -> %s",
                moveNumber,
                playerColor,
                movedPiece.getClass().getSimpleName(),
                from,
                to);
        if (capturedPiece != null) {
            return base + " (x " + capturedPiece.getClass().getSimpleName() + ")";
        }
        return base;
    }
}