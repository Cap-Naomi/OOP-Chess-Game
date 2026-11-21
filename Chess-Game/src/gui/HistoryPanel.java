package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pieces.Piece;

/**
 * Side panel that displays the game's move history, pieces captured by each player,
 * and an Undo button to revert to the previous game state.
 */
public class HistoryPanel extends JPanel {

    /** Model backing the scrollable move history list. */
    private final DefaultListModel<String> historyModel;

    /** Visual list showing the sequence of moves. */
    private final JList<String> historyList;

    /** Displays symbols of pieces captured by White. */
    private final JLabel capturedByWhiteLabel;

    /** Displays symbols of pieces captured by Black. */
    private final JLabel capturedByBlackLabel;

    /** Button that triggers undoing the most recent move. */
    private final JButton undoButton;

    /**
     * Constructs the HistoryPanel and attaches the Undo action listener.
     *
     * @param undoAction the listener invoked when the Undo button is clicked
     */
    public HistoryPanel(ActionListener undoAction) {
        setLayout(new BorderLayout(0, 10));
        setPreferredSize(new Dimension(270, 640));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Move History Section
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Move History"));

        // 2. Captured Pieces Section
        JPanel capturedPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        capturedPanel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));

        Font symbolFont = new Font("Segoe UI Symbol", Font.PLAIN, 22);

        capturedByWhiteLabel = new JLabel("<html><i>None</i></html>");
        capturedByWhiteLabel.setFont(symbolFont);
        JPanel whiteCapBox = new JPanel(new BorderLayout());
        whiteCapBox.setBorder(BorderFactory.createTitledBorder("Captured by White"));
        whiteCapBox.add(capturedByWhiteLabel, BorderLayout.CENTER);

        capturedByBlackLabel = new JLabel("<html><i>None</i></html>");
        capturedByBlackLabel.setFont(symbolFont);
        JPanel blackCapBox = new JPanel(new BorderLayout());
        blackCapBox.setBorder(BorderFactory.createTitledBorder("Captured by Black"));
        blackCapBox.add(capturedByBlackLabel, BorderLayout.CENTER);

        capturedPanel.add(whiteCapBox);
        capturedPanel.add(blackCapBox);
        capturedPanel.setPreferredSize(new Dimension(250, 180));

        // 3. Undo Button Section
        undoButton = new JButton("Undo Last Move");
        undoButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        undoButton.setEnabled(false);
        undoButton.addActionListener(undoAction);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.add(capturedPanel, BorderLayout.CENTER);
        bottomPanel.add(undoButton, BorderLayout.SOUTH);

        add(historyScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes the move history list, captured piece displays, and Undo button state.
     *
     * @param moves           the ordered list of moves made so far
     * @param capturedByWhite pieces captured by the White player
     * @param capturedByBlack pieces captured by the Black player
     */
    public void updateDisplay(List<MoveRecord> moves, List<Piece> capturedByWhite, List<Piece> capturedByBlack) {
        historyModel.clear();
        for (MoveRecord record : moves) {
            historyModel.addElement(record.toString());
        }

        if (!moves.isEmpty()) {
            historyList.ensureIndexIsVisible(moves.size() - 1);
        }

        capturedByWhiteLabel.setText(formatCapturedPieces(capturedByWhite));
        capturedByBlackLabel.setText(formatCapturedPieces(capturedByBlack));
        undoButton.setEnabled(!moves.isEmpty());
    }

    /**
     * Formats a list of captured pieces into an HTML-wrapped string of Unicode chess symbols.
     *
     * @param pieces the list of captured pieces
     * @return formatted HTML label text
     */
    private String formatCapturedPieces(List<Piece> pieces) {
        if (pieces == null || pieces.isEmpty()) {
            return "<html><span style='font-size:11pt;color:gray;'>None</span></html>";
        }

        StringBuilder sb = new StringBuilder("<html><body style='width:200px;'>");
        for (Piece piece : pieces) {
            sb.append(piece).append(" ");
        }
        sb.append("</body></html>");
        return sb.toString();
    }
}