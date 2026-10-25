package game;

import java.util.Scanner;

import board.Board;
import pieces.Piece;
import utils.Color;
import utils.Position;

/**
 * Runs a console-based chess game and tracks the current player's turn.
 */
public class Game {
    
    /** The board used for the current game. */
    private Board board;

    /** The color of the player whose turn it is. */
    private Color currentTurn;

    /**
     * Creates a game with a newly initialized board and White to move first.
     */
    public Game() { 
        board = new Board();
        currentTurn = Color.WHITE;
    }
    
    /**
     * Reads and processes moves from the supplied input until the game ends.
     *
     * @param input the scanner providing player input
     */
    public void play(Scanner input) {
        String message = "";

        while (true) {
            // clear console
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // draw the current board state and player turn
            board.display();
            System.out.println("Player turn: " + currentTurn);
            if (!message.isEmpty()) {
                System.out.println(message);
                message = "";
            }
            System.out.println("Enter a move (e.g. E2 E4) or 'q' to quit:");

            if (!input.hasNextLine()) {
                break; // input closed
            }
            String line = input.nextLine().trim();

            if (line.equalsIgnoreCase("q")) {
                break;
            }

            String[] squares = line.split("\\s+"); // store squares based on whitespace separation
            if (squares.length != 2) {
                message = "Enter only two squares e.g. E2 E4.";
                continue;
            }

            Position current;
            Position destination;
            try { // convert notion of user input to Position objects
                current = Position.fromAlgebraic(squares[0]);
                destination = Position.fromAlgebraic(squares[1]);
            } catch (IllegalArgumentException exception) {
                message = exception.getMessage();
                continue;
            }

            Piece piece = board.getPiece(current);
            if (piece == null) {
                message = "There is no piece on " + current + ".";
                continue;
            }

            if (piece.getColor() != currentTurn) {
                message = "That piece belongs to the other player.";
                continue;
            }

            if (!board.movePiece(current, destination)) { // check valid move and update board if valid
                message = "That piece can't move to " + destination + ".";
                continue;
            }

            // check for check/checkmate/stalemate *

            currentTurn = currentTurn.opposite();
        }

        System.out.println("End of game.");
    }
    
    /**
     * Provides a hook for end-of-game handling.
     */
    public void end() {

    }


   
}
