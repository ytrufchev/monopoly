public class Cheat {
    public static void identifyCheat(String cheatString, Player currentPlayer){
        String[] cheats = {"give me money", "send me to jail", "eliminate me", "get out of jail"};
        for (int i = 0; i < cheats.length; i++) {
            if(cheatString.equalsIgnoreCase(cheats[i])){
                performCheat(i, currentPlayer);
            }
        }
    }
    public static void performCheat(int selectedCheat, Player currentPlayer){
        switch (selectedCheat){
            case 0 : currentPlayer.addBalance(99999); break;
            case 1 : currentPlayer.setInJail(true); break;
            case 2 : currentPlayer.setIsEliminated(); break;
            case 3 : currentPlayer.setInJail(false); break;
            default:
                System.out.println("You should not see this"); break;
        }
    }
}
