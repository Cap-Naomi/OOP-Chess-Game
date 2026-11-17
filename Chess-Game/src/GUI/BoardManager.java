package GUI;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import game.Game;
import pieces.Piece;
import utils.Color;
import utils.Position;

/**
 * Displays the chessboard, current turn, move status, and captured pieces.
 * Square clicks are passed to the game model for move validation and execution.
 */
@SuppressWarnings("serial")
public final class BoardManager extends JPanel {

    /** Available board background palettes. */
    private enum BoardTheme {
        ROSE("Rose", new java.awt.Color(255, 194, 211), new java.awt.Color(150, 47, 76)),
        WOOD("Classic Wooden", new java.awt.Color(238, 214, 175), new java.awt.Color(137, 87, 54)),
        GRAY("Modern Gray", new java.awt.Color(225, 228, 232), new java.awt.Color(105, 115, 125));

        private final String label;
        private final java.awt.Color lightSquare;
        private final java.awt.Color darkSquare;

        BoardTheme(String label, java.awt.Color lightSquare, java.awt.Color darkSquare) {
            this.label = label;
            this.lightSquare = lightSquare;
            this.darkSquare = darkSquare;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Available chess-piece glyph, color, and font treatments. */
    private enum PieceStyle {
        CLASSIC("Classic Unicode", Font.SANS_SERIF),
        VIVID("Vivid Unicode", Font.SANS_SERIF),
        GOLD("Gold & Charcoal", Font.SERIF);

        private final String label;
        private final String fontFamily;

        PieceStyle(String label, String fontFamily) {
            this.label = label;
            this.fontFamily = fontFamily;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Available square and piece sizes. */
    private enum BoardSize {
        SMALL("Small", 52, 32),
        MEDIUM("Medium", 68, 42),
        LARGE("Large", 84, 52);

        private final String label;
        private final int squarePixels;
        private final int pieceFontSize;

        BoardSize(String label, int squarePixels, int pieceFontSize) {
            this.label = label;
            this.squarePixels = squarePixels;
            this.pieceFontSize = pieceFontSize;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Border color used to indicate the currently selected square. */
    private static final java.awt.Color HIGHLIGHT = new java.awt.Color(242, 190, 65);

    /** Game model supplying the board state, current turn, and move handling. */
    private final Game game;

    /** Buttons representing the 64 playable squares. */
    private final JButton[][] squares = new JButton[8][8];

    /** Label displaying which player is to move. */
    private final JLabel turnLabel = new JLabel();

    /** Label displaying selection and move feedback. */
    private final JLabel statusLabel = new JLabel("Select one of your pieces.");

    /** Label listing pieces captured by White. */
    private final JLabel whiteCapturesLabel = new JLabel("None");

    /** Label listing pieces captured by Black. */
    private final JLabel blackCapturesLabel = new JLabel("None");

    /** Move entries shown in the game-history panel. */
    private final DefaultListModel<String> moveHistoryModel = new DefaultListModel<>();

    /** List displaying the move history. */
    private final JList<String> moveHistoryList = new JList<>(moveHistoryModel);

    /** Button for reverting the latest move. */
    private final JButton undoButton = new JButton("Undo Last Move");

    /** Position selected as the source of the next move, or {@code null}. */
    private Position selectedPosition;

    private BoardTheme boardTheme = BoardTheme.ROSE;
    private PieceStyle pieceStyle = PieceStyle.CLASSIC;
    private BoardSize boardSize = BoardSize.MEDIUM;

    /**
     * Creates the board interface for the supplied game.
     *
     * @param game the game model displayed and controlled by this panel
     * @throws NullPointerException if {@code game} is {@code null}
     */
    public BoardManager(Game game) {
        this.game = Objects.requireNonNull(game, "game");
        setLayout(new BorderLayout(16, 16));
        setBackground(new java.awt.Color(245, 246, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createBoard(), BorderLayout.CENTER);
        add(createHistoryPanel(), BorderLayout.EAST);
        refreshBoard();
    }

    /**
     * Creates the header containing the turn and status labels.
     *
     * @return the configured header panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        turnLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JButton settingsButton = new JButton("Appearance...");
        settingsButton.addActionListener(event -> showSettingsDialog());
        header.add(turnLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.CENTER);
        header.add(settingsButton, BorderLayout.EAST);
        return header;
    }

    /**
     * Creates the chessboard grid and its rank and file labels.
     *
     * @return the configured board panel
     */
    private JPanel createBoard() {
        JPanel board = new JPanel(new BorderLayout(4, 4));
        board.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final Position position = new Position(row, col);
                JButton square = new JButton();
                square.setPreferredSize(new Dimension(boardSize.squarePixels, boardSize.squarePixels));
                square.setFont(new Font(pieceStyle.fontFamily, Font.PLAIN, boardSize.pieceFontSize));
                square.setFocusPainted(false);
                square.setMargin(new java.awt.Insets(0, 0, 0, 0));
                square.setOpaque(true);
                square.addActionListener(event -> handleSquareClick(position));
                squares[row][col] = square;
                grid.add(square);
            }
        }

        JPanel ranks = new JPanel(new GridLayout(8, 1));
        JPanel files = new JPanel(new GridLayout(1, 8));
        ranks.setOpaque(false);
        files.setOpaque(false);
        for (int i = 0; i < 8; i++) {
            ranks.add(createCoordinateLabel(Integer.toString(8 - i)));
            files.add(createCoordinateLabel(Character.toString((char) ('A' + i))));
        }

        board.add(ranks, BorderLayout.WEST);
        board.add(grid, BorderLayout.CENTER);
        board.add(files, BorderLayout.SOUTH);
        return board;
    }

    /**
     * Creates a centered coordinate label for a board rank or file.
     *
     * @param text the coordinate to display
     * @return the configured coordinate label
     */
    private JLabel createCoordinateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setPreferredSize(new Dimension(24, 24));
        return label;
    }

    /**
     * Creates the panel showing the move history, captured pieces, and undo control.
     *
     * @return the configured game-history panel
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(230, 0));

        JPanel historySection = new JPanel(new BorderLayout(0, 8));
        historySection.setBackground(java.awt.Color.WHITE);
        historySection.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new java.awt.Color(220, 224, 230)),
                new EmptyBorder(12, 12, 12, 12)));
        JLabel historyTitle = new JLabel("Move History");
        historyTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        moveHistoryList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        moveHistoryList.setVisibleRowCount(10);
        historySection.add(historyTitle, BorderLayout.NORTH);
        historySection.add(new JScrollPane(moveHistoryList), BorderLayout.CENTER);
        panel.add(historySection, BorderLayout.CENTER);

        JPanel captures = new JPanel(new GridLayout(2, 1, 0, 12));
        captures.setOpaque(false);
        captures.add(createCaptureSection("Captured by White", whiteCapturesLabel));
        captures.add(createCaptureSection("Captured by Black", blackCapturesLabel));

        JPanel lowerPanel = new JPanel(new BorderLayout(0, 12));
        lowerPanel.setOpaque(false);
        undoButton.addActionListener(event -> undoLastMove());
        lowerPanel.add(captures, BorderLayout.CENTER);
        lowerPanel.add(undoButton, BorderLayout.SOUTH);
        panel.add(lowerPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Opens the appearance settings dialog and applies selected options on request.
     */
    private void showSettingsDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Appearance Settings", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JComboBox<BoardTheme> themeSelector = new JComboBox<>(BoardTheme.values());
        JComboBox<PieceStyle> pieceStyleSelector = new JComboBox<>(PieceStyle.values());
        JComboBox<BoardSize> sizeSelector = new JComboBox<>(BoardSize.values());
        themeSelector.setSelectedItem(boardTheme);
        pieceStyleSelector.setSelectedItem(pieceStyle);
        sizeSelector.setSelectedItem(boardSize);

        JPanel options = new JPanel(new GridLayout(0, 2, 12, 12));
        options.setBorder(new EmptyBorder(16, 16, 8, 16));
        options.add(new JLabel("Board background"));
        options.add(themeSelector);
        options.add(new JLabel("Piece appearance"));
        options.add(pieceStyleSelector);
        options.add(new JLabel("Board size"));
        options.add(sizeSelector);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> {
            applyAppearance(
                    (BoardTheme) themeSelector.getSelectedItem(),
                    (PieceStyle) pieceStyleSelector.getSelectedItem(),
                    (BoardSize) sizeSelector.getSelectedItem());
            dialog.dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(new EmptyBorder(0, 8, 8, 16));
        buttons.add(cancelButton);
        buttons.add(applyButton);

        dialog.add(options, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(applyButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Applies appearance options to the board and resizes its containing window.
     *
     * @param theme the board-square color palette
     * @param style the chess-piece appearance
     * @param size the board and piece size
     */
    private void applyAppearance(BoardTheme theme, PieceStyle style, BoardSize size) {
        boardTheme = Objects.requireNonNull(theme, "theme");
        pieceStyle = Objects.requireNonNull(style, "style");
        boardSize = Objects.requireNonNull(size, "size");

        for (JButton[] row : squares) {
            for (JButton square : row) {
                square.setPreferredSize(new Dimension(size.squarePixels, size.squarePixels));
                square.setFont(new Font(pieceStyle.fontFamily, Font.PLAIN, size.pieceFontSize));
            }
        }
        whiteCapturesLabel.setFont(new Font(pieceStyle.fontFamily, Font.PLAIN, 24));
        blackCapturesLabel.setFont(new Font(pieceStyle.fontFamily, Font.PLAIN, 24));

        refreshBoard();
        revalidate();
        repaint();

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.pack();
            window.setLocationRelativeTo(null);
        }
    }

    /**
     * Creates one section of the capture panel.
     *
     * @param title the section title
     * @param piecesLabel the label that displays the captured pieces
     * @return the configured capture section
     */
    private JPanel createCaptureSection(String title, JLabel piecesLabel) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(java.awt.Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new java.awt.Color(220, 224, 230)),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        piecesLabel.setFont(new Font(pieceStyle.fontFamily, Font.PLAIN, 24));
        section.add(titleLabel, BorderLayout.NORTH);
        section.add(piecesLabel, BorderLayout.CENTER);
        return section;
    }

    /**
     * Processes a click on a board square as a selection or move destination.
     *
     * @param clickedPosition the position represented by the clicked square
     */
    private void handleSquareClick(Position clickedPosition) {
        if (game.isGameOver()) {
            return;
        }

        Piece clickedPiece = game.getBoard().getPiece(clickedPosition);
        if (selectedPosition == null) {
            selectPiece(clickedPosition, clickedPiece);
            refreshBoard();
            return;
        }

        if (selectedPosition.equals(clickedPosition)) {
            selectedPosition = null;
            statusLabel.setText("Selection cleared.");
            refreshBoard();
            return;
        }

        if (clickedPiece != null && clickedPiece.getColor() == game.getCurrentTurn()) {
            selectPiece(clickedPosition, clickedPiece);
            refreshBoard();
            return;
        }

        Position from = selectedPosition;
        boolean capture = clickedPiece != null;
        if (game.makeMove(from, clickedPosition)) {
            selectedPosition = null;
            if (game.isGameOver()) {
                statusLabel.setText(game.getWinner() + " wins!");
            } else {
                statusLabel.setText(capture
                        ? "Piece captured on " + clickedPosition + "."
                        : "Moved to " + clickedPosition + ".");
            }
        } else {
            statusLabel.setText("That move is not valid. Choose another destination.");
        }
        refreshBoard();
        if (game.isGameOver()) {
            showWinnerDialog();
        }
    }

    /**
     * Declares the winner and closes the game window after the dialog is dismissed.
     */
    private void showWinnerDialog() {
        JOptionPane.showMessageDialog(this, game.getWinner() + " wins!", "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    /**
     * Reverts the latest move and refreshes the board, history, and capture lists.
     */
    private void undoLastMove() {
        if (!game.undoLastMove()) {
            return;
        }

        selectedPosition = null;
        statusLabel.setText("Last move undone.");
        refreshBoard();
    }

    /**
     * Selects a current-player piece or displays why the clicked square cannot
     * be selected.
     *
     * @param position the position that was clicked
     * @param piece the piece at that position, or {@code null} if it is empty
     */
    private void selectPiece(Position position, Piece piece) {
        if (piece == null) {
            statusLabel.setText("There is no piece on " + position + ".");
        } else if (piece.getColor() != game.getCurrentTurn()) {
            statusLabel.setText("Select a piece belonging to " + game.getCurrentTurn() + ".");
        } else {
            selectedPosition = position;
            statusLabel.setText("Selected " + position + ". Choose a destination.");
        }
    }

    /**
     * Refreshes the board squares, turn indicator, and captured-piece labels
     * from the current game state.
     */
    private void refreshBoard() {
        turnLabel.setText(game.isGameOver()
                ? game.getWinner() + " wins"
                : game.getCurrentTurn() + " to move");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position position = new Position(row, col);
                Piece piece = game.getBoard().getPiece(position);
                JButton square = squares[row][col];
                boolean darkSquare = (row + col) % 2 != 0;
                square.setBackground(darkSquare ? boardTheme.darkSquare : boardTheme.lightSquare);
                square.setText(piece == null ? "" : pieceSymbol(piece));
                square.setForeground(piece == null
                        ? java.awt.Color.BLACK
                        : pieceColor(piece, darkSquare));
                square.setBorder(selectedPosition != null && selectedPosition.equals(position)
                        ? new LineBorder(HIGHLIGHT, 3)
                        : new LineBorder(new java.awt.Color(90, 100, 80), 1));
                square.setToolTipText(position.toString());
            }
        }
        whiteCapturesLabel.setText(capturedSymbols(game.getBoard().getCapturedByWhite()));
        blackCapturesLabel.setText(capturedSymbols(game.getBoard().getCapturedByBlack()));
        moveHistoryModel.clear();
        List<Game.MoveRecord> moveHistory = game.getMoveHistory();
        for (int index = 0; index < moveHistory.size(); index++) {
            Game.MoveRecord move = moveHistory.get(index);
            String entry = (index + 1) + ". " + move.getPlayerColor() + " "
                    + pieceName(move.getPieceSymbol()) + " "
                    + move.getFrom() + " -> " + move.getTo();
            if (move.getCapturedPieceSymbol() != null) {
                entry += " (captured " + pieceName(move.getCapturedPieceSymbol()) + ")";
            }
            moveHistoryModel.addElement(entry);
        }
        if (!moveHistory.isEmpty()) {
            moveHistoryList.ensureIndexIsVisible(moveHistory.size() - 1);
        }
        undoButton.setEnabled(!moveHistory.isEmpty());
    }

    /**
     * Converts a piece symbol into a readable color and piece name.
     *
     * @param symbol the two-character piece symbol
     * @return the color and name of the piece
     */
    private String pieceName(String symbol) {
        String colorName = symbol.charAt(0) == Color.WHITE.getPrefix().charAt(0)
                ? "White"
                : "Black";
        String name;
        switch (Character.toUpperCase(symbol.charAt(1))) {
            case 'K':
                name = "King";
                break;
            case 'Q':
                name = "Queen";
                break;
            case 'R':
                name = "Rook";
                break;
            case 'B':
                name = "Bishop";
                break;
            case 'N':
                name = "Knight";
                break;
            case 'P':
                name = "Pawn";
                break;
            default:
                name = symbol;
                break;
        }
        return colorName + " " + name;
    }

    /**
     * Formats captured pieces as a space-separated sequence of chess symbols.
     *
     * @param capturedPieces the pieces captured by one player
     * @return the symbols to display, or {@code "None"} if the list is empty
     */
    private String capturedSymbols(List<Piece> capturedPieces) {
        if (capturedPieces.isEmpty()) {
            return "None";
        }

        StringBuilder symbols = new StringBuilder(); 
        for (Piece piece : capturedPieces) {
            if (symbols.length() > 0) {
                symbols.append(' ');
            }
            symbols.append(pieceSymbol(piece));
        }
        return symbols.toString();
    }

    /**
     * Gets the Unicode chess symbol for a piece.
     *
     * @param piece the piece to represent
     * @return the symbol matching the piece's color and type
     */
    private String pieceSymbol(Piece piece) {
        char pieceType = Character.toUpperCase(piece.getSymbol().charAt(1));
        switch (pieceType) {
            case 'K':
                return "\u265A";
            case 'Q':
                return "\u265B";
            case 'R':
                return "\u265C";
            case 'B':
                return "\u265D";
            case 'N':
                return "\u265E";
            case 'P':
                return "\u265F";
            default:
                return piece.getSymbol();
        }
    }

    /**
     * Chooses a piece foreground color with contrast against its square.
     *
     * @param piece the piece whose color determines the foreground
     * @param darkSquare whether the square has a dark background
     * @return the foreground color for the piece
     */
    private java.awt.Color pieceColor(Piece piece, boolean darkSquare) {
        if (pieceStyle == PieceStyle.VIVID) {
            if (piece.getColor() == Color.WHITE) {
                return darkSquare
                        ? new java.awt.Color(151, 255, 231)
                        : new java.awt.Color(0, 102, 91);
            }
            return darkSquare
                    ? new java.awt.Color(255, 174, 215)
                    : new java.awt.Color(120, 35, 90);
        }

        if (pieceStyle == PieceStyle.GOLD) {
            if (piece.getColor() == Color.WHITE) {
                return darkSquare
                    ? new java.awt.Color(255, 224, 138)
                    : new java.awt.Color(145, 83, 0);
            }
            return darkSquare
                    ? new java.awt.Color(196, 164, 132)
                    : new java.awt.Color(76, 43, 30);
        }

        if (piece.getColor() == Color.WHITE) {
            return new java.awt.Color(255, 255, 245);
        }
        return new java.awt.Color(38, 42, 48);
    }
}
