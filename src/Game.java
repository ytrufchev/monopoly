import java.util.Random;
import java.util.Scanner;

public class Game {
    private final Player[] players;
    private final Board[] board;
    private final Dice dice;
    private int currentPlayerIndex;
    public Game(Player[] players) {
        this.players = players;
        this.board = Board.initializeTiles();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
    }
    public void playTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        if (!currentPlayer.isEliminated()) {
            System.out.println("\nIt is " + currentPlayer.getName() + "'s turn you have $" + currentPlayer.getBalance());
            System.out.println("1. Roll the dice.\n2. Mortgage properties\n3. unmortgage properties");
            Scanner sc = new Scanner(System.in);
            int menu = sc.nextInt();
            switch (menu){
                case 1 : startTurn(currentPlayer); break;
                case 2 : UtilitiesMethods.mortgageProperties(currentPlayer, false, board, currentPlayerIndex); break;
                case 3 : UtilitiesMethods.mortgageProperties(currentPlayer, true, board, currentPlayerIndex); break;
                default: System.out.println("Something is not alright"); break;
            }
        }
        else{
            UtilitiesMethods.freeEliminatedPlayerTiles(players, currentPlayerIndex, board);
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
    public void startTurn(Player currentPlayer){
        if (currentPlayer.isInJail()) {
            UtilitiesMethods.handleJailLogic(currentPlayer, dice, board);
        }
        int[] rolls = dice.rollTwoDice();
        int totalRoll = rolls[0] + rolls[1];
        System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
        int newPosition = (currentPlayer.getPosition() + totalRoll) % board.length;
        switch (board[newPosition].getType()) {
            case 0 -> {
                if (newPosition < currentPlayer.getPosition()) {
                    UtilitiesMethods.passGo(currentPlayerIndex, newPosition, currentPlayer.getPosition(), players, board);
                }
                UtilitiesMethods.buyableProperty(currentPlayerIndex, newPosition, players, board);
            }
            case 1 -> UtilitiesMethods.goToJail(currentPlayerIndex, players);
            case 2 -> UtilitiesMethods.payTax(currentPlayerIndex, newPosition, players);
            case 3 -> currentPlayer.setPosition(newPosition);
            case 4 -> UtilitiesMethods.chance(currentPlayerIndex, newPosition, players);
            default -> System.out.println("If you see this I've messed up");
        }
    }
    public boolean isGameOver() {
        int remainingPlayers = 0;
        String winnerName = "";
        for (Player player : players) {
            if (!player.isEliminated()) {
                remainingPlayers++;
                winnerName = player.getName();
            }
        }
        if (remainingPlayers <= 1) {
            System.out.println("Game Over! The winner is " + winnerName + "!");
            return true;
        }
        return false;
    }
}