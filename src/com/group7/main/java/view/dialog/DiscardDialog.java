package view.dialog;

import model.Player;
import model.card.Card;
import model.card.SpecialCard;
import model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class DiscardDialog extends JDialog {
    private Player player;
    private JList<Card> cardList;
    private ActionListener actionListener;

    public interface ActionListener {
        void onCardDiscarded(Card card);
        void onSpecialCardUsed(SpecialCard card);
    }

    public DiscardDialog(JFrame parent, Player player, ActionListener listener) {
        super(parent, "超出手牌上限 - 请弃牌", true);
        this.player = player;
        this.actionListener = listener;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 显示当前手牌数量
        JLabel infoLabel = new JLabel(
                String.format("当前手牌: %d张 (上限5张)", player.getHand().size()),
                SwingConstants.CENTER
        );
        add(infoLabel, BorderLayout.NORTH);

        // 创建手牌列表
        cardList = new JList<>(player.getHand().toArray(new Card[0]));
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cardList);
        add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 弃牌按钮
        JButton discardButton = new JButton("弃掉选中的牌");
        discardButton.addActionListener(e -> {
            Card selectedCard = cardList.getSelectedValue();
            if (selectedCard != null) {
                actionListener.onCardDiscarded(selectedCard);
                updateCardList();
                // 如果手牌数量不再超出上限，关闭对话框
                if (!player.handExceedsLimit()) {
                    dispose();
                }
            }
        });

        // 使用特殊卡按钮
        JButton useSpecialButton = new JButton("使用特殊卡");
        useSpecialButton.addActionListener(e -> {
            Card selectedCard = cardList.getSelectedValue();
            if (selectedCard instanceof SpecialCard) {
                actionListener.onSpecialCardUsed((SpecialCard)selectedCard);
                updateCardList();
                // 如果手牌数量不再超出上限，关闭对话框
                if (!player.handExceedsLimit()) {
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "请选择一张特殊卡（直升机或沙袋）使用",
                        "无效的选择",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(useSpecialButton);
        buttonPanel.add(discardButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框大小
        setSize(400, 300);
    }

    private void updateCardList() {
        cardList.setListData(player.getHand().toArray(new Card[0]));
    }
}