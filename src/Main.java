import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Getting the number of players
        System.out.print("Enter the number of players (2-4): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        Player[] players = new Player[numPlayers];
        //Getting the names of the players and setting them in the class Player
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name for Player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();

            Player player = new Player(playerName);
            players[i] = player;
        }
        //Start the game and new turn while more than one player is active
        Game game = new Game(players);
        while (!game.isGameOver()) {
            game.playTurn();
        }
    }
}