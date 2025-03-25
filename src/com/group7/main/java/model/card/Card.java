package model.card;

import model.enums.CardType;

import lombok.Getter;
import lombok.AllArgsConstructor;

/***
 * 基础卡牌类
 */
@Getter
@AllArgsConstructor
public abstract class Card {
    private String name;
    private String description;
    private CardType type;
}