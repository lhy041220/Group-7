package model;

import model.Tile;
import model.Player;

public interface RoleAbility {
    void useSpecialAbility(Player player, Tile destinationTile);
}