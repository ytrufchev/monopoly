import java.util.Scanner;

public class Game {
    //Game files that are needed
    private final Player[] players;
    private final Board[] board;
    private final Dice dice;
    //currentPlayerIndex is iterated in this method meaning that it needs to be called in the UtilitiesMethods
    //too much, i'm still looking for a way to decrease the amount of parameters
    private int currentPlayerIndex;
    public Game(Player[] players) {
        this.players = players;
        this.board = Board.initializeTiles();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
    }
    public void playTurn() {
        //Allow dice roll only if the player is not eliminated
        Player currentPlayer = players[currentPlayerIndex];
        if (!currentPlayer.isEliminated()) {
            System.out.println("*".repeat(80));
            System.out.println("It is " + currentPlayer.getName() + "'s turn you have $" + currentPlayer.getBalance() + "\n");
            System.out.println("1. Roll the dice.\n2. Mortgage properties\n3. unmortgage properties");
            Scanner sc = new Scanner(System.in);
            int menu = sc.nextInt();
            switch (menu){
                case 1 : startTurn(currentPlayer); break;
                case 2 : UtilitiesMethods.handleMortgageProperties(currentPlayer, false, board, currentPlayerIndex); break;
                case 3 : UtilitiesMethods.handleMortgageProperties(currentPlayer, true, board, currentPlayerIndex); break;
                default: System.out.println("Something is not alright"); break;
            }
        }
        else{
            String freeProperties = UtilitiesMethods.freeEliminatedPlayerTiles(players, currentPlayerIndex, board);
            System.out.println(freeProperties);
        }
        System.out.println("*".repeat(80));
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
    public void startTurn(Player currentPlayer) {
        //This is where the player turn starts based on the player being in jail or not
        if (currentPlayer.isInJail()) {
            String jailResult = UtilitiesMethods.handleJailLogic(currentPlayer, dice, board);
            System.out.println(jailResult);
        }
        else{
        int[] rolls = dice.rollTwoDice();
        int totalRoll = rolls[0] + rolls[1];
        System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
        int newPosition = (currentPlayer.getPosition() + totalRoll) % board.length;
        switch (board[newPosition].getType()) {
            case 0:
                if (newPosition < currentPlayer.getPosition()) {
                    String passedGo = UtilitiesMethods.passGo(currentPlayerIndex, newPosition, currentPlayer.getPosition(), players, board);
                    System.out.println(passedGo);
                }
                UtilitiesMethods.landedOnBuyableProperty(currentPlayerIndex, newPosition, players, board);
                break;
            case 1:
                String goToJailResult = UtilitiesMethods.goToJail(currentPlayerIndex, players);
                System.out.println(goToJailResult);
                break;
            case 2:
                String landedOnTaxResult = UtilitiesMethods.payTax(currentPlayerIndex, newPosition, players);
                System.out.println(landedOnTaxResult);
                break;
            case 3:
                currentPlayer.setPosition(newPosition);
                break;
            case 4:
                String chanceOutput = UtilitiesMethods.landedOnChance(currentPlayerIndex, newPosition, players);
                System.out.println(chanceOutput);
                break;
            default:
                System.out.println("If you see this I've messed up");
                break;
        }
    }
    }
    //check if any of the players is eliminated
    //couldn't find a better logic that checking at every turn
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