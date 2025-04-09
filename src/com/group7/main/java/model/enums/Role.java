package model.enums;

import model.Tile;  
import model.Player; 

public enum Role {  
    PILOT {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Pilot 特殊能力：飞行到任意板块  
            // player.setCurrentTile(destinationTile);
            // System.out.println("Pilot has flown to " + destinationTile.getName());
        }  
    },  
    ENGINEER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Engineer 特殊能力：一次翻转两个淹水板块  
            System.out.println("Engineer can shore up two tiles in one action.");  
            // 这里可以具体实现连接到翻转逻辑  
        }  
    },  
    NAVIGATOR {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Navigator 特殊能力：移动其他玩家  
            // 具体实现取决于如何选择目标玩家，这里略作展示  
            System.out.println("Navigator can move another player.");  
        }  
    },  
    DIVER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Diver 特殊能力：穿越多个空板块到达目的地  
            // player.setCurrentTile(destinationTile);
            // System.out.println("Diver has dived to " + destinationTile.getName());
        }  
    },  
    MESSENGER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Messenger 特殊能力：跨板块传递宝藏卡  
            System.out.println("Messenger can give a treasure card without being on the same tile.");  
        }  
    },  
    EXPLORER {  
        @Override  
        public void useSpecialAbility(Player player, Tile destinationTile) {  
            // Explorer 特殊能力：对角移动或翻转  
            System.out.println("Explorer can move diagonally.");  
        }  
    };  

    public abstract void useSpecialAbility(Player player, Tile destinationTile);  
}  

