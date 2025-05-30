package view.dialog;

import model.Player;
import model.card.Card;
import model.card.SpecialCard;

import javax.swing.*;
import java.awt.*;

public class DiscardDialog extends JDialog {
    private Player player;
    private JList<Card> cardList;
    private ActionListener actionListener;

    public interface ActionListener {
        void onCardDiscarded(Card card);
        void onSpecialCardUsed(SpecialCard card);
    }

    public DiscardDialog(JFrame parent, Player player, ActionListener listener) {
        super(parent, "Hand Limit Exceeded - Please Discard", true);
        this.player = player;
        this.actionListener = listener;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Show current hand size
        JLabel infoLabel = new JLabel(
                String.format("Current hand: %d cards (Maximum: 5)", player.getHand().size()),
                SwingConstants.CENTER
        );
        add(infoLabel, BorderLayout.NORTH);

        // Create a list of cards in hand
        cardList = new JList<>(player.getHand().toArray(new Card[0]));
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cardList);
        add(scrollPane, BorderLayout.CENTER);

        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Discard button
        JButton discardButton = new JButton("Discard Selected Card");
        discardButton.addActionListener(e -> {
            Card selectedCard = cardList.getSelectedValue();
            if (selectedCard != null) {
                actionListener.onCardDiscarded(selectedCard);
                updateCardList();
                // If hand no longer exceeds limit, close dialog
                if (!player.handExceedsLimit()) {
                    dispose();
                }
            }
        });

        // Use special card button
        JButton useSpecialButton = new JButton("Use Special Card");
        useSpecialButton.addActionListener(e -> {
            Card selectedCard = cardList.getSelectedValue();
            if (selectedCard instanceof SpecialCard) {
                actionListener.onSpecialCardUsed((SpecialCard)selectedCard);
                updateCardList();
                // If hand no longer exceeds limit, close dialog
                if (!player.handExceedsLimit()) {
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a special card (Helicopter or Sandbag) to use.",
                        "Invalid Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(useSpecialButton);
        buttonPanel.add(discardButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog size
        setSize(400, 300);
    }

    private void updateCardList() {
        cardList.setListData(player.getHand().toArray(new Card[0]));
    }
}