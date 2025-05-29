package model.card;

import model.Player;
import model.Tile;
import model.Game;

import view.dialog.RoleActionDialog;

import javax.swing.*;

public class SandbagCard extends SpecialCard {
    public SandbagCard() {
        super("沙袋", "移除任意一个板块上的水");
        this.canBeUsedAfterFlood = false;  // 沙袋卡不能在看到洪水卡后使用
    }

    @Override
    public void useCard(Player player) {
        // 沙袋卡可以在任何时候使用，用于排干任意一个板块的水
        // 注意：根据规则，不能在看到洪水卡后使用沙袋卡
        Game game = Game.getInstance();

        // 打开对话框让玩家选择要排水的板块
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
            RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                @Override
                public void onTileSelected(Tile tile) {
                    if (tile != null && tile.isFlooded()) {
                        tile.shoreUp(); // 使用正确的方法名
                        game.getMainFrame().updateBoard(game.getBoard()); // 更新游戏板显示
                    }
                }

                @Override
                public void onPlayerSelected(Player targetPlayer) {
                    // 不需要选择玩家
                }

                @Override
                public void onCardSelected(TreasureCard card) {
                    // 不需要选择卡牌
                }

                @Override
                public void onActionCancelled() {
                    // 取消行动时不做任何事
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * 使用沙袋卡排干指定板块的水
     * @param tile 目标板块
     * @return 是否成功使用
     */
    public boolean useOnTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            Game.getInstance().getBoard().dryTile(tile);
            return true;
        }
        return false;
    }
}