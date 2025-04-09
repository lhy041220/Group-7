# Group-7

## Table of Contents 

- [Project Description](#project-description)
- [Player Rules](#player-rules)
- [Game components](#game-components)
- [Game preparation](#game-preparation)
- [Start the game](#start-the-game)
- [Perform a maximum of 3 actions](#perform-a-maximum-of-3-actions)
  - [Move](#move)
  - [Drainage of water](#drainage-of-water)
  - [Pass 1 treasure card](#pass-1-treasure-card)
  - [Get the treasure](#get-the-treasure)
- [Draw 2 treasure cards](#draw-2-treasure-cards)
  - [Card type](#there-are-5-cards-for-each-type-of-treasure-in-the-library)
- [Draw flood cards](#draw-flood-cards)
- [Game Over](#game-over)
- [Role capability](#role-capability)

## Project Description
This project implements the board game "Forbidden Island" using Java. Adventures in the Forbidden Island... Brave and Fearless 2-4 player game | Suitable for ages 10 and over Forbidden Island was once the secret domain of the ancient and mysterious empire known as the "Aikins." Legend has it that the Aikins could control the core elements of Earth—fire, wind, water, and earth—through four sacred treasures. These treasures are: the Flame Crystal, the Wind Statue, the Holy Cup of the Sea, and the Earth Gem. Given that these treasures could cause catastrophic destruction if they fell into enemy hands, the Aikins secretly hid them on Forbidden Island and designed the island to sink when invaders attempted to seize the treasures. For centuries after the mysterious fall of the Aikin Empire, Forbidden Island remained undiscovered... until now. Will your team be the first to break through the forbidden island's defenses, capture the treasure and leave alive?

## Player Rules:
Game goal
- Players play as adventurers to find a way to find 4 treasures and escape safely before the island sinks.

## Game components
**58 cards, classified as follows:**
- 28 treasure cards (red back): 4 kinds of treasures, 5 each; 
- 3 "Water level rising!" cards; 
- 3 "Helicopter rescue" cards; 2 "Sandbag" cards. 
- 24 flood cards (blue back)
- 6 Explorer cards 
- 24 double-sided island plates
- Six wooden pieces 
- 4 treasure models: Earth Gem, Wind Statue, Flame Crystal, Ocean Holy Grail 
-  1 water level meter and 1 water level marker

## Game preparation
  - After shuffling the 24 plates, the color of the plates faces upward, and arrange them randomly according to the following figure:
  - Place the four treasures of wind, fire, stone and water in the four directions of the island.
  - Cards are divided into flood cards, treasure cards and character cards according to type.
  - Shuffle the flood cards and place them next to the island, draw one at a time, open 6 flood cards, and place the cards into the flood card discard pile (next to the flood card library) after the corresponding island plate is turned over to the flooded water surface.
  - Shuffle 6 corner color cards, randomly give each player 1, the player will get their character ability to read out to teammates.
  - Each player takes the indicator corresponding to their character's color and places it on the corresponding island plate, placing it on the flooded plate is allowed.
  - After shuffling the treasure cards, issue 2 cards to each player, and place the treasure card face up in front of everyone to see.
  - Place the treasure card next to the island to form a card library, and leave room next to the card library for the treasure card discard pile.
  - Set the flood level to your desired difficulty.

## Start the game
  - In the game, players can view the discard pile of treasure cards and flood cards at any time.
  - Start with the player who has been to the island recently and play clockwise.
  - The turn of the player executes three phases in order:
    1. Perform a maximum of 3 actions
    2. Draw 2 treasure cards
    3. Draw flood cards according to the flood level

## Perform a maximum of 3 actions
Players can perform 0~3 actions per turn. Teammates can give advice to players to perform actions in a turn.There are four actions to choose from, and players can perform the actions they want in any order or repeatedly.

### Move 
It takes 1 action to move the indicator to the right or left or next to each other on the plate (the Angle is not good).The player can move to the flooded plate, but not to the removed space.

### Drainage of water
Take 1 action to turn the flooded plate where the indicator is located or perpendicular to it back to the front.

### Pass 1 treasure card
  - Spend 1 action to pass a treasure card to a teammate on the same board as you.
  - Each transfer of a Treasure card costs 1 action.
  - Players cannot pass action cards.

### Get the treasure
  - If the player has 4 identical treasure cards and the indicator is located on the corresponding treasure board, the player can discard the 4 treasure cards and spend 1 action to retrieve the treasure.
  - Taking treasure on flooded plates is allowed.
  - Place the treasure model in front of you.

## Draw 2 treasure cards
  - Draw 2 cards from the treasure card library, 1 at a time, and add them to yourself facing upward.
  - The player's hand is limited to 5 cards (including treasure and action cards), if the player has more than 6 cards, the player must discard the cards to the remaining 5, if the discard is an action card, you can perform the action before discarding the card.
  - If you draw "Water Level Rise", immediately follow the instructions on the card to execute the effect, put "water level Rise" into the treasure card discard pile, not in front of you.
  - When the treasure card library is exhausted, the discard pile is immediately shuffled to form a new library.
  Artifact Card
###  There are 5 cards for each type of treasure in the library.
1. Mobile card
  - Action cards are divided into two types: 3 helicopter launches and 2 sandbags.
  - Action cards can be played at any time in the game, even during someone else's turn.
  - An action card is not an action.
  - Put the action card into the Treasure discard pile immediately after playing the action card.
2. Water level rise
  - There are 3 "Rising Water level" cards in the deck.
  - When drawing, execute the effect according to the following order:
    1. Turn the water level up.
    2. Shuffle the Flood card discard pile and add it to the top of the Flood card library facing down.
    3. Place "Water Rise" into the Treasure card discard pile.
  - Draw "water level rise" without extra fill draw treasure card.
  - If the player draws 2 "Water Level Rise" cards at once, the flood discard pile is only rewashed once, but the water level has to rise by 2 squares.
  - If there are no cards in the flood card discard pile, the player simply raises the water level.
3. Pump flood cards
  - Draw the same number of flood cards according to the flood level.
  - One at a time, place the open flood card side up on the discard pile, and then turn the corresponding island plate over the flooded water, if it is already flooded, directly remove the plate from the game.
  - The player cannot use the sandbag after knowing the corresponding plate of the flood card.
  - If there is an indicator on the plate, turn over and put the indicator back on the block.
  - If the flooded plate on which the indicator is located is removed, the indicator immediately swims onto the adjacent plate.
  - If no adjacent board can land after the board is removed, the player drowns in the water and all player missions fail. Exceptions: divers can swim to the nearest plate, explorers can swim to the diagonal plate.
  - If the flood card library is exhausted, the pile will be discarded immediately to form a new card library.

## Game over
### Mission success
After the player has collected 4 treasures, all players return to the Fools' Landing board, and one player takes off with a helicopter, the mission is successfully completed. (Fools' Landing flooding does not affect mission completion)
### Task failure
**There are 4 possible failure scenarios:**
1. Before collecting the treasure, the corresponding plate of the treasure has been removed.
2. Fools' Landing was removed.
3. A player drowned.
4. The water level rises to the skull symbol.

## Role capability
### Messenger
Spend 1 action to pass a treasure card to any player on the island.
### Pilot
Take 1 action to fly to any plate. This ability can only be used once per turn.
### Navigator
Spend 1 action to move another teammate up to 2 moves.
### Engineer
Spend 1 action to remove up to 2 plates of stagnant water.
### Diver
Action 1 traverses 1 or more continuously flooded plates.
### Explorer
Can move diagonally or remove water.
