package gui;

import board.Board;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;
import utils.Color;
import utils.Position;

/**
 * Swing GUI window for the Chess Game that demonstrates board rendering,
 * click-to-move and drag-and-drop movement, piece capturing, and King-capture
 * endgame notification.
 */
public class ChessGui extends JFrame {

    /** Light square color on the chessboard. */
    private static final java.awt.Color LIGHT_SQUARE = new java.awt.Color(240, 217, 181);

    /** Dark square color on the chessboard. */
    private static final java.awt.Color DARK_SQUARE = new java.awt.Color(181, 136, 99);

    /** Highlight color for the currently selected square. */
    private static final java.awt.Color SELECTED_SQUARE = new java.awt.Color(246, 246, 105);

    /** Internal 8x8 piece grid initialized from Board. */
    private final Piece[][] grid;

    /** Visual 8x8 grid of square labels. */
    private final JLabel[][] squares;

    /** Panel holding the 8x8 chessboard. */
    private final JPanel boardPanel;

    /** Status bar displaying capture updates. */
    private final JLabel statusLabel;

    /** Handles piece capturing logic (Section 4). */
    private final CaptureManager captureManager;

    /** Handles King capture winner notification and exit (Section 5). */
    private final EndgameNotifier endgameNotifier;

    /** Currently selected square for click-to-move. */
    private Position selectedPos;

    /** Square where a mouse press began (used for drag-and-drop). */
    private Position pressPos;

    /**
     * Constructs and displays the Chess GUI window.
     */
    public ChessGui() {
        super("OOP Chess Game");
        this.grid = new Piece[8][8];
        this.squares = new JLabel[8][8];
        this.captureManager = new CaptureManager();
        this.endgameNotifier = new EndgameNotifier();

        loadInitialBoard();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(640, 640));

        Font pieceFont = new Font("Segoe UI Symbol", Font.PLAIN, 48);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JLabel square = new JLabel("", SwingConstants.CENTER);
                square.setOpaque(true);
                square.setFont(pieceFont);
                squares[r][c] = square;
                boardPanel.add(square);
            }
        }

        attachMouseControls();

        statusLabel = new JLabel("Click or drag a piece to move. Capture the opponent's King to win.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        refreshBoard();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Loads the standard starting positions from the existing Board class.
     */
    private void loadInitialBoard() {
        Board initialBoard = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid[r][c] = initialBoard.getPiece(new Position(r, c));
            }
        }
    }

    /**
     * Attaches mouse listeners to support both click-to-move and drag-and-drop.
     */
    private void attachMouseControls() {
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressPos = pointToPosition(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Position releasePos = pointToPosition(e.getX(), e.getY());
                if (pressPos == null || releasePos == null) {
                    return;
                }

                if (!pressPos.equals(releasePos)) {
                    // Drag and drop
                    attemptMove(pressPos, releasePos);
                    selectedPos = null;
                } else {
                    // Click to select / click to move
                    handleSquareClick(releasePos);
                }

                pressPos = null;
                refreshBoard();
            }
        });
    }

    /**
     * Handles single-square clicks for click-and-move interaction.
     *
     * @param clickedPos the board coordinate that was clicked
     */
    private void handleSquareClick(Position clickedPos) {
        if (selectedPos == null) {
            if (grid[clickedPos.getRow()][clickedPos.getCol()] != null) {
                selectedPos = clickedPos;
            }
        } else if (selectedPos.equals(clickedPos)) {
            selectedPos = null;
        } else {
            attemptMove(selectedPos, clickedPos);
            selectedPos = null;
        }
    }

    /**
     * Attempts to move a piece, process any capture, refresh the display,
     * and trigger endgame notification if a King was captured.
     *
     * @param from        the starting coordinate
     * @param destination the target coordinate
     */
    private void attemptMove(Position from, Position destination) {
        Piece movingPiece = grid[from.getRow()][from.getCol()];
        Piece targetPiece = grid[destination.getRow()][destination.getCol()];

        if (movingPiece == null) {
            return;
        }

        if (captureManager.isFriendlyOccupied(movingPiece, targetPiece)) {
            statusLabel.setText("Cannot capture your own piece.");
            return;
        }

        Color movingColor = movingPiece.getColor();
        Piece captured = captureManager.executeMoveAndCapture(grid, from, destination);

        // Refresh immediately so any captured piece disappears before the pop-up appears
        refreshBoard();

        if (captured != null) {
            statusLabel.setText(movingColor + " captured " + captured.getColor() + " " + captured.getClass().getSimpleName() + "!");
            endgameNotifier.checkAndNotifyWinner(captured, movingColor, this);
        } else {
            statusLabel.setText("Moved " + movingColor + " " + movingPiece.getClass().getSimpleName()
                    + " from " + from + " to " + destination + ".");
        }
    }

    /**
     * Redraws all 64 squares and piece symbols based on the current grid state.
     */
    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JLabel square = squares[r][c];
                boolean isSelected = selectedPos != null
                        && selectedPos.getRow() == r
                        && selectedPos.getCol() == c;

                if (isSelected) {
                    square.setBackground(SELECTED_SQUARE);
                } else {
                    square.setBackground((r + c) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                }

                Piece piece = grid[r][c];
                square.setText(piece != null ? getUnicodeSymbol(piece) : "");
            }
        }
    }

    /**
     * Converts pixel coordinates inside the board panel to a Position.
     *
     * @param x the x-coordinate in pixels
     * @param y the y-coordinate in pixels
     * @return the corresponding Position, or {@code null} if outside the board
     */
    private Position pointToPosition(int x, int y) {
        int width = boardPanel.getWidth();
        int height = boardPanel.getHeight();
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        int col = (x * 8) / width;
        int row = (y * 8) / height;
        return new Position(row, col);
    }

    /**
     * Maps a Piece instance to its standard Unicode chess symbol.
     *
     * @param piece the piece to convert
     * @return the Unicode string representing the piece
     */
    private String getUnicodeSymbol(Piece piece) {
        boolean white = piece.getColor() == Color.WHITE;
        if (piece instanceof King)   return white ? "\u2654" : "\u265A";
        if (piece instanceof Queen)  return white ? "\u2655" : "\u265B";
        if (piece instanceof Rook)   return white ? "\u2656" : "\u265C";
        if (piece instanceof Bishop) return white ? "\u2657" : "\u265D";
        if (piece instanceof Knight) return white ? "\u2658" : "\u265E";
        if (piece instanceof Pawn)   return white ? "\u2659" : "\u265F";
        return piece.getSymbol();
    }

    /** Handles move capture operations for the GUI. */
    private static final class CaptureManager {
        private boolean isFriendlyOccupied(Piece movingPiece, Piece targetPiece) {
            return movingPiece != null && targetPiece != null
                    && movingPiece.getColor() == targetPiece.getColor();
        }

        private Piece executeMoveAndCapture(Piece[][] grid, Position from, Position destination) {
            Piece captured = grid[destination.getRow()][destination.getCol()];
            Piece movingPiece = grid[from.getRow()][from.getCol()];

            grid[from.getRow()][from.getCol()] = null;
            grid[destination.getRow()][destination.getCol()] = movingPiece;
            return captured;
        }
    }

    /** Shows the winner and closes the application when a King is captured. */
    private static final class EndgameNotifier {
        private void checkAndNotifyWinner(Piece captured, Color winner, ChessGui parent) {
            if (captured instanceof King) {
                javax.swing.JOptionPane.showMessageDialog(parent,
                        winner + " wins! The opponent's King was captured.",
                        "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
    }

    /**
     * Launches the Swing Chess GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGui gui = new ChessGui();
            gui.setVisible(true);
        });
    }
}
