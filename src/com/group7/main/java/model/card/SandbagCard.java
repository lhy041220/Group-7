package model.card;

import model.Player;
import model.Tile;
import model.Game;

import view.dialog.RoleActionDialog;

import javax.swing.*;

public class SandbagCard extends SpecialCard {
    public SandbagCard() {
        super("Sandbag", "Remove the water on any one of the plates");
        this.canBeUsedAfterFlood = false;  // Sandbag cards cannot be used after seeing flood cards
    }

    @Override
    public void useCard(Player player) {
        // The sandbag card can be used at any time to drain the water from any plate
        // Note: According to the rules, sandbag cards cannot be used after seeing flood cards
        Game game = Game.getInstance();

        // Open the dialog box and let the player select the section to drain
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(game.getMainFrame());
            RoleActionDialog dialog = new RoleActionDialog(frame, player, new RoleActionDialog.ActionListener() {
                @Override
                public void onTileSelected(Tile tile) {
                    if (tile != null && tile.isFlooded()) {
                        tile.shoreUp(); // Use the correct method name
                        game.getMainFrame().updateBoard(game.getBoard());
                    }
                }

                @Override
                public void onPlayerSelected(Player targetPlayer) {
                    // There is no need to select players
                }

                @Override
                public void onCardSelected(TreasureCard card) {
                    // There is no need to select cards
                }

                @Override
                public void onActionCancelled() {
                    // Do nothing when canceling the operation
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * Use sandbag cards to drain the water from the designated sections
     * @param tile Target section
     * @return Whether was used successfully
     */
    public boolean useOnTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            Game.getInstance().getBoard().dryTile(tile);
            return true;
        }
        return false;
    }
}