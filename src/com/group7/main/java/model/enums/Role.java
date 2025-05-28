package model.enums;

import model.Tile;  
import model.Player;
import model.Game;
import java.util.List;

public enum Role {  
    PILOT {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 飞行员特殊能力：一回合一次，可以飞行到任意板块
            if (destinationTile != null && destinationTile.isNavigable()) {
                player.moveToTile(destinationTile);
            }
        }  
    },  
    ENGINEER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 工程师特殊能力：可以用一个行动点翻转两个淹水板块
            if (destinationTile != null && destinationTile.isFlooded()) {
                destinationTile.shoreUp();
                
                // 工程师可额外翻转一个板块，但需要玩家再次选择
                // 注意：这里简化处理，实际游戏中需要UI交互让玩家选择第二个板块
                // 此处逻辑仅作示范，完整实现需要与UI交互
            }
        }  
    },  
    NAVIGATOR {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 领航员特殊能力：可以移动另一名玩家1或2格
            Game game = Game.getInstance();
            List<Player> players = game.getPlayers();
            
            // 这里简化处理，实际游戏中需要UI交互让玩家选择要移动的玩家和目标板块
            // 搜索除当前玩家外的其他玩家
            for (Player otherPlayer : players) {
                if (otherPlayer != player && destinationTile != null && destinationTile.isNavigable()) {
                    // 可以移动其他玩家到目标板块（实际游戏中需要检查是否在1-2步范围内）
                    otherPlayer.moveToTile(destinationTile);
                    break;
                }
            }
        }  
    },  
    DIVER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 潜水员特殊能力：可以穿过一个或多个相邻的已沉没或已淹没板块移动
            if (destinationTile != null && destinationTile.isNavigable()) {
                // 潜水员可以通过已沉没的板块到达目标
                // 实际游戏中需要路径验证算法检查从当前位置是否可以通过沉没板块到达目标
                player.moveToTile(destinationTile);
            }
        }  
    },  
    MESSENGER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 信使特殊能力：可以在不相邻的情况下将宝藏卡给予其他玩家
            Game game = Game.getInstance();
            List<Player> players = game.getPlayers();
            
            // 这里简化处理，实际游戏中需要UI交互让玩家选择要给予的卡牌和目标玩家
            // 信使可以跨距离交换卡牌，所以不需要检查位置是否相邻
            // 具体的卡牌交换逻辑需要在UI层实现
        }  
    },  
    EXPLORER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // 探险家特殊能力：可以对角线移动或对角线翻转
            // 在游戏中，通常移动和翻转只能在正交方向（上下左右）进行
            // 探险家可以额外在对角线方向移动或翻转
            
            if (destinationTile != null) {
                // 检查目标板块是否为对角线方向（实际游戏需要有具体的坐标计算）
                int rowDiff = Math.abs(player.getCurrentTile().getRow() - destinationTile.getRow());
                int colDiff = Math.abs(player.getCurrentTile().getCol() - destinationTile.getCol());
                
                // 对角线移动（行差和列差都为1）
                if (rowDiff == 1 && colDiff == 1 && destinationTile.isNavigable()) {
                    player.moveToTile(destinationTile);
                }
                // 对角线翻转
                else if (rowDiff == 1 && colDiff == 1 && destinationTile.isFlooded()) {
                    destinationTile.shoreUp();
                }
            }
        }  
    };  

    public abstract void useSpecialAbility(Player player, Tile destinationTile);  
}  

