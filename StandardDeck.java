import java.util.ArrayList;

public class StandardDeck {
    private ArrayList<StandardCard> deck = new ArrayList<StandardCard>();

    public StandardDeck() {
        this.reset();
        this.shuffleDeck();
    }

    // Getter for deck
    public ArrayList<StandardCard> getDeck() {
        return this.deck;
    }

    // Getter for the current deck's remaining size/count
    public int getDeckSize() {
        return this.deck.size();
    }

    // Method to add every card in a standard deck
    public void reset() {
        this.deck.clear();
        for (int i = 2; i <= 14; i++) {
            deck.add(new StandardCard(i, "Spades"));
            deck.add(new StandardCard(i, "Hearts"));
            deck.add(new StandardCard(i, "Clubs"));
            deck.add(new StandardCard(i, "Diamonds"));
        }
    }

    // Method to shuffle, or randomize the ordering of cards in the deck
    public void shuffleDeck() {
        ArrayList<StandardCard> shuffledDeck = new ArrayList<StandardCard>();
        while (this.deck.size() > 0) {
            int randIndex = ((int) (Math.random() * 100)) % this.deck.size();
            shuffledDeck.add(this.deck.remove(randIndex));
        }
        this.deck = shuffledDeck;
    }
    
    // Method to draw the next card in the shuffled deck
    public StandardCard drawCard() {
        return this.deck.remove(this.deck.size() - 1);
    }
}
