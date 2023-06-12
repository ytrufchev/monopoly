import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player[] players;
    private Board[] board;
    private Dice dice;

    private int currentPlayerIndex;
    private int jailTurns;

    public Game(Player[] players, int numPlayers) {
        this.players = players;
        this.board = Board.initializeTiles();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
    }

    public void playTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        String name = currentPlayer.getName();
        int location = currentPlayer.getPosition();
        int balance = currentPlayer.getBalance();
        boolean isInJail = currentPlayer.isInJail();
        if (!currentPlayer.isEliminated()) {
            if (isInJail) {
                handleJailLogic(currentPlayer);
            }
            System.out.println("\nIt is " + name + "'s turn you have $" + balance + " press enter to roll the dice");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            System.out.println(name + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
            int newPosition = (location + totalRoll) % board.length;
            if (newPosition < location) {
                currentPlayer.addBalance(200);
                currentPlayer.setPosition(newPosition);
                System.out.println(currentPlayer.getName() + " gets $200 for passing GO and now has $" + currentPlayer.getBalance());
                System.out.println(currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".");
            } else {
                currentPlayer.setPosition(newPosition);
                System.out.println(name + " landed on " + board[newPosition].getName() + ".");
            }
            if (board[newPosition].getType() == 1) {
                currentPlayer.setPosition(newPosition);
                goToJail(currentPlayerIndex);
            } else if (board[newPosition].getType() == 2) {
                currentPlayer.setPosition(newPosition);
                payTax(currentPlayerIndex);
            } else if (board[newPosition].getType() == 4) {
                currentPlayer.setPosition(newPosition);
                chance(currentPlayerIndex);
            } else if (board[newPosition].getType() == 0) {
                currentPlayer.setPosition(newPosition);
                buyableProperty(currentPlayerIndex, newPosition);
            }
            else if (board[newPosition].getType() == 3) {
                currentPlayer.setPosition(newPosition);
            }
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
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
        currentPlayer.setPosition(board[10].getTileNumber());
        System.out.println(currentPlayer.getName() + " is in jail.");
        currentPlayer.resetJailTurns();
    }

    public void payTax(int index) {
        Player currentPlayer = players[index];
        int taxAmount = 100;
        System.out.println(currentPlayer.getName() + " must pay Tax of $" + taxAmount + ".");
        currentPlayer.deductBalance(taxAmount);
    }

    public void chance(int index) {
        Player currentPlayer = players[index];
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
        Board tile = board[location];
        String propertyName = tile.getName();
        int propertyPrice = tile.getPrice();
        int owner = tile.getOwner();
        if (owner == -1 && currentPlayer.getBalance() > tile.getPrice()) {
            System.out.println("This property is not owned by anyone.");
            System.out.println("Do you want to buy it for $" + propertyPrice + "? (Y/N)");

            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                if (currentPlayer.getBalance() >= propertyPrice + 1) {
                    currentPlayer.deductBalance(propertyPrice);
                    tile.setOwner(index);
                    System.out.println(currentPlayer.getName() + " has purchased " + propertyName + ".");
                } else {
                    System.out.println("Insufficient funds to purchase the property.");
                }
            }
        } else if (owner != currentPlayerIndex && tile.getType() == 0 ) {
            int rent = board[location].getRent();
            if (tile.getOwner() != currentPlayerIndex && tile.getOwner() != -1) {
                System.out.println("This property is owned by " + players[tile.getOwner()].getName() + ".");
                System.out.println(currentPlayer.getName() + " must pay $" + rent + " as rent to " + players[tile.getOwner()].getName() + ".");
                currentPlayer.deductBalance(rent);
                players[tile.getOwner()].addBalance(rent);
                System.out.println(currentPlayer.getName() + " payed $" + board[location].getRent() + " to " + players[tile.getOwner()].getName());
            }
        }
    }
}