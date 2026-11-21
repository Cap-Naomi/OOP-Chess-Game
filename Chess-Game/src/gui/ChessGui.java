package gui;

import board.Board;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
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
 * Swing GUI window for the Chess Game with a Game menu bar (New, Save, Load),
 * turn enforcement, validated piece movement and capturing, and King-capture
 * endgame notification.
 */
public class ChessGui extends JFrame {

    /** Light square color on the chessboard. */
    private static final java.awt.Color LIGHT_SQUARE = new java.awt.Color(240, 217, 181);

    /** Dark square color on the chessboard. */
    private static final java.awt.Color DARK_SQUARE = new java.awt.Color(181, 136, 99);

    /** Highlight color for the currently selected square. */
    private static final java.awt.Color SELECTED_SQUARE = new java.awt.Color(246, 246, 105);

    /** Active chess board storing piece positions and validating moves. */
    private final Board board;

    /** Visual 8x8 grid of square labels. */
    private final JLabel[][] squares;

    /** Panel holding the 8x8 chessboard. */
    private final JPanel boardPanel;

    /** Status bar displaying turn and capture updates. */
    private final JLabel statusLabel;

    /** Handles validated moves and piece capturing. */
    private CaptureManager captureManager;

    /** Handles King capture winner notification and exit. */
    private final EndgameNotifier endgameNotifier;

    /** Handles saving and loading game state files. */
    private final GameStateManager gameStateManager;

    /** Color of the player whose turn it currently is. */
    private Color currentTurn;

    /** Currently selected square for click-to-move. */
    private Position selectedPos;

    /** Square where a mouse press began (used for drag-and-drop). */
    private Position pressPos;

    /**
     * Constructs and displays the Chess GUI window.
     */
    public ChessGui() {
        super("OOP Chess Game");
        this.board = new Board();
        this.squares = new JLabel[8][8];
        this.captureManager = new CaptureManager();
        this.endgameNotifier = new EndgameNotifier();
        this.gameStateManager = new GameStateManager();
        this.currentTurn = Color.WHITE;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

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

        statusLabel = new JLabel("Turn: WHITE — Click or drag a piece to move.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        refreshBoard();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Builds the top menu bar containing New Game, Save Game, and Load Game items.
     *
     * @return the configured {@link JMenuBar}
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutMask));
        newGameItem.addActionListener(e -> startNewGame());

        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask));
        saveGameItem.addActionListener(e -> saveGame());

        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutMask));
        loadGameItem.addActionListener(e -> loadGame());

        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        return menuBar;
    }

    /**
     * Resets the board, turn state, selection, and capture tracking for a fresh game.
     */
    private void startNewGame() {
        board.initializeBoard();
        captureManager = new CaptureManager();
        currentTurn = Color.WHITE;
        selectedPos = null;
        pressPos = null;
        refreshBoard();
        statusLabel.setText("New game started. Turn: WHITE — Click or drag a piece to move.");
    }

    /**
     * Prompts the user for a file location and saves the current game state.
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Chess Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Save Files (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try {
                gameStateManager.saveGame(board, currentTurn, file);
                statusLabel.setText("Game saved to " + file.getName() + ". Turn: " + currentTurn);
                JOptionPane.showMessageDialog(
                        this,
                        "Game saved successfully to:\n" + file.getName(),
                        "Save Game",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save game:\n" + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Prompts the user to select a saved game file and restores the board and turn state.
     */
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Chess Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Save Files (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                currentTurn = gameStateManager.loadGame(board, file);
                captureManager = new CaptureManager();
                selectedPos = null;
                pressPos = null;
                refreshBoard();
                statusLabel.setText("Game loaded from " + file.getName() + ". Turn: " + currentTurn);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to load game:\n" + ex.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE
                );
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
            Piece clickedPiece = board.getPiece(clickedPos);
            if (clickedPiece == null) {
                return;
            }
            if (clickedPiece.getColor() != currentTurn) {
                statusLabel.setText("It is " + currentTurn + "'s turn.");
                return;
            }
            selectedPos = clickedPos;
        } else if (selectedPos.equals(clickedPos)) {
            selectedPos = null;
        } else {
            attemptMove(selectedPos, clickedPos);
            selectedPos = null;
        }
    }

    /**
     * Attempts a validated move, switches turns on success, refreshes the board,
     * and triggers endgame notification if a King was captured.
     *
     * @param from        the starting coordinate
     * @param destination the target coordinate
     */
    private void attemptMove(Position from, Position destination) {
        Color movingColor = currentTurn;
        CaptureManager.MoveResult result = captureManager.executeMove(board, from, destination, currentTurn);

        if (!result.isMoved()) {
            statusLabel.setText(result.getMessage() + " (Turn: " + currentTurn + ")");
            return;
        }

        // Switch turns after a valid move
        currentTurn = currentTurn.opposite();

        // Refresh immediately so any captured piece disappears before the pop-up appears
        refreshBoard();

        if (result.getCapturedPiece() != null) {
            statusLabel.setText(result.getMessage() + " Next turn: " + currentTurn);
            endgameNotifier.checkAndNotifyWinner(result.getCapturedPiece(), movingColor, this);
        } else {
            statusLabel.setText(result.getMessage() + " Next turn: " + currentTurn);
        }
    }

    /**
     * Redraws all 64 squares and piece symbols based on the current Board state.
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

                Piece piece = board.getPiece(new Position(r, c));
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