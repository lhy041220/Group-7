import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class ForbiddenIslandGame {

    public void setupGame() {
        // ... existing code ...

        mainFrame.onGiveCardButtonClick.addListener(sender -> {
            Player player = game.getCurrentPlayer();
            List<Player> others = new ArrayList<>();
            for (Player p : game.getPlayers()) {
                if (p != player) {
                    if (player.getRole() != null && player.getRole().getDisplayName().equals("Messenger")) {
                        others.add(p);
                    } else if (p.getCurrentTile() == player.getCurrentTile()) {
                        others.add(p);
                    }
                }
            }
            List<model.card.TreasureCard> giveableCards = player.getGiveableTreasureCards();
            if (others.isEmpty() || giveableCards.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No available players or treasure cards to give!", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Player target = (Player) JOptionPane.showInputDialog(
                    mainFrame,
                    "Select a player to give a card to:",
                    "Give Card",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    others.toArray(),
                    others.get(0)
            );
            if (target == null) return;
            model.card.TreasureCard card = (model.card.TreasureCard) JOptionPane.showInputDialog(
                    mainFrame,
                    "Select a treasure card to give:",
                    "Give Card",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    giveableCards.toArray(),
                    giveableCards.get(0)
            );
            if (card == null) return;
            gameController.handleGiveCard(player, target, card);
        });
    }
} 