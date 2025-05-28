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

        PILOT("飞行员") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // 飞行员特殊能力：可以飞到任意板块（每回合限用一次）
            if (destinationTile != null && destinationTile.isNavigable()) {
                player.moveToTile(destinationTile);
            }
        }  
    },

    ENGINEER("工程师"){
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            if (destinationTile != null && destinationTile.isFlooded()) {
                Game game = Game.getInstance();
                game.getBoard().dryTile(destinationTile);

                // 打开对话框选择第二个要排水的板块
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

    NAVIGATOR("领航员") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // 领航员特殊能力：可以移动其他玩家最多两格
            // 注意：这里需要UI交互来选择目标玩家和移动位置
            Game game = Game.getInstance();

            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
                RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                    @Override
                    public void onTileSelected(Tile tile) {}

                    @Override
                    public void onPlayerSelected(Player targetPlayer) {
                        if (targetPlayer != null && destinationTile != null) {
                            targetPlayer.moveToTile(destinationTile);
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

    DIVER("潜水员") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // 潜水员特殊能力：可以穿过任意数量的相邻的被淹没或沉没的板块
            if (destinationTile != null) {
                Game game = Game.getInstance();
                List<Tile> reachableTiles = game.getBoard().getReachableTilesForDiver(player.getCurrentTile());

                if (reachableTiles.contains(destinationTile)) {
                    player.moveToTile(destinationTile);
                }
            }
        }  
    },

    MESSENGER("信使") {
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // 信使特殊能力：可以在任意位置将宝藏卡给其他玩家
            Game game = Game.getInstance();
            // 打开对话框选择玩家和卡牌
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

    EXPLORER("探险家") {
        @Override
        public void useSpecialAbility(Player player, Tile destinationTile) {
            // 探险家特殊能力：可以斜向移动，并且可以斜向排水
        // 探险家特殊能力：可以斜向移动，并且可以斜向排水
            if (destinationTile != null) {
                Game game = Game.getInstance();
                List<Tile> reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(player.getCurrentTile());

                if (reachableTiles.contains(destinationTile)) {
                    if (destinationTile.isFlooded()) {
                        // 如果目标板块是被淹没的，进行排水
                        game.getBoard().dryTile(destinationTile);
                    } else {
                        // 否则移动到目标板块
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

