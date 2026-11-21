package gui;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import pieces.King;
import pieces.Piece;
import utils.Color;

/**
 * Handles endgame detection and winner notification when a King is captured.
 */
public class EndgameNotifier {

    /**
     * Checks whether the captured piece is a King.
     *
     * @param capturedPiece the piece that was captured (may be null)
     * @return {@code true} if the captured piece is a {@code King}; {@code false} otherwise
     */
    public boolean isKingCaptured(Piece capturedPiece) {
        return capturedPiece instanceof King;
    }

    /**
     * Checks if the captured piece is an opponent's King. If so, displays a
     * pop-up window declaring the capturing player as the winner and terminates
     * the application immediately afterward.
     *
     * @param capturedPiece   the piece that was just captured
     * @param capturingPlayer the color of the player who captured the piece
     * @param parentComponent the parent Swing component to center the pop-up over
     * @return {@code true} if the game ended; {@code false} otherwise
     */
    public boolean checkAndNotifyWinner(Piece capturedPiece, Color capturingPlayer, Component parentComponent) {
        if (!isKingCaptured(capturedPiece)) {
            return false;
        }

        String winnerName = (capturingPlayer == Color.WHITE) ? "White" : "Black";

        JOptionPane.showMessageDialog(
                parentComponent,
                winnerName + " captured the opponent's King!\n" + winnerName + " wins the game!",
                "Game Over - " + winnerName + " Wins",
                JOptionPane.INFORMATION_MESSAGE
        );

        terminateGame(parentComponent);
        return true;
    }

    /**
     * Closes the game window and terminates the JVM cleanly.
     *
     * @param parentComponent a component belonging to the game window
     */
    private void terminateGame(Component parentComponent) {
        if (parentComponent != null) {
            Window window = SwingUtilities.getWindowAncestor(parentComponent);
            if (window != null) {
                window.dispose();
            }
        }
        System.exit(0);
    }
}