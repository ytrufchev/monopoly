public class Player {
    private String name;
    private int balance;
    private int position;
    private boolean inJail;
    private int jailTurns;
    private boolean isEliminated;

    public Player(String name) {
        this.name = name;
        this.balance = 150;
        this.position = 0;
        this.jailTurns = 0;
    }

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

    public void setPosition(int position) {
        this.position = position;
    }
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
