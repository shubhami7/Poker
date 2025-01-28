import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class PokerGame {
    private ArrayList<Player> players;
    private StandardDeck gameDeck;
    public StandardCard[] communityCards;
    private int pot;
    private int smallBlind;
    private int bigBlind;
    private Player buttonPlayer;
    private Player smallBlindPlayer;
    private Player bigBlindPlayer;
    private int playersTotalBets[];
    private boolean keepPlaying;
    Scanner input = new Scanner(System.in);

    public PokerGame() {
        this.players = new ArrayList<Player>();
        this.gameDeck = new StandardDeck();
        this.communityCards = new StandardCard[5];
        this.pot = 0;
        this.playerSetup();
        this.smallBlind = this.blindsSetup();
        this.bigBlind = this.smallBlind * 2;
        this.buttonPlayer = this.players.get(this.players.size() - 1);
        this.smallBlindPlayer = this.players.get(0);
        this.bigBlindPlayer = this.players.get(1);
        this.keepPlaying = true;

        while (keepPlaying) {
            //Preflop
            this.payBlinds();
            this.bettingRound(true);
            // Flop
            this.dealNextCommunityCard();
            this.bettingRound(false);
            // Turn
            this.dealNextCommunityCard();
            this.bettingRound(false);
            // River
            this.dealNextCommunityCard();
            this.bettingRound(false);

            // Finds the winner
            this.findWinner();

            // Rotate blinds
            this.rotateBlinds();

            System.out.println("Do you want to keep playing? y/n");
            this.keepPlaying = input.nextLine().equals("y");
        }
        
    }

    // Method to add a singular player
    public void addPlayer() {
        System.out.println("Enter player name:");
        String newPlayer = input.next();
        System.out.println("Enter player's starting stack size: ");
        int newPlayerStack = input.nextInt();
        System.out.println("Welcome " + newPlayer + ", Your starting stack is " + newPlayerStack);

        StandardCard[] temp = {gameDeck.drawCard(), gameDeck.drawCard()};
        players.add(new Player(newPlayer, newPlayerStack, temp));
    }

    // Method to add multiple players
    public void playerSetup() {
        boolean addAnotherPlayer = true;
        while (addAnotherPlayer) {
            addAnotherPlayer = false;
            this.addPlayer();
            System.out.println("Add another player? (y/n)");
            if (input.next().toLowerCase().equals("y")) {
                addAnotherPlayer = true;
            }
        }
        this.playersTotalBets = new int[players.size()];
    }

    // Method to set up game blinds based on user input
    public int blindsSetup() {
        System.out.println("What is the small blind value?");
        int smallBlind = input.nextInt();
        return smallBlind;
    }

    // Correctly bets the blinds during the start of a game
    public void payBlinds() {
        if (this.players.size() > 2) {
            this.smallBlindPlayer.subtractChips(this.smallBlind);
            this.playersTotalBets[this.players.indexOf(this.smallBlindPlayer)] = this.smallBlind;
            this.bigBlindPlayer.subtractChips(this.bigBlind);
            this.playersTotalBets[this.players.indexOf(this.bigBlindPlayer)] = this.bigBlind;
            updatePot();
        }  
    }

    // Method to ensure all remaining players have equal bets
    public boolean allBetsEqual() {
        int highestBet = this.getHighestBet();
        for (int j = 0; j < this.playersTotalBets.length; j++) {
            if ((this.playersTotalBets[j] < highestBet) && (this.players.get(j).getIsInGame())) {
                return false;
            }
        }
        return true;
    }

    // Method to find current highest bet
    public int getHighestBet() {
        int highestBet = 0;
        for (int i = 0; i < this.playersTotalBets.length; i++) {
            if (this.playersTotalBets[i] > highestBet && this.players.get(i).getIsInGame()) {
                highestBet = this.playersTotalBets[i];
            }
        }
        return highestBet;
    }

    // Method to iterate though the players, considering their action and calling the respective methods
    public void bettingRound(boolean isPreFlop) {
        int startingPlayerIndex = this.players.indexOf(this.smallBlindPlayer);
        if (isPreFlop) {
            startingPlayerIndex += 2;
        }
        int currentPlayerIndex = startingPlayerIndex;
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(currentPlayerIndex).getIsInGame()) {
                currentPlayerIndex = this.individualBet(currentPlayerIndex);
            } 
        }
        while (!this.allBetsEqual()) {
            currentPlayerIndex = this.individualBet(currentPlayerIndex);
        }
    }

    public int individualBet(int currentPlayerIndex) {
        String checkOrCall = this.allBetsEqual() ? "check" : "call";
        System.out.println("Pot: " + this.pot);
        String playerName = this.players.get(currentPlayerIndex).getName();
        System.out.println(playerName + "'s turn.\n" + playerName + "'s cards: " + this.players.get(currentPlayerIndex).getHoleCards()[0].cardInfo() +", " + this.players.get(currentPlayerIndex).getHoleCards()[1].cardInfo() + "\n Fold, " + checkOrCall + ", or raise?");
        String action = input.next();
        // Folds
        if (action.toLowerCase().equals("fold")) {
            this.fold(this.players.get(currentPlayerIndex));
        // Calls
        } else if (action.toLowerCase().equals("call")) {
            this.call(this.players.get(currentPlayerIndex));
        // Raises
        } else if (action.toLowerCase().equals("raise")) {
            this.raise(this.players.get(currentPlayerIndex));
        }
        // Check does nothing and just moves on
        currentPlayerIndex++;
        if (currentPlayerIndex == this.players.size()) {
            currentPlayerIndex = 0;
        }
        return currentPlayerIndex;
    }

    /**
     * Folds a player
     * 
     * @param player object to be folded
     */
    public void fold(Player player) {
        player.setIsInGame(false);
    }

    /**
     * Calls the highest bet of that round
     * 
     * @param player object that calls
     */
    public void call(Player player) {
        int highestBet = this.getHighestBet();
        int playerBetDifference = highestBet - (this.playersTotalBets[this.players.indexOf(player)]);
        player.subtractChips(playerBetDifference);
        this.playersTotalBets[this.players.indexOf(player)] += playerBetDifference;
        updatePot();
    }   

    /**
     * Raise the bet to a higher value than current highest value
     * 
     * @param player object that chooses to raise
     */
    public void raise(Player player) {
        System.out.println(player.getName() + ": How much do you want to raise?");
        int raiseAmount = input.nextInt();
        this.call(player);
        player.subtractChips(raiseAmount);
        this.playersTotalBets[this.players.indexOf(player)] += raiseAmount;
        updatePot();
    }

    // Method to deal community cards, based on current state of the game
    public void dealNextCommunityCard() {
        if (this.communityCards[0] == null) {
            this.communityCards[0] = this.gameDeck.drawCard();
            this.communityCards[1] = this.gameDeck.drawCard();
            this.communityCards[2] = this.gameDeck.drawCard();
        } else if (this.communityCards[3] == null) {
            this.communityCards[3] = this.gameDeck.drawCard();
        } else if (this.communityCards[4] == null) {
            this.communityCards[4] = this.gameDeck.drawCard();
        }
        System.out.println("------------------------------------------------------------------------------------");
        for (StandardCard card : communityCards) {
            if (card != null) {
                System.out.print(card.cardInfo() + ", ");
            }
        }
        System.out.println("");
        System.out.println("------------------------------------------------------------------------------------");
    }

    public void rotateBlinds() {
        int buttonIndex = this.players.indexOf(this.buttonPlayer);
        int smallBlindIndex = this.players.indexOf(this.smallBlindPlayer);
        int bigBlindIndex = this.players.indexOf(this.bigBlindPlayer);

        if (buttonIndex < this.players.size() - 1) {
            this.buttonPlayer = this.players.get(buttonIndex + 1);
        } else {
            this.buttonPlayer = this.players.get(0);
        }

        if (smallBlindIndex < this.players.size() - 1) {
            this.smallBlindPlayer = this.players.get(smallBlindIndex + 1);
        } else {
            this.smallBlindPlayer = this.players.get(0);
        }

        if (bigBlindIndex < this.players.size() - 1) {
            this.bigBlindPlayer = this.players.get(bigBlindIndex + 1);
        } else {
            this.bigBlindPlayer = this.players.get(0);
        }
    }

    public ArrayList<Integer> getPlayersCardValues(Player player) {
        ArrayList<Integer> allCardValues = new ArrayList<Integer>();
        for (int i = 0; i < this.communityCards.length; i++) {
            allCardValues.add(this.communityCards[i].getValue());
        }
        allCardValues.add(player.getHoleCards()[0].getValue());
        allCardValues.add(player.getHoleCards()[1].getValue());
        return allCardValues;
    }

    public ArrayList<StandardCard> sortLowToHighCards(ArrayList<StandardCard> cardsToSort) {
        ArrayList<StandardCard> sortedCards = new ArrayList<StandardCard>();
        while (cardsToSort.size() > 0) {
            int lowestValue = 15;
            int indexToRemove = -1;
            for (int i = 0; i < cardsToSort.size(); i++) {
                if (cardsToSort.get(i).getValue() < lowestValue) {
                    lowestValue = cardsToSort.get(i).getValue();
                    indexToRemove = i;
                }
            }
            if (indexToRemove != -1) {
                sortedCards.add(cardsToSort.remove(indexToRemove));
            }
        }
        return sortedCards;
    }

    // Method to cycle through the various poker hands in order of strength to determine winner
    public void findWinner() {
        ArrayList<Player> winningPlayers = new ArrayList<Player>();
        winningPlayers = royalFlush();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        } 
        winningPlayers = straightFlush();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = quads();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        } 
        winningPlayers = fullHouse();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = flush();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = straight();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = trips();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = twoPair();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = pair();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
        winningPlayers = highCard();
        if (winningPlayers.size() > 0) {
            handleWinners(winningPlayers);
            return;
        }
    }

    public void updatePot() {
        this.pot = 0;
        for (int i = 0; i < this.playersTotalBets.length; i++) {
            this.pot += this.playersTotalBets[i];
        }
    }

    public void handleWinners(ArrayList<Player> winningPlayers) {
        int tempWinnerAward = this.pot / winningPlayers.size();
            for (int i = 0; i < winningPlayers.size(); i++) {
                winningPlayers.get(i).addChips(tempWinnerAward);
            }
    }

    public ArrayList<Integer> getValuesOfSuit(ArrayList<StandardCard> playerCardsAll, String suit) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < playerCardsAll.size(); i++) {
            if (playerCardsAll.get(i).getSuit().contains(suit)) {
                result.add(playerCardsAll.get(i).getValue());
            }
        }
        return result;
    }

    public ArrayList<Integer> getValueOfAllCards(ArrayList<StandardCard> playerCardsAll) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < playerCardsAll.size(); i++) {
            result.add(playerCardsAll.get(i).getValue());
        }
        return result;
    }

    public ArrayList<StandardCard> tempPlayerCardsAll(Player player) {
        ArrayList<StandardCard> result = new ArrayList<>();
        for (int i = 0; i < this.communityCards.length; i++) {
            result.add(this.communityCards[i]);
        }
        result.add(player.getHoleCards()[0]);
        result.add(player.getHoleCards()[1]);

        return result;
    }

    public ArrayList<Player> royalFlush() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        int hearts = 0;
        int diamonds = 0;
        int spades = 0;
        int clubs = 0;
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                ArrayList<Integer> tempPlayerCards = new ArrayList<>();
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int k = 0; k < tempPlayerCardsAll.size(); k++) {
                    if (tempPlayerCardsAll.get(i).getSuit().contains("Hearts")) {
                        hearts++;
                    }
                    if (tempPlayerCardsAll.get(i).getSuit().contains("Diamonds")) {
                        diamonds++;
                    }
                    if (tempPlayerCardsAll.get(i).getSuit().contains("Spades")) {
                        spades++;
                    }
                    if (tempPlayerCardsAll.get(i).getSuit().contains("Clubs")) {
                        clubs++;
                    }
                }

                if (hearts >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Hearts");
                } else if (diamonds >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Diamonds");
                } else if (spades >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Spades");
                } else if (clubs >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Clubs");
                }

                if (tempPlayerCards.contains(10) &&
                tempPlayerCards.contains(11) &&
                tempPlayerCards.contains(12) &&
                tempPlayerCards.contains(13) &&
                tempPlayerCards.contains(14)) {
                    winningPlayers.add(tempPlayer);
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> straightFlush() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        int hearts = 0;
        int diamonds = 0;
        int spades = 0;
        int clubs = 0;
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                ArrayList<Integer> tempPlayerCards = new ArrayList<>();
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int k = 0; k < tempPlayerCardsAll.size(); k++) {
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Hearts")) {
                        hearts++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Diamonds")) {
                        diamonds++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Spades")) {
                        spades++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Clubs")) {
                        clubs++;
                    }
                }

                if (hearts >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Hearts");
                } else if (diamonds >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Diamonds");
                } else if (spades >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Spades");
                } else if (clubs >= 5) {
                    tempPlayerCards = getValuesOfSuit(tempPlayerCardsAll, "Clubs");
                }

                Collections.sort(tempPlayerCards);
                int lowestValue = 15;
                int secondLowestValue = 15;
                int thirdLowestValue = 15;
                
                if (tempPlayerCards.size() >= 5) {
                    if (tempPlayerCards.contains(lowestValue + 1) &&
                    tempPlayerCards.contains(lowestValue + 2) &&
                    tempPlayerCards.contains(lowestValue + 3) &&
                    tempPlayerCards.contains(lowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
                if (tempPlayerCards.size() >= 6) {
                    if (tempPlayerCards.contains(secondLowestValue + 1) &&
                    tempPlayerCards.contains(secondLowestValue + 2) &&
                    tempPlayerCards.contains(secondLowestValue + 3) &&
                    tempPlayerCards.contains(secondLowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
                if (tempPlayerCards.size() >= 7) {
                    if (tempPlayerCards.contains(thirdLowestValue + 1) &&
                    tempPlayerCards.contains(thirdLowestValue + 2) &&
                    tempPlayerCards.contains(thirdLowestValue + 3) &&
                    tempPlayerCards.contains(thirdLowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> quads() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                int[] allValues = new int[13];
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int j = 0; j < tempPlayerCardsAll.size(); j++) {
                    allValues[tempPlayerCardsAll.get(j).getValue()-2]++;
                }
                for (int k = 0; k < allValues.length; k++) {
                    if (allValues[k] >= 4) {
                        winningPlayers.add(tempPlayer);
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> fullHouse() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                int[] allValues = new int[13];
                boolean threeCard = false;
                boolean twoCard = false;
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int j = 0; j < tempPlayerCardsAll.size(); j++) {
                    allValues[tempPlayerCardsAll.get(j).getValue() - 2]++;
                }
                for (int k = 0; k < allValues.length; k++) {
                    if (allValues[k] >= 3) {
                        threeCard = true;
                    } else if (allValues[k] >= 2) {
                        twoCard = true;
                    }
                }
                if (threeCard && twoCard) {
                    winningPlayers.add(tempPlayer);
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> flush() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            int hearts = 0;
            int diamonds = 0;
            int spades = 0;
            int clubs = 0;
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int k = 0; k < tempPlayerCardsAll.size(); k++) {
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Hearts")) {
                        hearts++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Diamonds")) {
                        diamonds++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Spades")) {
                        spades++;
                    }
                    if (tempPlayerCardsAll.get(k).getSuit().contains("Clubs")) {
                        clubs++;
                    }
                }
                if (hearts >= 5 || diamonds >= 5 || spades >= 5 || clubs >= 5) {
                    winningPlayers.add(tempPlayer);
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> straight() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                ArrayList<Integer> tempPlayerCards = getValueOfAllCards(tempPlayerCardsAll);

                Collections.sort(tempPlayerCards);
                int lowestValue = 15;
                int secondLowestValue = 15;
                int thirdLowestValue = 15;
                
                if (tempPlayerCards.size() >= 5) {
                    if (tempPlayerCards.contains(lowestValue + 1) &&
                    tempPlayerCards.contains(lowestValue + 2) &&
                    tempPlayerCards.contains(lowestValue + 3) &&
                    tempPlayerCards.contains(lowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
                if (tempPlayerCards.size() >= 6) {
                    if (tempPlayerCards.contains(secondLowestValue + 1) &&
                    tempPlayerCards.contains(secondLowestValue + 2) &&
                    tempPlayerCards.contains(secondLowestValue + 3) &&
                    tempPlayerCards.contains(secondLowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
                if (tempPlayerCards.size() >= 7) {
                    if (tempPlayerCards.contains(thirdLowestValue + 1) &&
                    tempPlayerCards.contains(thirdLowestValue + 2) &&
                    tempPlayerCards.contains(thirdLowestValue + 3) &&
                    tempPlayerCards.contains(thirdLowestValue + 4)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> trips() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                int[] allValues = new int[13];
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int j = 0; j < tempPlayerCardsAll.size(); j++) {
                    allValues[tempPlayerCardsAll.get(j).getValue()-2]++;
                }
                for (int k = 0; k < allValues.length; k++) {
                    if (allValues[k] >= 3) {
                        winningPlayers.add(tempPlayer);
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> twoPair() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                int[] allValues = new int[13];
                int pairCount = 0;
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int j = 0; j < tempPlayerCardsAll.size(); j++) {
                    allValues[tempPlayerCardsAll.get(j).getValue()-2]++;
                }
                for (int k = 0; k < allValues.length; k++) {
                    if (allValues[k] >= 2) {
                        pairCount++;
                        if (pairCount == 2) {
                            winningPlayers.add(tempPlayer);
                        }
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> pair() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        
        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                int[] allValues = new int[13];
                ArrayList<StandardCard> tempPlayerCardsAll = tempPlayerCardsAll(tempPlayer);
                for (int j = 0; j < tempPlayerCardsAll.size(); j++) {
                    allValues[tempPlayerCardsAll.get(j).getValue()-2]++;
                }
                for (int k = 0; k < allValues.length; k++) {
                    if (allValues[k] >= 2 && !winningPlayers.contains(tempPlayer)) {
                        winningPlayers.add(tempPlayer);
                    }
                }
            }
        }

        return winningPlayers;
    }

    public ArrayList<Player> highCard() {
        ArrayList<Player> winningPlayers = new ArrayList<>();
        int[][] sortedPlayerHoleCards = new int[this.players.size()][2];
        int highestColOne = 0;
        int highestColTwo = 0;
        boolean tie = false;

        for (int i = 0; i < this.players.size(); i++) {
            Player tempPlayer = this.players.get(i);
            if (tempPlayer.getIsInGame()) {
                ArrayList<Integer> tempHoleCards = new ArrayList<>();
                tempHoleCards.add(tempPlayer.getHoleCards()[0].getValue());
                tempHoleCards.add(tempPlayer.getHoleCards()[1].getValue());
                Collections.sort(tempHoleCards);
                if (tempHoleCards.get(1) > highestColTwo) {
                    highestColTwo = tempHoleCards.get(1);
                    winningPlayers.clear();
                    winningPlayers.add(tempPlayer);
                    sortedPlayerHoleCards = new int[this.players.size()][2];
                    sortedPlayerHoleCards[i][0] = i;
                    sortedPlayerHoleCards[i][1] = tempHoleCards.get(0);
                } else if (tempHoleCards.get(1) == highestColTwo) {
                    winningPlayers.add(tempPlayer);
                    tie = true;
                    sortedPlayerHoleCards[i][0] = i;
                    sortedPlayerHoleCards[i][1] = tempHoleCards.get(0);
                }
            }
        }

        if (tie) {
            winningPlayers.clear();
            for (int k = 0; k < sortedPlayerHoleCards.length; k++) {
                if (sortedPlayerHoleCards[k][1] > highestColOne) {
                    highestColOne = sortedPlayerHoleCards[k][1];
                }
            }
            for (int k = 0; k < sortedPlayerHoleCards.length; k++) {
                if (sortedPlayerHoleCards[k][1] == highestColOne) {
                    winningPlayers.add(this.players.get(1));
                }
            }
        }
        
        return winningPlayers;
    }
}
