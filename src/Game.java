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
        if (!currentPlayer.isEliminated()) {
            if (currentPlayer.isInJail()) {
                handleJailLogic(currentPlayer);
            }
            System.out.println("\nIt is " + currentPlayer.getName() + "'s turn you have $" + currentPlayer.getBalance() + " press enter to roll the dice");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
            int[] rolls = dice.rollTwoDice();
            int totalRoll = rolls[0] + rolls[1];
            System.out.println(currentPlayer.getName() + " rolled " + rolls[0] + " and " + rolls[1] + " (total: " + totalRoll + ").");
            int newPosition = (currentPlayer.getPosition() + totalRoll) % board.length;
            switch (board[newPosition].getType()){
                case 0 :
                    if (newPosition < currentPlayer.getPosition()) {
                        passGo(currentPlayerIndex, newPosition, currentPlayer.getPosition());
                    }
                    buyableProperty(currentPlayerIndex, newPosition); break;
                case 1 : goToJail(currentPlayerIndex); break;
                case 2 : payTax(currentPlayerIndex, newPosition); break;
                case 3 : currentPlayer.setPosition(newPosition); break;
                case 4 : chance(currentPlayerIndex, newPosition); break;
                default : System.out.println("If you see this I've messed up"); break;
            }
        }
        else{
            freeEliminatedPlayerTiles(currentPlayerIndex);
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
    public void freeEliminatedPlayerTiles(int currentPlayerIndex){
        for(int i = 0; i < board.length; i++){
            if(board[i].getOwner() == currentPlayerIndex){
                board[i].setOwner(-1);
            }
        }
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
        currentPlayer.setPosition(board[10].getTileNumber());
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
        } else if (tile.getOwner() != currentPlayerIndex && tile.getType() == 0 ) {
            if (tile.getOwner() != currentPlayerIndex && tile.getOwner() != -1) {
                tileOwnedByAnotherPlayer(tile, currentPlayer, index);
            }
        }
    }

    private void tileOwnedByAnotherPlayer(Board tile, Player currentPlayer,int  index) {
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
                System.out.println(currentPlayer.getName() + " has purchased " + tile.getName() + ".");
            } else {
                System.out.println("Insufficient funds to purchase the property.");
            }
        }
    }
}