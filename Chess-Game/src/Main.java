import java.util.Scanner;

import javax.swing.SwingUtilities;

import GUI.GUI;
import game.Game;

public class Main {
    public static void main(String[] args) {
        // if (args.length > 1 || (args.length == 1 && !"--console".equals(args[0]))) {
        //     throw new IllegalArgumentException("Usage: java Main [--console]");
        // }

        // if (args.length == 1) {
        //     playConsole();
        //     return;
        // }

        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }

    // private static void playConsole() {
    //     System.out.println("Welcome to our chess game!");
    //     System.out.println("Press 'Enter' to start.");

    //     Game game = new Game();
    //     try (Scanner input = new Scanner(System.in)) {
    //         if (input.hasNextLine()) {
    //             input.nextLine(); // take in first 'Enter' input before starting game
    //         }
    //         game.play(input);
    //     }
    // }
}
