import java.util.ArrayList;

public class Player {
    //field needed for each player
    private String name;
    private int balance;
    private int position;
    private boolean inJail;
    private int jailTurns;
    private boolean isEliminated;
    private ArrayList<Integer> properties;

    public Player(String name) {
        //Player constructor with some default values
        this.name = name;
        this.balance = 1500;
        this.position = 0;
        this.jailTurns = 0;
        this.properties = new ArrayList<>();
    }

    public ArrayList getProperties(){
        return properties;
    }
    public void addProperty(int property){
        this.properties.add(property);
    }
    //Used to free up the player properties when eliminated
    public void clearProperties(){
        this.properties.clear();
    }
    //The three methods below change, reset or get the turns spent in jail to
    //allow the player to leave after three turns in jail
    public int getJailTurns() {
        return jailTurns;
    }
    public void increaseJailTurns(){
        this.jailTurns++;
    }
    public void resetJailTurns(){
        this.jailTurns = 0;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getPosition() {
        return position;
    }
    //This is for moving the player on the board
    public void setPosition(int position) {
        this.position = position;
    }
    //Check if the player is eliminated
    public boolean isEliminated(){
        return isEliminated;
    }
    public void setIsEliminated() {
        this.isEliminated = true;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    public void deductBalance(int amount) {
        if( balance - amount <= 0){
            balance = 0;
            setIsEliminated();
            System.out.println("******\n" + this.name + " is eliminated\n******");
        }
        else {
            balance -= amount;
        }
    }

    public boolean isInJail() {
        return inJail;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }
}
