package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pieces.Piece;
import utils.Color;

/**
 * Manages chessboard colors, piece colors, and board sizing preferences.
 */
public final class AppearanceManager {

    /** Available chessboard color palettes. */
    private enum BoardTheme {
        CLASSIC("Classic", new java.awt.Color(240, 217, 181), new java.awt.Color(181, 136, 99)),
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

    /** Available piece color palettes. */
    private enum PiecePalette {
        CLASSIC("Classic"),
        VIVID("Emerald & Rose"),
        GOLD("Gold & Charcoal");

        private final String label;

        PiecePalette(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Available chessboard sizes. */
    private enum BoardSize {
        SMALL("Small", 52, 32),
        MEDIUM("Medium", 80, 48),
        LARGE("Large", 96, 58);

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

    private BoardTheme boardTheme = BoardTheme.CLASSIC;
    private PiecePalette piecePalette = PiecePalette.CLASSIC;
    private BoardSize boardSize = BoardSize.MEDIUM;

    /**
     * Opens a dialog for changing appearance settings.
     *
     * @param owner the window that owns the dialog
     * @param onApply called after the selected settings are applied
     */
    public void showAppearanceDialog(Window owner, Runnable onApply) {
        JDialog dialog = new JDialog(owner, "Appearance Settings", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JComboBox<BoardTheme> themeSelector = new JComboBox<>(BoardTheme.values());
        JComboBox<PiecePalette> piecePaletteSelector = new JComboBox<>(PiecePalette.values());
        JComboBox<BoardSize> sizeSelector = new JComboBox<>(BoardSize.values());
        themeSelector.setSelectedItem(boardTheme);
        piecePaletteSelector.setSelectedItem(piecePalette);
        sizeSelector.setSelectedItem(boardSize);

        JPanel options = new JPanel(new GridLayout(0, 2, 12, 12));
        options.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        options.add(new JLabel("Board colors"));
        options.add(themeSelector);
        options.add(new JLabel("Piece colors"));
        options.add(piecePaletteSelector);
        options.add(new JLabel("Board size"));
        options.add(sizeSelector);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> {
            boardTheme = (BoardTheme) themeSelector.getSelectedItem();
            piecePalette = (PiecePalette) piecePaletteSelector.getSelectedItem();
            boardSize = (BoardSize) sizeSelector.getSelectedItem();
            onApply.run();
            dialog.dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(applyButton);

        dialog.add(options, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(applyButton);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    /**
     * Gets the selected board's preferred dimensions.
     *
     * @return the width and height in pixels for the 8-by-8 board
     */
    public Dimension getBoardDimension() {
        int dimension = boardSize.squarePixels * 8;
        return new Dimension(dimension, dimension);
    }

    /**
     * Gets the piece font sized for the selected board.
     *
     * @return the font used to render the board's piece symbols
     */
    public Font getPieceFont() {
        return new Font("Segoe UI Symbol", Font.PLAIN, boardSize.pieceFontSize);
    }

    /**
     * Gets the selected board color for a square.
     *
     * @param darkSquare whether the requested square is dark
     * @return the selected square color
     */
    public java.awt.Color getSquareColor(boolean darkSquare) {
        return darkSquare ? boardTheme.darkSquare : boardTheme.lightSquare;
    }

    /**
     * Gets a piece color with contrast against its square.
     *
     * @param piece the piece whose color determines the foreground
     * @param darkSquare whether the square is dark
     * @param defaultColor the default foreground for the classic palette
     * @return the foreground color for the piece
     */
    public java.awt.Color getPieceColor(Piece piece, boolean darkSquare, java.awt.Color defaultColor) {
        if (piecePalette == PiecePalette.CLASSIC) {
            return defaultColor;
        }
        if (piecePalette == PiecePalette.VIVID) {
            if (piece.getColor() == Color.WHITE) {
                return darkSquare
                        ? new java.awt.Color(151, 255, 231)
                        : new java.awt.Color(0, 102, 91);
            }
            return darkSquare
                    ? new java.awt.Color(255, 174, 215)
                    : new java.awt.Color(120, 35, 90);
        }
        if (piece.getColor() == Color.WHITE) {
            return darkSquare
                    ? new java.awt.Color(255, 224, 138)
                    : new java.awt.Color(145, 83, 0);
        }
        return darkSquare
                ? new java.awt.Color(196, 164, 132)
                : new java.awt.Color(76, 43, 30);
    }
}
