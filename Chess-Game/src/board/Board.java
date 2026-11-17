package board;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import pieces.*;
import utils.Color;
import utils.Position;

/**
 * Represents the 8x8 chessboard, manages piece placement, and renders
 * the text-based board state to the console.
 */
public class Board {

    /** The 8x8 grid storing the pieces currently on the board. */
    private final Piece[][] grid;

    /** Pieces captured by White. */
    private final List<Piece> capturedByWhite;

    /** Pieces captured by Black. */
    private final List<Piece> capturedByBlack;

    /** Successful moves in the order they were made. */
    private final Deque<MoveSnapshot> moveHistory;

    /**
     * Constructs a new Board and initializes it with the standard chess starting positions.
     */
    public Board() {
        this.grid = new Piece[8][8];
        this.capturedByWhite = new ArrayList<>();
        this.capturedByBlack = new ArrayList<>();
        this.moveHistory = new ArrayDeque<>();
        initializeBoard();
    }

    /**
     * Populates the 8x8 grid with all 32 chess pieces in their standard starting squares.
     */
    public final void initializeBoard() {
        // Clear board and capture lists first
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid[r][c] = null;
            }
        }
        capturedByWhite.clear();
        capturedByBlack.clear();
        moveHistory.clear();

        // Black back rank (Row 0 / Rank 8)
        placePiece(new Rook(Color.BLACK, new Position(0, 0)));
        placePiece(new Knight(Color.BLACK, new Position(0, 1)));
        placePiece(new Bishop(Color.BLACK, new Position(0, 2)));
        placePiece(new Queen(Color.BLACK, new Position(0, 3)));
        placePiece(new King(Color.BLACK, new Position(0, 4)));
        placePiece(new Bishop(Color.BLACK, new Position(0, 5)));
        placePiece(new Knight(Color.BLACK, new Position(0, 6)));
        placePiece(new Rook(Color.BLACK, new Position(0, 7)));

        // Black pawns (Row 1 / Rank 7)
        for (int c = 0; c < 8; c++) {
            placePiece(new Pawn(Color.BLACK, new Position(1, c)));
        }

        // White pawns (Row 6 / Rank 2)
        for (int c = 0; c < 8; c++) {
            placePiece(new Pawn(Color.WHITE, new Position(6, c)));
        }

        // White back rank (Row 7 / Rank 1)
        placePiece(new Rook(Color.WHITE, new Position(7, 0)));
        placePiece(new Knight(Color.WHITE, new Position(7, 1)));
        placePiece(new Bishop(Color.WHITE, new Position(7, 2)));
        placePiece(new Queen(Color.WHITE, new Position(7, 3)));
        placePiece(new King(Color.WHITE, new Position(7, 4)));
        placePiece(new Bishop(Color.WHITE, new Position(7, 5)));
        placePiece(new Knight(Color.WHITE, new Position(7, 6)));
        placePiece(new Rook(Color.WHITE, new Position(7, 7)));
    }

    /**
     * Places a piece on the board at the piece's current position.
     *
     * @param piece the piece to place on the grid
     */
    public void placePiece(Piece piece) {
        if (piece != null && piece.getPosition().isValid()) {
            Position pos = piece.getPosition();
            grid[pos.getRow()][pos.getCol()] = piece;
        }
    }

    /**
     * Retrieves the piece located at the specified position.
     *
     * @param position the board coordinate to query
     * @return the {@code Piece} at the given position, or {@code null} if the square is empty or out of bounds
     */
    public Piece getPiece(Position position) {
        if (position == null || !position.isValid()) {
            return null;
        }
        return grid[position.getRow()][position.getCol()];
    }

    /**
     * Gets the pieces captured by White.
     *
     * @return an unmodifiable view of White's captured pieces
     */
    public List<Piece> getCapturedByWhite() {
        return Collections.unmodifiableList(capturedByWhite);
    }

    /**
     * Gets the pieces captured by Black.
     *
     * @return an unmodifiable view of Black's captured pieces
     */
    public List<Piece> getCapturedByBlack() {
        return Collections.unmodifiableList(capturedByBlack);
    }

    /**
     * Checks whether the specified position is empty (contains no piece).
     *
     * @param position the board coordinate to check
     * @return {@code true} if the coordinate is within bounds and contains no piece; {@code false} otherwise
     */
    public boolean isEmpty(Position position) {
        return position != null && position.isValid() && getPiece(position) == null;
    }

    /**
     * Checks whether the specified position contains an opponent's piece relative to the given color.
     *
     * @param position the board coordinate to check
     * @param myColor  the color of the friendly player
     * @return {@code true} if a piece of the opposite color occupies the square; {@code false} otherwise
     */
    public boolean isOpponentPiece(Position position, Color myColor) {
        Piece target = getPiece(position);
        return target != null && target.getColor() != myColor;
    }

    /**
     * Attempts to move a piece from one board position to another.
     *
     * @param current the position of the piece to move
     * @param destination the position to move the piece to
     * @return {@code true} if the move is completed; {@code false} if it is invalid
     */
    public boolean movePiece(Position current, Position destination) {
        // checking for valid input and valid positions
        if (current == null || destination == null
                || !current.isValid() || !destination.isValid()
                || current.equals(destination)) {
            return false;
        }
        
        Piece piece = getPiece(current);
        if (piece == null || !piece.isValidMove(destination, this)) { // if piece dne or invalid move 
            return false;
        }

        Piece target = getPiece(destination);
        if (target != null && target.getColor() == piece.getColor()) { // if piece at destination is current player's
            return false;
        }

        MoveSnapshot move = new MoveSnapshot(piece, current, destination, target, piece.hasMoved());
        // validation for castling *

        grid[current.getRow()][current.getCol()] = null; // clear current pieces square
        if (target != null) {
            if (piece.getColor() == Color.WHITE) {
                capturedByWhite.add(target);
            } else {
                capturedByBlack.add(target);
            }
        }
        piece.setPosition(destination);
        grid[destination.getRow()][destination.getCol()] = piece;
        moveHistory.push(move);

        // if castling *

        return true;
    }

    /**
     * Reverts the most recent successful move, restoring its moving and captured pieces.
     *
     * @return {@code true} if a move was reverted; {@code false} if there is no move to undo
     * @throws IllegalStateException if the board no longer matches the recorded move
     */
    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        MoveSnapshot move = moveHistory.peek();
        int fromRow = move.from.getRow();
        int fromCol = move.from.getCol();
        int toRow = move.to.getRow();
        int toCol = move.to.getCol();
        if (grid[toRow][toCol] != move.piece || grid[fromRow][fromCol] != null) {
            throw new IllegalStateException("Board state does not match the most recent move.");
        }

        List<Piece> capturedPieces = move.piece.getColor() == Color.WHITE
                ? capturedByWhite
                : capturedByBlack;
        if (move.capturedPiece != null && !capturedPieces.contains(move.capturedPiece)) {
            throw new IllegalStateException("Captured piece is missing from the capture history.");
        }

        moveHistory.pop();
        grid[toRow][toCol] = null;
        move.piece.setPosition(move.from);
        move.piece.setHasMoved(move.pieceHadMoved);
        grid[fromRow][fromCol] = move.piece;
        if (move.capturedPiece != null) {
            capturedPieces.remove(move.capturedPiece);
            grid[toRow][toCol] = move.capturedPiece;
        }
        return true;
    }

    /** Stores the board state needed to reverse one successful move. */
    private static final class MoveSnapshot {
        private final Piece piece;
        private final Position from;
        private final Position to;
        private final Piece capturedPiece;
        private final boolean pieceHadMoved;

        private MoveSnapshot(Piece piece, Position from, Position to,
                Piece capturedPiece, boolean pieceHadMoved) {
            this.piece = piece;
            this.from = from;
            this.to = to;
            this.capturedPiece = capturedPiece;
            this.pieceHadMoved = pieceHadMoved;
        }
    }

    /**
     * Prints the current state of the chessboard to the console with rank and file labels.
     */
    public void display() {
        System.out.println("  A  B  C  D  E  F  G  H");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r;
            System.out.print(rank + " ");
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null) {
                    System.out.print(piece.getSymbol());
                } else {
                    // Light squares are spaces, dark squares are "##"
                    System.out.print((r + c) % 2 == 0 ? "  " : "##");
                }
                if (c < 7) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}