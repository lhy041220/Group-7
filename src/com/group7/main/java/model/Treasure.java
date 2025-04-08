package model;  

import model.enums.TreasureType;  

public class Treasure {  
    private final TreasureType type;  
    private boolean collected; // 是否已被收集  

    public Treasure(TreasureType type) {  
        this.type = type;  
        this.collected = false; // 默认状态未收集  
    }  

    public TreasureType getType() {  
        return type;  
    }  

    public boolean isCollected() {  
        return collected;  
    }  

    public void collect() {  
        if (!collected) {  
            collected = true; // 标记为已收集  
            System.out.println(type.getDisplayName() + " has been collected!");  
        } else {  
            System.out.println(type.getDisplayName() + " is already collected.");  
        }  
    }  
}  
