package model.card;
import model.Player;
import model.Tile;
import model.Game;
import view.dialog.HelicopterLiftDialog;

import javax.swing.*;
import java.util.List;

public class HelicopterLiftCard extends SpecialCard {
    public HelicopterLiftCard() {
        super("直升机救援", "将任意数量的玩家从任意板块移动到另一个板块");
    }

    @Override
    public void useCard(Player player) {
        Game game = Game.getInstance();

        // 打开对话框让玩家选择目标板块和要移动的玩家
        // 打开对话框让玩家选择目标板块和要移动的玩家
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
            HelicopterLiftDialog dialog = new HelicopterLiftDialog(frame, player, new HelicopterLiftDialog.ActionListener() {
                @Override
                public void onPlayersSelected(List<Player> selectedPlayers, Tile destinationTile) {
                    // 移动所有选中的玩家到目标板块
                    for (Player p : selectedPlayers) {
                        p.moveToTile(destinationTile);
                    }
                }
                @Override
                public void onActionCancelled() {
                    // 取消行动时不做任何事
                }
            });
            dialog.setVisible(true);
        });
    }
}