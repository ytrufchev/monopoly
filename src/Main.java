import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players (2-4): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        Player[] players = new Player[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name for Player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();

            Player player = new Player(playerName);
            players[i] = player;
        }

        Game game = new Game(players);
        while (!game.isGameOver()) {
            game.playTurn();
        }
    }
}