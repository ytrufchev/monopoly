import java.util.Random;

public class Dice {
    public int roll() {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }

    public int[] rollTwoDice() {
        int[] rolls = new int[2];
        rolls[0] = roll();
        rolls[1] = roll();
        return rolls;
    }
}
