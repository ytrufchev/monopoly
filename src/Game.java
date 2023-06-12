import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player[] players;
    private Board[] board;
    private Dice dice;

    private int currentPlayerIndex;
    private int[] jailTurns;

    public Game(Player[] players) {
        this.players = players;
        this.board = Board.initializeTiles();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
        this.jailTurns = new int[players.length];
    }

    public void playTurn() {
        Scanner scanner = new Scanner(System.in);
        Player currentPlayer = players[currentPlayerIndex];
        currentPlayer.setInd(currentPlayerIndex);
        System.out.println("It's " + currentPlayer.getName() + "'s turn and he has $" + currentPlayer.getBalance());
        System.out.println("Press enter to roll the dice");
        scanner.nextLine();
        if (currentPlayer.isInJail()) {
            handleJailLogic(currentPlayer);
        } else {
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");

            int newPosition = (currentPlayer.getPosition() + totalRoll) % board.length;
            if(newPosition < currentPlayer.getPosition()){
                currentPlayer.setPosition(newPosition);
                currentPlayer.addBalance(200);
                System.out.println(currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".");
                System.out.println(currentPlayer.getName() + " gets $200 for passing GO and now has "+currentPlayer.getBalance());
            }
            else {
                currentPlayer.setPosition(newPosition);
                System.out.println(currentPlayer.getName() + " landed on " + board[newPosition].getName() + ".");
            }
            if (board[newPosition].getType() == 1) {
                currentPlayer.setInJail(true);
                currentPlayer.setPosition(board[newPosition].getTileNumber());
                System.out.println(currentPlayer.getName() + " is in jail.");
                jailTurns[currentPlayerIndex] = 0;
            } else if (board[newPosition].getType() == 2) {
                int taxAmount = 100;
                System.out.println(currentPlayer.getName() + " must pay Tax of $" + taxAmount + ".");
                currentPlayer.deductBalance(taxAmount);
            }
            else if (board[newPosition].getType() == 4) {
                int[] amounts = {-500, -200, -100, -50, 50, 100, 200, 500};
                Random random = new Random();
                int randomNumber = random.nextInt(8);
                if(amounts[randomNumber] < 0){
                    System.out.println("You must pay $"+ Math.abs(amounts[randomNumber]));
                    currentPlayer.deductBalance(Math.abs(amounts[randomNumber]));
                }
                else{
                    System.out.println("You get $"+ Math.abs(amounts[randomNumber]));
                    currentPlayer.addBalance(Math.abs(amounts[randomNumber]));
                }
            }else if (board[newPosition].getType() == 0) {
                currentPlayer.setPosition(newPosition);

                if (board[newPosition].getType() == 0) {
                    String propertyName = board[newPosition].getName();
                    int propertyPrice = board[newPosition].getPrice();
                    int propertyRent = board[newPosition].getRent();
                    String owner = board[newPosition].getOwner();
                    if (owner.equals("")) {
                        System.out.println("This property is not owned by anyone.");
                        System.out.println("Do you want to buy it for $" + propertyPrice + "? (Y/N)");

                        Scanner sc = new Scanner(System.in);
                        String choice = sc.nextLine();

                        if (choice.equalsIgnoreCase("Y")) {
                            if (currentPlayer.getBalance() >= propertyPrice) {
                                currentPlayer.deductBalance(propertyPrice);
                                board[newPosition].setOwner(currentPlayer.getName().toString());
                                System.out.println(currentPlayer.getName() + " has purchased " + propertyName + ".");
                            } else {
                                System.out.println("Insufficient funds to purchase the property.");
                            }
                        }
                    } else if (owner != currentPlayer.getName() && board[newPosition].getType() == 0) {
                        int rent = board[newPosition].getRent();
                        String propertyOwner = board[newPosition].getOwner();
                        System.out.println(propertyOwner);
                        int ownerIndex = -1;

                        for (int i = 0; i < players.length; i++) {
                            if (players[i].getName().equals(owner)) {
                                ownerIndex = i;
                                break;
                            }
                        }

                        if (ownerIndex != currentPlayerIndex && ownerIndex != -1) {
                            System.out.println("This property is owned by " + players[ownerIndex].getName() + ".");
                            System.out.println(currentPlayer.getName() + " must pay $" + rent + " as rent to " + propertyOwner + ".");
                            currentPlayer.deductBalance(rent);
                            players[ownerIndex].addBalance(rent);
                            System.out.println(currentPlayer.getName() + " payed $" + board[newPosition].getRent() + " to " + propertyOwner);
                        }
                    }
                }
            }
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    public boolean isGameOver() {
        Player currentPlayer = players[currentPlayerIndex];
        if (currentPlayer.getBalance() <= 0) {
            System.out.println(currentPlayer.getName() + "'s balance reached 0 and they are eliminated.");
            return true;
        }
        return false;
    }

    private void handleJailLogic(Player currentPlayer) {
        if (jailTurns[currentPlayerIndex] == 3) {
            System.out.println(currentPlayer.getName() + " has spent 3 turns in jail and must pay $200 to get out.");
            currentPlayer.deductBalance(200);
            System.out.println(currentPlayer.getName() + " now has: $" + currentPlayer.getBalance());
            currentPlayer.setInJail(false);
            jailTurns[currentPlayerIndex] = 0;
        } else {
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");

            if (rolls[0] == rolls[1]) {
                System.out.println(currentPlayer.getName() + " rolled doubles and gets out of jail.");
                currentPlayer.setInJail(false);
                currentPlayer.setPosition((currentPlayer.getPosition() + totalRoll) % board.length);
                System.out.println(currentPlayer.getName() + " moved to " + board[currentPlayer.getPosition()].getName() + ".");
                jailTurns[currentPlayerIndex] = 0;
            } else {
                System.out.println(currentPlayer.getName() + " failed to roll doubles and remains in jail.");
                jailTurns[currentPlayerIndex]++;
            }
        }
    }
}