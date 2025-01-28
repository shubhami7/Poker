public class Player {
    private String name;
    private int chips;
    private StandardCard[] holeCards = new StandardCard[2];
    private boolean isInGame;

    public Player(String name, int chips, StandardCard[] holeCards) {
        this.name = name;
        this.chips = chips;
        this.holeCards = holeCards;
        this.isInGame = true;
    }

    // Getter for player name
    public String getName() {
        return this.name;
    }

    // Getter for player's chip balance
    public float getChips() {
        return this.chips;
    }

    // Getter for player's hole cards
    public StandardCard[] getHoleCards() {
        return this.holeCards;
    }

    // Getter for player's is in game status
    public boolean getIsInGame() {
        return this.isInGame;
    }

    // Setter for player's is in game status
    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    // Setter for player's hole cards
    public void setHoleCards(StandardCard[] holeCards) {
        this.holeCards = holeCards;
    }

    /**
     * Adds additional chips to the player's chip balance
     * 
     * @param chipsToAdd the amount of additional chips to add
     */
    public void addChips(int chipsToAdd) {
        this.chips += chipsToAdd;
    }

    /**
     * Subtracts chips from the player's chip balance
     * 
     * @param chipsToSub the amount of chips to subtract
     */
    public void subtractChips(int chipsToSub) {
        this.chips -= chipsToSub;
    }

    /**
     * Returns all the player's information in a string format
     * 
     * @return player's information
     */
    public String playerInfo() {
        return ("Player " + this.name + " has " + this.chips + " chips\nHas cards: " + holeCards[0].cardInfo() + ", " + holeCards[1].cardInfo() + "\nIn the game: " + this.isInGame);
    }
}