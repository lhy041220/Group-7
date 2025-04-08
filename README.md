# Group-7

## Table of Contents 

- [Project Description](#project-description)
- [Player Rules](#player-rules)

## Project Description
This project implements the board game "Forbidden Island" using Java. 

## Player Rules:
Game goal
◎ Players play as adventurers to find a way to find 4 treasures and escape safely before the island sinks.
 
Game preparation
◎ After shuffling the 24 plates, the color of the plates faces upward, and arrange them randomly according to the following figure:
◎ Please leave a gap between plates when discharging.
◎ Place the four treasures of wind, fire, stone and water in the four directions of the island.
◎ Cards are divided into flood cards, treasure cards and character cards according to type.
◎ Shuffle the flood cards and place them next to the island, draw one at a time, open 6 flood cards, and place the cards into the flood card discard pile (next to the flood card library) after the corresponding island plate is turned over to the flooded water surface.
◎ Shuffle 6 corner color cards, randomly give each player 1, the player will get their character ability to read out to teammates.
◎ Each player takes the indicator corresponding to their character's color and places it on the corresponding island plate, placing it on the flooded plate is allowed.
◎ After shuffling the treasure cards, issue 2 cards to each player, and place the treasure card face up in front of everyone to see.
◎ Place the treasure card next to the island to form a card library, and leave room next to the card library for the treasure card discard pile.
◎ Set the flood level to your desired difficulty.
Start the game
◎ In the game, players can view the discard pile of treasure cards and flood cards at any time.
◎ Start with the player who has been to the island recently and play clockwise.
◎ The turn of the player executes three phases in order:
1. Perform a maximum of 3 actions
2. Draw 2 treasure cards
3. Draw flood cards according to the flood level
1. Perform a maximum of 3 actions
◎ Players can perform 0~3 actions per turn.
◎ Teammates can give advice to players to perform actions in a turn.
There are four actions to choose from, and players can perform the actions they want in any order or repeatedly.
Move
◎ It takes 1 action to move the indicator to the right or left or next to each other on the plate (the Angle is not good).
◎ The player can move to the flooded plate, but not to the removed space.

Drainage of water
◎ Take 1 action to turn the flooded plate where the indicator is located or perpendicular to it back to the front.
Pass 1 treasure card
◎ Spend 1 action to pass a treasure card to a teammate on the same board as you.
◎ Each transfer of a Treasure card costs 1 action.
◎ Players cannot pass action cards.
Get the treasure
◎ If the player has 4 identical treasure cards and the indicator is located on the corresponding treasure board, the player can discard the 4 treasure cards and spend 1 action to retrieve the treasure.
◎ Taking treasure on flooded plates is allowed.
◎ Place the treasure model in front of you.
2. Draw 2 treasure cards
◎ Draw 2 cards from the treasure card library, 1 at a time, and add them to yourself facing upward.
◎ The player's hand is limited to 5 cards (including treasure and action cards), if the player has more than 6 cards, the player must discard the cards to the remaining 5, if the discard is an action card, you can perform the action before discarding the card.
◎ If you draw "Water Level Rise", immediately follow the instructions on the card to execute the effect, put "water level Rise" into the treasure card discard pile, not in front of you.
◎ When the treasure card library is exhausted, the discard pile is immediately shuffled to form a new library.
Artifact Card
◎ There are 5 cards for each type of treasure in the library.
Mobile card
◎ Action cards are divided into two types: 3 helicopter launches and 2 sandbags.
Action cards can be played at any time in the game, even during someone else's turn.
◎ An action card is not an action.
◎ Put the action card into the Treasure discard pile immediately after playing the action card.
Water level rise
◎ There are 3 "Rising Water level" cards in the deck.
◎ When drawing, execute the effect according to the following order:
a. Turn the water level up.
b. Shuffle the Flood card discard pile and add it to the top of the Flood card library facing down.
c. Place "Water Rise" into the Treasure card discard pile.
◎ Draw "water level rise" without extra fill draw treasure card.
◎ If the player draws 2 "Water Level Rise" cards at once, the flood discard pile is only rewashed once, but the water level has to rise by 2 squares.
◎ If there are no cards in the flood card discard pile, the player simply raises the water level.
3. Pump flood cards
◎ Draw the same number of flood cards according to the flood level.
◎ One at a time, place the open flood card side up on the discard pile, and then turn the corresponding island plate over the flooded water, if it is already flooded, directly remove the plate from the game.
◎ The player cannot use the sandbag after knowing the corresponding plate of the flood card.
◎ If there is an indicator on the plate, turn over and put the indicator back on the block.
If the flooded plate on which the indicator is located is removed, the indicator immediately swims onto the adjacent plate.
If no adjacent board can land after the board is removed, the player drowns in the water and all player missions fail. Exceptions: divers can swim to the nearest plate, explorers can swim to the diagonal plate.
◎ If the flood card library is exhausted, the pile will be discarded immediately to form a new card library.
Game over
Mission success
◎ After the player has collected 4 treasures, all players return to the Fools' Landing board, and one player takes off with a helicopter, the mission is successfully completed. (Fools' Landing flooding does not affect mission completion)
Task failure
◎ There are 4 possible failure scenarios:
▲ Before collecting the treasure, the corresponding plate of the treasure has been removed.
▲Fools' Landing was removed.
▲ A player drowned.
▲ The water level rises to the skull symbol.

Role capability
Messenger
Spend 1 action to pass a treasure card to any player on the island.
Pilot
Take 1 action to fly to any plate. This ability can only be used once per turn.
Navigator
Spend 1 action to move another teammate up to 2 moves.
Engineer
Spend 1 action to remove up to 2 plates of stagnant water.
diver
Action 1 traverses 1 or more continuously flooded plates.
Explorer
Can move diagonally or remove water.
