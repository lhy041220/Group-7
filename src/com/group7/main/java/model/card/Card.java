package model.card;

import model.enums.CardType;

import lombok.Getter;
import lombok.AllArgsConstructor;

/***
 * Basic card category
 */
@Getter
@AllArgsConstructor
public abstract class Card {
    private String name;
    private String description;
    private CardType type;
}