import java.util.Random;

public class Dice {
    public int roll() {
        Random random = new Random();
        return random.nextInt(6) + 1; // Generates a random number between 1 and 6 (inclusive)
    }

    public int[] rollTwoDice() {
        int[] rolls = new int[2];
        rolls[0] = roll();
        rolls[1] = roll();
        return rolls;
    }
}
