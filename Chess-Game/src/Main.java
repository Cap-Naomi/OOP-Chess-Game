import java.util.*;
import game.Game;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to our chess game!");
        System.out.println("Press 'Enter' to start.");

        Game game = new Game();
        try (Scanner input = new Scanner(System.in)) {
            game.play(input);
        }
    }
}
