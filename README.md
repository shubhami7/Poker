
# Poker Game

A simple Poker game implemented in Java. This program provides the foundation for simulating a Texas Hold'em Poker game with features like a standard deck, card representation, shuffle/draw mechanics, hand evaluation, and betting mechanics.

## Features

1. **StandardCard Class**
   - Represents an individual playing card with:
     - A `value` (e.g., 2, 3, ... 10, Jack, Queen, King, Ace).
     - A `suit` (Spades, Hearts, Clubs, Diamonds).
   - Includes methods to:
     - Retrieve the card's value and suit.
     - Convert the card's integer value to a string representation (e.g., `11` -> `Jack`).
     - Display the card's full information as a string (e.g., "Ace of Spades").

2. **StandardDeck Class**
   - Represents a standard deck of 52 cards.
   - Provides functionality to:
     - Reset the deck to its original, ordered state.
     - Shuffle the deck to randomize card order.
     - Draw a card from the top of the deck.
     - Check the current size of the deck.

3. **Player Class**
   - Represents an individual player in the poker game with:
     - Attributes for `name`, `chips`, `holeCards`, and `isInGame` status.
     - Methods to:
       - Retrieve and modify player information (e.g., `getName`, `addChips`, `subtractChips`, `playerInfo`).
       - Handle player actions like folding or betting.

4. **PokerGame Class**
   - Implements the core game logic, including:
     - Setting up players and managing the poker table.
     - Handling betting rounds, community card dealing, and blind rotations.
     - Evaluating winning hands and distributing the pot.
   - Includes detailed methods for evaluating poker hands like:
     - `royalFlush`, `straightFlush`, `quads` (four of a kind), `fullHouse`, `flush`, `straight`, `trips` (three of a kind), `twoPair`, `pair`, and `highCard`.
   - Supports game flow control (e.g., rotating blinds, updating pot).

5. **Main Class**
   - Entry point for the program.
   - Initializes a `PokerGame` to start the game.

## Game Flow

1. The game starts by setting up players and blinds.
2. Players take turns during betting rounds (Preflop, Flop, Turn, River).
3. Community cards are dealt progressively in each round.
4. Winners are determined based on the strength of poker hands.
5. The game continues based on user input, allowing for multiple rounds.

This project provides a great starting point for building more advanced poker functionalities, such as AI players, networked multiplayer, or custom betting rules.
