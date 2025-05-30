package model.card;
import model.Player;
import model.Tile;
import model.Game;
import view.dialog.HelicopterLiftDialog;

import javax.swing.*;
import java.util.List;

public class HelicopterLiftCard extends SpecialCard {
    public HelicopterLiftCard() {
        super("Helicopter rescue", "Move any number of players from any plate to another plate");
    }

    @Override
    public void useCard(Player player) {
        Game game = Game.getInstance();

        // Open the dialog box and let the player select the target board and the player to move
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
            HelicopterLiftDialog dialog = new HelicopterLiftDialog(frame, player, new HelicopterLiftDialog.ActionListener() {
                @Override
                public void onPlayersSelected(List<Player> selectedPlayers, Tile destinationTile) {
                    // Move all the selected players to the target board
                    for (Player p : selectedPlayers) {
                        p.moveToTile(destinationTile);
                    }
                }
                @Override
                public void onActionCancelled() {
                    // Do nothing when canceling the operation
                }
            });
            dialog.setVisible(true);
        });
    }
}