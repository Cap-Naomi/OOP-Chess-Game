package gui;

import board.Board;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
 * Handles saving and loading the chess game state to and from a text file.
 */
public class GameStateManager {

    /**
     * Saves the current turn and all pieces on the board to the specified file.
     *
     * @param board       the active chessboard to save
     * @param currentTurn the color of the player whose turn it is
     * @param file        the destination file
     * @throws IOException if an error occurs while writing to the file
     */
    public void saveGame(Board board, Color currentTurn, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("TURN:" + currentTurn.name());
            writer.newLine();

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece piece = board.getPiece(new Position(r, c));
                    if (piece != null) {
                        writer.write(String.format("PIECE:%s:%s:%d:%d",
                                piece.getColor().name(),
                                piece.getClass().getSimpleName(),
                                r,
                                c));
                        writer.newLine();
                    }
                }
            }
        }
    }

    /**
     * Loads a saved game from the specified file, restoring all pieces onto
     * the provided board and returning the saved player's turn.
     *
     * @param board the chessboard to populate with the loaded pieces
     * @param file  the saved game file to read
     * @return the {@link Color} of the player whose turn it is
     * @throws IOException if the file cannot be read or contains invalid data
     */
    public Color loadGame(Board board, File file) throws IOException {
        Color loadedTurn = null;
        List<Piece> loadedPieces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":");
                if (parts[0].equals("TURN") && parts.length == 2) {
                    loadedTurn = Color.valueOf(parts[1]);
                } else if (parts[0].equals("PIECE") && parts.length == 5) {
                    Color color = Color.valueOf(parts[1]);
                    String type = parts[2];
                    int row = Integer.parseInt(parts[3]);
                    int col = Integer.parseInt(parts[4]);

                    Position pos = new Position(row, col);
                    if (!pos.isValid()) {
                        throw new IOException("Invalid piece position in save file: " + line);
                    }
                    loadedPieces.add(createPiece(type, color, pos));
                } else {
                    throw new IOException("Unrecognized line in save file: " + line);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Corrupted save file data.", e);
        }

        if (loadedTurn == null) {
            throw new IOException("Save file is missing the TURN entry.");
        }

        // Only modify the live board after the entire file parsed cleanly
        board.clearBoard();
        for (Piece piece : loadedPieces) {
            board.placePiece(piece);
        }

        return loadedTurn;
    }

    /**
     * Instantiates the appropriate Piece subclass from its saved type name.
     *
     * @param type  the simple class name of the piece (e.g., "Pawn", "King")
     * @param color the color of the piece
     * @param pos   the board position of the piece
     * @return the constructed {@link Piece}
     * @throws IOException if the piece type is unknown
     */
    private Piece createPiece(String type, Color color, Position pos) throws IOException {
        switch (type) {
            case "Pawn":   return new Pawn(color, pos);
            case "Rook":   return new Rook(color, pos);
            case "Knight": return new Knight(color, pos);
            case "Bishop": return new Bishop(color, pos);
            case "Queen":  return new Queen(color, pos);
            case "King":   return new King(color, pos);
            default:
                throw new IOException("Unknown piece type in save file: " + type);
        }
    }
}