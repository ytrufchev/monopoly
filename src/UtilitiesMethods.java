import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class UtilitiesMethods {
    public static void handleMortgageProperties(Player currentPlayer, boolean operation, Board[] board, int currentPlayerIndex) {
        String opString;
        if (!operation) {
            opString = "mortgage";
        } else {
            opString = "unmortgage";
        }

        HashMap<String, Integer> properties = new HashMap<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i].getOwner() == currentPlayerIndex && board[i].isMortgaged() == operation && board[i].getMortgageValue() != 0) {
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
    public static String freeEliminatedPlayerTiles(Player[] players, int currentPlayerIndex, Board[] board){
        for (Board value : board) {
            if (value.getOwner() == currentPlayerIndex) {
                value.setOwner(-1);
            }
        }
        players[currentPlayerIndex].clearProperties();
        return "Player " + players[currentPlayerIndex].getName() + " is eliminated and his properties are now unoccupied";
    }
    public static String passGo(int currentPlayerIndex, int newPosition, int location, Player[] players, Board[] board){
        Player currentPlayer = players[currentPlayerIndex];
        if (newPosition < location) {
            currentPlayer.addBalance(200);
            currentPlayer.setPosition(newPosition);
            return currentPlayer.getName() + " gets $200 for passing GO and now has $" + currentPlayer.getBalance() + "\n" +
                    "" + currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".";
        } else {
            currentPlayer.setPosition(newPosition);
            return currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".";
        }
    }
    public static String payTax(int index, int location, Player[] players) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        int taxAmount = 100;
        currentPlayer.deductBalance(taxAmount);
        return currentPlayer.getName() + " must pay Tax of $" + taxAmount + ".";
    }
    public static String landedOnChance(int index, int location, Player[] players) {
        Player currentPlayer = players[index];
        currentPlayer.setPosition(location);
        int[] amounts = {-500, -200, -100, -50, 50, 100, 200, 500};
        Random random = new Random();
        int randomNumber = random.nextInt(8);
        int amount = Math.abs(amounts[randomNumber]);
        if (amounts[randomNumber] < 0) {
            currentPlayer.deductBalance(amount);
            return "You landed on chance and must pay $" + amount;
        } else {
            currentPlayer.addBalance(amount);
            return "You landed on chance and get $" + amount;
        }
    }
    static String handleJailLogic(Player currentPlayer, Dice dice, Board[] board) {
        int turns = currentPlayer.getJailTurns();
        if (turns == 2) {
            currentPlayer.deductBalance(200);
            currentPlayer.setInJail(false);
            currentPlayer.resetJailTurns();
            return currentPlayer.getName() + " has spent 3 turns in jail and must pay $200 to get out.";
        } else {
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            String playerRolled = currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").";
            if (rolls[0] == rolls[1]) {
                String getsOutOfJail = playerRolled + "\n" + currentPlayer.getName() + " rolled doubles and gets out of jail.";
                currentPlayer.setInJail(false);
                String moveAfterJail = "\n" + currentPlayer.getName() + " moved to " + board[currentPlayer.getPosition()].getName() + ".";
                String endsTurn = getsOutOfJail + moveAfterJail;
                currentPlayer.resetJailTurns();
                return endsTurn;
            } else {
                currentPlayer.increaseJailTurns();
                return currentPlayer.getName() + " failed to roll doubles and remains in jail.";
            }
        }
    }
    public static String goToJail(int index, Player[] players) {
        Player currentPlayer = players[index];
        currentPlayer.setInJail(true);
        currentPlayer.setPosition(10);
        currentPlayer.resetJailTurns();
        return currentPlayer.getName() + " is in jail.";
    }
    public static void landedOnBuyableProperty(int currentPlayerIndex, int location, Player[] players, Board[] board) {
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
        System.out.println("You landed on " + tile.getName() + " it is not owned by anyone.");
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