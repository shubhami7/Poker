public class StandardCard {
    private int value;
    private String suit;
    
    public StandardCard(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    // Getter for card's value
    public int getValue() {
        return this.value;
    }

    // Getter for card's suit
    public String getSuit() {
        return this.suit;
    } 

    /**
     * Method to convert car's integer value to a string
     * 
     * @return string representing card's value
     */
    public String convertValueToString() {
        switch (this.value) {
            case 2: return "Two";
            case 3: return "Three";
            case 4: return "Four";
            case 5: return "Five";
            case 6: return "Six";
            case 7: return "Seven";
            case 8: return "Eight";
            case 9: return "Nine";
            case 10: return "Ten";
            case 11: return "Jack";
            case 12: return "Queen";
            case 13: return "King";
            case 14: return "Ace";
            default: return "Invalid";
        }
    }

    public String cardInfo() {
        return (convertValueToString() + " of " + this.suit);
    }
}
