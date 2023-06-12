public class Player {
    private String name;
    private int balance;
    private int position;
    private boolean inJail;

    private int ind;

    public Player(String name) {
        this.name = name;
        this.balance = 1500;
        this.position = 0;
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
    public void setInd(int ind){
        this.ind = ind;
    }
    public int getInd() {
        return ind;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    public void deductBalance(int amount) {
        if( balance - amount < 0){
            balance = 0;
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
