package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

    /** Background color for light squares. */
    private static final java.awt.Color LIGHT_SQUARE = new java.awt.Color(255, 194, 211);

    /** Background color for dark squares. */
    private static final java.awt.Color DARK_SQUARE = new java.awt.Color(150, 47, 76);

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

    /** Position selected as the source of the next move, or {@code null}. */
    private Position selectedPosition;

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
        add(createCapturePanel(), BorderLayout.EAST);
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
        header.add(turnLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.CENTER);
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
                square.setPreferredSize(new Dimension(68, 68));
                square.setFont(new Font("SansSerif", Font.PLAIN, 42));
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
     * Creates the panel showing the pieces captured by each player.
     *
     * @return the configured capture panel
     */
    private JPanel createCapturePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 16));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(190, 0));
        panel.add(createCaptureSection("Captured by White", whiteCapturesLabel));
        panel.add(createCaptureSection("Captured by Black", blackCapturesLabel));
        return panel;
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
        piecesLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
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
            statusLabel.setText(capture
                    ? "Piece captured on " + clickedPosition + "."
                    : "Moved to " + clickedPosition + ".");
        } else {
            statusLabel.setText("That move is not valid. Choose another destination.");
        }
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
        turnLabel.setText(game.getCurrentTurn() + " to move");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position position = new Position(row, col);
                Piece piece = game.getBoard().getPiece(position);
                JButton square = squares[row][col];
                boolean darkSquare = (row + col) % 2 != 0;
                square.setBackground(darkSquare ? DARK_SQUARE : LIGHT_SQUARE);
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
        switch (Character.toUpperCase(piece.getSymbol().charAt(1))) {
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
        // if (piece.getColor() == Color.WHITE) {
        //     return darkSquare ? new java.awt.Color(255, 255, 245) : new java.awt.Color(72, 83, 105);
        // }
        // return darkSquare ? new java.awt.Color(255, 230, 190) : new java.awt.Color(38, 42, 48);
    
        if (piece.getColor() == Color.WHITE) {
            return new java.awt.Color(255, 255, 245); 
        }
        return new java.awt.Color(38, 42, 48);
    
    }
}
