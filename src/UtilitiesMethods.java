import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class UtilitiesMethods {
    public static void mortgageProperties(Player currentPlayer, boolean operation, Board[] board, int currentPlayerIndex) {
        String opString;
        if (!operation) {
            opString = "mortgage";
        } else {
            opString = "unmortgage";
        }

        HashMap<String, Integer> properties = new HashMap<String, Integer>();
        for (int i = 0; i < board.length; i++) {
            if (board[i].getOwner() == currentPlayerIndex && board[i].isMortgaged() == operation) {
                properties.put(board[i].getName(), i);
            }
        }
        if (properties.isEmpty()) {
            System.out.println("No properties available to " + opString + ".");
            return;
        }

        int position = 1;
        for (Map.Entry<String, Integer> entry : properties.entrySet()) {
            System.out.println(position + ". You can " + opString + " " + entry.getKey() + " for $" + board[entry.getValue()].getMortgageValue());
            position++;
        }

        System.out.println("Choose a property to " + opString + " (enter the corresponding number): ");
        Scanner sc = new Scanner(System.in);
        int selection = sc.nextInt();

        if (selection >= 1 && selection <= properties.size()) {
            int propertyIndex = (int) properties.values().toArray()[selection - 1];
            board[propertyIndex].setMortgaged(!operation);
            int mortgageValue = board[propertyIndex].getMortgageValue();
            if (!operation) {
                currentPlayer.addBalance(mortgageValue);
            } else {
                currentPlayer.deductBalance(mortgageValue);
            }
            System.out.println("You have " + opString + "d " + board[propertyIndex].getName() + " for $" + mortgageValue);
            System.out.println("Your new balance is $" + currentPlayer.getBalance());
        } else {
            System.out.println("Invalid selection!");
        }
    }
    public static void freeEliminatedPlayerTiles(Player[] players, int currentPlayerIndex, Board[] board){
        for (Board value : board) {
            if (value.getOwner() == currentPlayerIndex) {
                value.setOwner(-1);
            }
        }
        players[currentPlayerIndex].clearProperties();
    }
    public static void passGo(int currentPlayerIndex, int newPosition, int location, Player[] players, Board[] board){
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
    public static void payTax(int index, int location, Player[] players) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        int taxAmount = 100;
        System.out.println(currentPlayer.getName() + " must pay Tax of $" + taxAmount + ".");
        currentPlayer.deductBalance(taxAmount);
    }
    public static void chance(int index, int location, Player[] players) {
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
    static void handleJailLogic(Player currentPlayer, Dice dice, Board[] board) {
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
    public static void goToJail(int index, Player[] players) {
        Player currentPlayer = players[index];
        currentPlayer.setInJail(true);
        currentPlayer.setPosition(10);
        System.out.println(currentPlayer.getName() + " is in jail.");
        currentPlayer.resetJailTurns();
    }
    public static void buyableProperty(int currentPlayerIndex, int location, Player[] players, Board[] board) {
        Player currentPlayer = players[currentPlayerIndex];
        currentPlayer.setPosition(location);
        Board tile = board[location];
        if (tile.getOwner() == -1 && currentPlayer.getBalance() > tile.getPrice()) {
            tileNotOwnedAndAffordable(tile, currentPlayer, currentPlayerIndex);
        }else if(tile.getOwner() == -1 && currentPlayer.getBalance() < tile.getPrice()){
            System.out.println(currentPlayer.getName() + " landed on " + tile.getName() + " but can't afford it at $" + tile.getPrice());
        }else if (tile.getOwner() != currentPlayerIndex && tile.getType() == 0 ) {
            if (tile.getOwner() != currentPlayerIndex && tile.getOwner() != -1) {
                tileOwnedByAnotherPlayer(tile, currentPlayerIndex, players);
            }
        }
    }

    private static void tileOwnedByAnotherPlayer(Board tile, int currentPlayerIndex, Player[] players) {
        System.out.println("This property is owned by " + players[tile.getOwner()].getName() + ".");
        System.out.println(players[currentPlayerIndex].getName() + " must pay $" + tile.getRent() + " as rent to " + players[tile.getOwner()].getName() + ".");
        players[currentPlayerIndex].deductBalance(tile.getRent());
        players[tile.getOwner()].addBalance(tile.getRent());
        System.out.println(players[currentPlayerIndex].getName() + " payed $" + tile.getRent() + " to " + players[tile.getOwner()].getName());
    }

    private static void tileNotOwnedAndAffordable(Board tile, Player currentPlayer, int index) {
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