package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import board.Board;
import pieces.King;
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

    /** The winning color, or {@code null} while the game is still in progress. */
    private Color winner;

    /** Successful moves in the order they were made. */
    private final List<MoveRecord> moveHistory;

    /**
     * Creates a game with a newly initialized board and White to move first.
     */
    public Game() { 
        board = new Board();
        currentTurn = Color.WHITE;
        moveHistory = new ArrayList<>();
    }

    /**
     * Gets the board used for the current game.
     *
     * @return the current game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the color of the player whose turn it is.
     *
     * @return the current player's color
     */
    public Color getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Gets the winning color once a King has been captured.
     *
     * @return the winning color, or {@code null} if the game is still in progress
     */
    public Color getWinner() {
        return winner;
    }

    /**
     * Checks whether a player has won by capturing the opposing King.
     *
     * @return {@code true} if the game has ended
     */
    public boolean isGameOver() {
        return winner != null;
    }

    /**
     * Gets an unmodifiable view of the moves made during this game.
     *
     * @return the move history in chronological order
     */
    public List<MoveRecord> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    /**
     * Attempts a move for the current player and advances the turn on success.
     * Capturing the opposing King ends the game without advancing the turn.
     *
     * @param current the position of the piece to move
     * @param destination the destination position
     * @return {@code true} if the current player's move succeeds
     */
    public boolean makeMove(Position current, Position destination) {
        if (isGameOver()) {
            return false;
        }

        Piece piece = board.getPiece(current);
        Piece capturedPiece = board.getPiece(destination);
        if (piece == null || piece.getColor() != currentTurn
                || !board.movePiece(current, destination)) {
            return false;
        }

        moveHistory.add(new MoveRecord(currentTurn, piece.getSymbol(), current, destination,
                capturedPiece == null ? null : capturedPiece.getSymbol()));
        if (capturedPiece instanceof King) {
            winner = currentTurn;
        } else {
            currentTurn = currentTurn.opposite();
        }
        return true;
    }

    /**
     * Reverts the most recent move and restores the corresponding turn and game status.
     *
     * @return {@code true} if a move was reverted; {@code false} if there is no move to undo
     */
    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        MoveRecord move = moveHistory.get(moveHistory.size() - 1);
        if (!board.undoLastMove()) {
            return false;
        }

        moveHistory.remove(moveHistory.size() - 1);
        currentTurn = move.getPlayerColor();
        winner = null;
        return true;
    }

    /** Immutable details about a move, used by the game history display. */
    public static final class MoveRecord {
        private final Color playerColor;
        private final String pieceSymbol;
        private final Position from;
        private final Position to;
        private final String capturedPieceSymbol;

        private MoveRecord(Color playerColor, String pieceSymbol, Position from, Position to,
                String capturedPieceSymbol) {
            this.playerColor = playerColor;
            this.pieceSymbol = pieceSymbol;
            this.from = from;
            this.to = to;
            this.capturedPieceSymbol = capturedPieceSymbol;
        }

        /**
         * Gets the color of the player who made the move.
         *
         * @return the moving player's color
         */
        public Color getPlayerColor() {
            return playerColor;
        }

        /**
         * Gets the symbol of the piece that moved.
         *
         * @return the moving piece's two-character symbol
         */
        public String getPieceSymbol() {
            return pieceSymbol;
        }

        /**
         * Gets the move's starting square.
         *
         * @return the starting position
         */
        public Position getFrom() {
            return from;
        }

        /**
         * Gets the move's destination square.
         *
         * @return the destination position
         */
        public Position getTo() {
            return to;
        }

        /**
         * Gets the captured piece's symbol, if the move captured a piece.
         *
         * @return the captured piece's two-character symbol, or {@code null}
         */
        public String getCapturedPieceSymbol() {
            return capturedPieceSymbol;
        }
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

            if (!makeMove(current, destination)) { // check valid move and update board if valid
                message = "That piece can't move to " + destination + ".";
                continue;
            }

            if (isGameOver()) {
                System.out.println(winner + " wins!");
                break;
            }

            // check for check/checkmate/stalemate *
        }

        System.out.println("End of game.");
    }
    
    /**
     * Provides a hook for end-of-game handling.
     */
    public void end() {

    }


   
}
