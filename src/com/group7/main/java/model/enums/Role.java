package model.enums;

import model.Tile;  
import model.Player;
import model.Game;
import model.RoleAbility;
import model.card.TreasureCard;
import view.dialog.RoleActionDialog;
import javax.swing.*;
import java.util.List;

public enum Role implements RoleAbility {

        PILOT("Pilot") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // Pilot's special ability: Can fly to any plate (limited to once per round)
            if (destinationTile != null && destinationTile.isNavigable()) {
                player.moveToTile(destinationTile);
            }
        }  
    },

    ENGINEER("Engineer"){
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            if (destinationTile != null && destinationTile.isFlooded()) {
                Game game = Game.getInstance();
                game.getBoard().dryTile(destinationTile);

                // Open the dialog box and select the second section you want to drain
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
                    RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                        @Override
                        public void onTileSelected(Tile secondTile) {
                            if (secondTile != null && secondTile.isFlooded()) {
                                game.getBoard().dryTile(secondTile);
                            }
                        }

                        @Override
                        public void onPlayerSelected(Player targetPlayer) {}

                        @Override
                        public void onCardSelected(TreasureCard card) {}

                        @Override
                        public void onActionCancelled() {}
                    });
                    dialog.setVisible(true);
                });
            }
        }  
    },

    NAVIGATOR("Navigator") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // Navigator's special ability: Can move other players up to two Spaces
            // Note: Here, UI interaction is required to select the target player and move the position
            Game game = Game.getInstance();

            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
                RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                    @Override
                    public void onTileSelected(Tile tile) {}

                    @Override
                    public void onPlayerSelected(Player targetPlayer) {
                        if (targetPlayer != null) {
                            // 连续两次弹窗选择目标格子
                            for (int i = 0; i < 2; i++) {
                                List<Tile> movable = game.getBoard().getMovableTilesForPlayer(targetPlayer);
                                if (movable.isEmpty()) break;
                                Tile dest = (Tile) JOptionPane.showInputDialog(
                                        frame,
                                        "Navigator: Select tile to move Player " + targetPlayer.getPlayerId() + " (step " + (i+1) + "):",
                                        "Navigator Move",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        movable.toArray(),
                                        movable.get(0)
                                );
                                if (dest != null && targetPlayer.canMoveTo(dest)) {
                                    targetPlayer.moveToTile(dest);
                                } else {
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCardSelected(TreasureCard card) {}

                    @Override
                    public void onActionCancelled() {}
                });
                dialog.setVisible(true);
            });
        }
    },

    DIVER("Diver") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // Diver's special ability: Can pass through any number of adjacent submerged or sunken plates
            if (destinationTile != null) {
                Game game = Game.getInstance();
                List<Tile> reachableTiles = game.getBoard().getReachableTilesForDiver(player.getCurrentTile());

                if (reachableTiles.contains(destinationTile)) {
                    player.moveToTile(destinationTile);
                }
            }
        }  
    },

    MESSENGER("Messenger") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // Messenger's special ability: Can give treasure cards to other players at any position
            Game game = Game.getInstance();
            // Open the dialog box and select the player and the card
            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
                RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                    private Player selectedPlayer;

                    @Override
                    public void onTileSelected(Tile tile) {}

                    @Override
                    public void onPlayerSelected(Player targetPlayer) {
                        this.selectedPlayer = targetPlayer;
                    }

                    @Override
                    public void onCardSelected(TreasureCard card) {
                        if (selectedPlayer != null && card != null) {
                            player.giveCardToPlayer(selectedPlayer, card);
                        }
                    }

                    @Override
                    public void onActionCancelled() {}
                });
                dialog.setVisible(true);
            });
        }
    },

    EXPLORER("Explorer") {
        @Override
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // Explorer's special ability: Can move diagonally and drain water diagonally
            if (destinationTile != null) {
                Game game = Game.getInstance();
                List<Tile> reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(player.getCurrentTile());

                if (reachableTiles.contains(destinationTile)) {
                    if (destinationTile.isFlooded()) {
                        // If the target plate is submerged, carry out drainage
                        game.getBoard().dryTile(destinationTile);
                    } else {
                        // Otherwise, move to the target section
                        player.moveToTile(destinationTile);
                    }
                }
            }
        }
    };

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}  

