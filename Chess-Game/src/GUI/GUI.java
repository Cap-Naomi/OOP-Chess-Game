package GUI;

import javax.swing.JFrame;

import game.Game;

/**
 * Main Swing window for a chess game.
 * The window contains a {@link BoardManager} connected to the supplied game.
 */
@SuppressWarnings("serial")
public final class GUI extends JFrame {

    /**
     * Creates a chess window with a new game in its starting position.
     */
    public GUI() {
        this(new Game());
    }

    /**
     * Creates a chess window for an existing game.
     *
     * @param game the game model to display
     * @throws NullPointerException if {@code game} is {@code null}
     */
    public GUI(Game game) {
        super("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new BoardManager(game));
        pack();
        setLocationRelativeTo(null);
    }

}
