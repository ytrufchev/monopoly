public class Board {
    private int tileNumber;
    private String name;
    private int price;
    private int rent;
    private int mortgageValue;
    private int owner;
    private boolean mortgaged;

    private int type;

    public Board(String name, int price, int rent, int mortgageValue, int owner, boolean mortgaged, int type) {
        this.tileNumber = tileNumber;
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.mortgageValue = mortgageValue;
        this.owner = owner;
        this.mortgaged = false;
        this.type = type;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRent() {
        return rent;
    }

    public int getMortgageValue() {
        return mortgageValue;
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }
    public int getType() {
        return type;
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }

    public static Board[] initializeTiles() {
        Board[] tiles = new Board[40];

        tiles[0] = new Board("Go", 0, 0, 0, -1, false, 3);
        tiles[1] = new Board("Mediterranean Avenue", 60, 50, 30, -1, false, 0);
        tiles[2] = new Board("Community Chest", 0, 0, 0, -1, false, 4);
        tiles[3] = new Board("Baltic Avenue", 60, 50, 30, -1, false, 0);
        tiles[4] = new Board("Income Tax", 0, 0, 0, -1, false,2);
        tiles[5] = new Board("Reading Railroad", 200, 120, 100, -1, false, 0);
        tiles[6] = new Board("Oriental Avenue", 100, 80, 50, -1, false, 0);
        tiles[7] = new Board("Chance", 0, 0, 0, -1, false, 4);
        tiles[8] = new Board("Vermont Avenue", 100, 70, 50, -1, false, 0);
        tiles[9] = new Board("Connecticut Avenue", 120, 85, 60, -1, false, 0);
        tiles[10] = new Board("Jail/Just Visiting", 0, 0, 0, -1, false, 3);
        tiles[11] = new Board("St. Charles Place", 140, 70, 70, -1, false, 0);
        tiles[12] = new Board("Electric Company", 150, 0, 0, -1, false, 0);
        tiles[13] = new Board("States Avenue", 140, 70, 70, -1, false, 0);
        tiles[14] = new Board("Virginia Avenue", 160, 90, 80, -1, false, 0);
        tiles[15] = new Board("Pennsylvania Railroad", 200, 120, 100, -1, false, 0);
        tiles[16] = new Board("St. James Place", 180, 100, 90, -1, false, 0);
        tiles[17] = new Board("Community Chest", 0, 0, 0, -1, false, 4);
        tiles[18] = new Board("Tennessee Avenue", 180, 100, 90, -1, false, 0);
        tiles[19] = new Board("New York Avenue", 200, 120, 100, -1, false, 0);
        tiles[20] = new Board("Free Parking", 0, 0, 0, -1, false, 3);
        tiles[21] = new Board("Kentucky Avenue", 220, 130, 110, -1, false, 0);
        tiles[22] = new Board("Chance", 0, 0, 0, -1, false, 4);
        tiles[23] = new Board("Indiana Avenue", 220, 130, 110, -1, false, 0);
        tiles[24] = new Board("Illinois Avenue", 240, 150, 120, -1, false, 0);
        tiles[25] = new Board("B. & O. Railroad", 200, 120, 100, -1, false, 0);
        tiles[26] = new Board("Atlantic Avenue", 260, 160, 130, -1, false, 0);
        tiles[27] = new Board("Ventnor Avenue", 260, 160, 130, -1, false, 0);
        tiles[28] = new Board("Water Works", 150, 100, 0, -1, false, 0);
        tiles[29] = new Board("Marvin Gardens", 280, 170, 140, -1, false, 0);
        tiles[30] = new Board("Go To Jail", 0, 0, 0, -1, false, 1);
        tiles[31] = new Board("Pacific Avenue", 300, 200, 150, -1, false, 0);
        tiles[32] = new Board("North Carolina Avenue", 300, 200, 150, -1, false, 0);
        tiles[33] = new Board("Community Chest", 0, 0, 0, -1, false, 4);
        tiles[34] = new Board("Pennsylvania Avenue", 320, 210, 160, -1, false, 0);
        tiles[35] = new Board("Short Line", 200, 25, 139, -1, false, 0);
        tiles[36] = new Board("Chance", 0, 0, 0, -1, false, 4);
        tiles[37] = new Board("Park Place", 350, 250, 175, -1, false, 0);
        tiles[38] = new Board("Luxury Tax", 0, 0, 0, -1, false, 2);
        tiles[39] = new Board("Boardwalk", 400, 300, 200, -1, false, 0);


        return tiles;
    }
}
