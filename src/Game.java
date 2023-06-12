import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Map;

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
            System.out.println("1. Roll the dice.\n2. Mortgage properties");
            Scanner sc = new Scanner(System.in);
            int menu = sc.nextInt();
            switch (menu){
                case 1 : startTurn(currentPlayer); break;
                case 2 : mortgageProperties(currentPlayer); break;
                default: System.out.println("Something is not alright"); break;
            }
        }
        else{
            freeEliminatedPlayerTiles(currentPlayerIndex);
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
    public void mortgageProperties(Player currentPlayer){
        HashMap<String, Integer> properties = new HashMap<String, Integer>();
        for (int i = 0; i < board.length; i++) {
            if (board[i].getOwner() == currentPlayerIndex && !board[i].isMortgaged()) {
                properties.put(board[i].getName(), i);
            }
        }

        int position = 1;
        for (Map.Entry<String, Integer> entry : properties.entrySet()) {
            System.out.println(position + ". You can mortgage " + entry.getKey() + " for $" + board[entry.getValue()].getMortgageValue());
            position++;
        }

        System.out.println("Choose a property to mortgage (enter the corresponding number): ");
        Scanner sc = new Scanner(System.in);
        int selection = sc.nextInt();

        if (selection >= 1 && selection <= properties.size()) {
            int propertyIndex = (int) properties.values().toArray()[selection - 1];
            board[propertyIndex].setMortgaged(true);
            int mortgageValue = board[propertyIndex].getMortgageValue();
            currentPlayer.addBalance(mortgageValue);
            System.out.println("You have mortgaged " + board[propertyIndex].getName() + " for $" + mortgageValue);
            System.out.println("Your new balance is $" + currentPlayer.getBalance());
        } else {
            System.out.println("Invalid selection!");
        }

    }
    public void startTurn(Player currentPlayer){
        if (currentPlayer.isInJail()) {
            handleJailLogic(currentPlayer);
        }
        int[] rolls = dice.rollTwoDice();
        int totalRoll = rolls[0] + rolls[1];
        System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
        int newPosition = (currentPlayer.getPosition() + totalRoll) % board.length;
        switch (board[newPosition].getType()) {
            case 0 -> {
                if (newPosition < currentPlayer.getPosition()) {
                    passGo(currentPlayerIndex, newPosition, currentPlayer.getPosition());
                }
                buyableProperty(currentPlayerIndex, newPosition);
            }
            case 1 -> goToJail(currentPlayerIndex);
            case 2 -> payTax(currentPlayerIndex, newPosition);
            case 3 -> currentPlayer.setPosition(newPosition);
            case 4 -> chance(currentPlayerIndex, newPosition);
            default -> System.out.println("If you see this I've messed up");
        }
    }
    public void freeEliminatedPlayerTiles(int currentPlayerIndex){
        for (Board value : board) {
            if (value.getOwner() == currentPlayerIndex) {
                value.setOwner(-1);
            }
        }
        players[currentPlayerIndex].clearProperties();
    }
    public void passGo(int currentPlayerIndex, int newPosition, int location){
        Player currentPlayer = players[currentPlayerIndex];
        if (newPosition < location) {
            currentPlayer.addBalance(200);
            currentPlayer.setPosition(newPosition);
            System.out.println(currentPlayer.getName() + " gets $200 for passing GO and now has $" + currentPlayer.getBalance());
            System.out.println(currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".");
        } else {
            currentPlayer.setPosition(newPosition);
            System.out.println(currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".");
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
    private void handleJailLogic(Player currentPlayer) {
        int turns = currentPlayer.getJailTurns();
        if (turns == 3) {
            System.out.println(currentPlayer.getName() + " has spent 3 turns in jail and must pay $200 to get out.");
            currentPlayer.deductBalance(200);
            System.out.println(currentPlayer.getName() + " now has: $" + currentPlayer.getBalance());
            currentPlayer.setInJail(false);
            currentPlayer.resetJailTurns();
        } else {
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
            if (rolls[0] == rolls[1]) {
                System.out.println(currentPlayer.getName() + " rolled doubles and gets out of jail.");
                currentPlayer.setInJail(false);
                currentPlayer.setPosition((currentPlayer.getPosition() + totalRoll) % board.length);
                System.out.println(currentPlayer.getName() + " moved to " + board[currentPlayer.getPosition()].getName() + ".");
                currentPlayer.resetJailTurns();
            } else {
                System.out.println(currentPlayer.getName() + " failed to roll doubles and remains in jail.");
                currentPlayer.increaseJailTurns();
            }
        }
    }
    public void goToJail(int index) {
        Player currentPlayer = players[index];
        currentPlayer.setInJail(true);
        currentPlayer.setPosition(10);
        System.out.println(currentPlayer.getName() + " is in jail.");
        currentPlayer.resetJailTurns();
    }
    public void payTax(int index, int location) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        int taxAmount = 100;
        System.out.println(currentPlayer.getName() + " must pay Tax of $" + taxAmount + ".");
        currentPlayer.deductBalance(taxAmount);
    }
    public void chance(int index, int location) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        int[] amounts = {-500, -200, -100, -50, 50, 100, 200, 500};
        Random random = new Random();
        int randomNumber = random.nextInt(8);
        int amount = Math.abs(amounts[randomNumber]);
        if (amounts[randomNumber] < 0) {
            System.out.println("You must pay $" + amount);
            currentPlayer.deductBalance(amount);
        } else {
            System.out.println("You get $" + amount);
            currentPlayer.addBalance(amount);
        }
    }
    public void buyableProperty(int index, int location) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        Board tile = board[location];
        if (tile.getOwner() == -1 && currentPlayer.getBalance() > tile.getPrice()) {
            tileNotOwnedAndAffordable(tile, currentPlayer, index);
        }else if(tile.getOwner() == -1 && currentPlayer.getBalance() < tile.getPrice()){
            System.out.println(currentPlayer.getName() + " landed on " + tile.getName() + " but can't afford it at $" + tile.getPrice());
        }else if (tile.getOwner() != currentPlayerIndex && tile.getType() == 0 ) {
            if (tile.getOwner() != currentPlayerIndex && tile.getOwner() != -1) {
                tileOwnedByAnotherPlayer(tile, currentPlayer);
            }
        }
    }

    private void tileOwnedByAnotherPlayer(Board tile, Player currentPlayer) {
        System.out.println("This property is owned by " + players[tile.getOwner()].getName() + ".");
        System.out.println(currentPlayer.getName() + " must pay $" + tile.getRent() + " as rent to " + players[tile.getOwner()].getName() + ".");
        currentPlayer.deductBalance(tile.getRent());
        players[tile.getOwner()].addBalance(tile.getRent());
        System.out.println(currentPlayer.getName() + " payed $" + tile.getRent() + " to " + players[tile.getOwner()].getName());
    }

    private void tileNotOwnedAndAffordable(Board tile, Player currentPlayer,int  index) {
        System.out.println("This property is not owned by anyone.");
        System.out.println("Do you want to buy it for $" + tile.getPrice() + "? (Y/N)");
        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            if (currentPlayer.getBalance() >= tile.getPrice() + 1) {
                currentPlayer.deductBalance(tile.getPrice());
                tile.setOwner(index);
                currentPlayer.addProperty(currentPlayer.getPosition());
                System.out.println(currentPlayer.getName() + " has purchased " + tile.getName() + ".");
            } else {
                System.out.println("Insufficient funds to purchase the property.");
            }
        }
    }
}