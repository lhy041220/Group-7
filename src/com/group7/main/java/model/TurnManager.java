package model;

import lombok.Getter;
import model.card.*;
import model.enums.CardType;
import model.enums.GameState;
import model.enums.Role;
import model.enums.TreasureType;

import java.util.List;
import java.util.ArrayList;

// 回合管理
public class TurnManager {
    private Game game;
    private List<Player> players;

    @Getter
    private int roundNumber;
    @Getter
    private TurnPhase currentPhase;

    public enum TurnPhase {
        ACTIONS,         // 玩家行动阶段
        DRAW_TREASURE,   // 抽取宝藏卡阶段
        DRAW_FLOOD,      // 抽取洪水卡阶段
        END              // 回合结束阶段
    }

    public enum ActionType {
        MOVE,           // 移动
        SHORE_UP,       // 加固
        GIVE_CARD,      // 给予卡牌
        CAPTURE_TREASURE // 获取宝藏
    }

    public TurnManager(Game game, List<Player> players) {
        this.game = game;
        this.players = players;
        this.roundNumber = 1;
        this.currentPhase = TurnPhase.ACTIONS;
    }

    public Player getCurrentPlayer() {
        return players.get(game.getCurrentPlayerIndex());
    }

    // 开始一个玩家的回合
    public void startPlayerTurn() {
        currentPhase = TurnPhase.ACTIONS;
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.resetActionsForTurn();
        game.notifyTurnStarted(currentPlayer);
    }

    // 玩家执行行动
    public boolean performAction(ActionType actionType, Object... params) {
        Player currentPlayer = getCurrentPlayer();
        boolean actionSuccessful = false;

        if (currentPlayer.getRemainingActions() <= 0) {
            return false;
        }

        switch (actionType) {
            case MOVE:
                if (params.length >= 1 && params[0] instanceof Tile) {
                    Tile destinationTile = (Tile) params[0];
                    actionSuccessful = movePlayer(currentPlayer, destinationTile);
                }
                break;

            case SHORE_UP:
                if (params.length >= 1 && params[0] instanceof Tile) {
                    Tile tileToShoreUp = (Tile) params[0];
                    actionSuccessful = shoreUpTile(currentPlayer, tileToShoreUp);
                }
                break;

            case GIVE_CARD:
                if (params.length >= 2 && params[0] instanceof Player && params[1] instanceof Card) {
                    Player targetPlayer = (Player) params[0];
                    Card cardToGive = (Card) params[1];
                    actionSuccessful = giveCard(currentPlayer, targetPlayer, cardToGive);
                }
                break;
        }

        if (actionSuccessful) {
            currentPlayer.useAction();
            // 如果玩家用完了行动点，自动进入下一阶段
            if (currentPlayer.getRemainingActions() <= 0) {
                nextPhase();
            }
            // 通知游戏状态改变
            game.notifyTurnStarted(currentPlayer);
        }

        return actionSuccessful;
    }

    // 移动玩家
    private boolean movePlayer(Player player, Tile destinationTile) {
        if (destinationTile == null || destinationTile.isSunk()) {
            return false;
        }

        Tile currentTile = player.getCurrentTile();
        List<Tile> reachableTiles;

        // 根据角色获取可到达的板块
        if (player.getRole() == Role.EXPLORER) {
            reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(currentTile);
        } else if (player.getRole() == Role.DIVER) {
            reachableTiles = game.getBoard().getReachableTilesForDiver(currentTile);
        } else {
            reachableTiles = game.getBoard().getAdjacentTiles(currentTile);
        }

        if (reachableTiles.contains(destinationTile)) {
            return player.moveToTile(destinationTile);
        }

        return false;
    }

    // 加固板块
    private boolean shoreUpTile(Player player, Tile tile) {
        if (tile == null || !tile.isFlooded() || tile.isSunk()) {
            return false;
        }

        // 工程师可以一次行动加固两个板块
        if (player.getRole() == Role.ENGINEER) {
            // 工程师的特殊处理
            tile.shoreUp();
            return true;
        }

        // 探险家可以加固对角的板块
        if (player.getRole() == Role.EXPLORER) {
            List<Tile> reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(player.getCurrentTile());
            if (reachableTiles.contains(tile)) {
                game.getBoard().dryTile(tile);
                return true;
            }
        } else {
            // 其他角色只能加固相邻的板块
            List<Tile> reachableTiles = game.getBoard().getAdjacentTiles(player.getCurrentTile());
            if (reachableTiles.contains(tile)) {
                tile.shoreUp();
                return true;
            }
        }

        return false;
    }

    // 给予卡牌
    private boolean giveCard(Player fromPlayer, Player toPlayer, Card card) {
        // 信使可以在任何位置给卡
        if (fromPlayer.getRole() == Role.MESSENGER) {
            return fromPlayer.giveCardToPlayer(toPlayer, card);
        }

        // 其他角色需要在同一个板块
        if (fromPlayer.getCurrentTile() == toPlayer.getCurrentTile()) {
            return fromPlayer.giveCardToPlayer(toPlayer, card);
        }

        return false;
    }

    // 获取宝藏
    private boolean captureTreasure(Player player, Tile treasureTile) {
        if (!game.getBoard().canCaptureTreasure(treasureTile, player)) {
            return false;
        }

        // 找到4张对应的宝藏卡并弃掉
        TreasureType treasureType = treasureTile.getTreasure();
        List<TreasureCard> cardsToDiscard = new ArrayList<>();

        for (Card card : player.getHand()) {
            if (card instanceof TreasureCard) {
                TreasureCard treasureCard = (TreasureCard) card;
                if (treasureCard.getTreasureType() == treasureType) {
                    cardsToDiscard.add(treasureCard);
                    if (cardsToDiscard.size() == 4) {
                        break;
                    }
                }
            }
        }

        // 弃掉宝藏卡并添加宝藏到玩家收集列表中
        for (TreasureCard card : cardsToDiscard) {
            game.playerDiscardHandCard(player, card);
        }
        player.addCollectedTreasure(treasureType);

        // 触发宝藏获取事件
        for (GameEventListener listener : game.getEventListeners()) {
            listener.onTreasureCaptured(player, treasureType);
        }
        return true;
    }

    // 进入下一个阶段
    public void nextPhase() {
        switch (currentPhase) {
            case ACTIONS:
                currentPhase = TurnPhase.DRAW_TREASURE;
                drawTreasureCards();
                break;

            case DRAW_TREASURE:
                currentPhase = TurnPhase.DRAW_FLOOD;
                drawFloodCards();
                break;

            case DRAW_FLOOD:
                currentPhase = TurnPhase.END;
                endTurn();
                break;

            case END:
                // 切换到下一个玩家
                int nextIndex = (game.getCurrentPlayerIndex() + 1) % players.size();
                game.setCurrentPlayerIndex(nextIndex);
                if (nextIndex == 0) {
                    roundNumber++;
                }

                // 开始新玩家的回合
                currentPhase = TurnPhase.ACTIONS;  // 重置为行动阶段
                startPlayerTurn();
                break;
        }

        // 通知GameController阶段改变
        game.notifyPhaseChanged(currentPhase);
    }

    // 抽取宝藏卡
    private void drawTreasureCards() {
        Player currentPlayer = getCurrentPlayer();
        boolean waterRiseCardDrawn = false;

        // 抽两张宝藏卡
        for (int i = 0; i < 2; i++) {
            HandCard card = game.drawTreasureCard();

            // 如果抽到水位上升卡
            if (card instanceof WaterRiseCard) {
                if (waterRiseCardDrawn) {
                    // 第二张水位上升卡，只上升水位
                    game.increaseWaterLevel();
                } else {
                    // 第一张水位上升卡，执行完整效果
                    waterRiseCardDrawn = true;
                    ((WaterRiseCard) card).useCard(currentPlayer);
                }
                continue;
            }

            // 普通卡牌加入手牌
            if (card != null) {
                currentPlayer.addCardToHand(card);

                // 检查手牌上限
                if (currentPlayer.handExceedsLimit()) {
                    game.notifyPlayerMustDiscard(currentPlayer);
                    return; // 等待玩家弃牌后再继续
                }
            }
        }
        if (!currentPlayer.handExceedsLimit()) {
            nextPhase();
        }
    }

    // 抽取洪水卡
    private void drawFloodCards() {
        // 根据当前水位获取需要抽取的洪水卡数量
        int cardsToDrawCount = game.getWaterLevel().getFloodCardsCount();

        for (int i = 0; i < cardsToDrawCount; i++) {
            FloodCard card = game.getFloodDeck().drawCard();
            if (card != null) {
                Tile tileToFlood = game.getBoard().getTileByType(card.getTileType());

                if (tileToFlood != null) {
                    if (tileToFlood.isFlooded()) {
                        // 如果已经被淹没，则沉没
                        tileToFlood.sink();
                        // 检查玩家是否在沉没的板块上
                        checkPlayersOnSunkTile(tileToFlood);
                    } else {
                        // 如果未被淹没，则淹没
                        tileToFlood.flood();
                    }
                }

                // 将洪水卡放入弃牌堆
                game.getFloodDeck().discard(card);
            }
        }

        // 进入回合结束阶段
        nextPhase();
    }

    private void checkPlayersOnSunkTile(Tile sunkTile) {
        for (Player player : players) {
            if (player.getCurrentTile() == sunkTile) {
                // 如果是潜水员，可以游到相邻的板块
                if (player.getRole() == Role.DIVER) {
                    List<Tile> reachableTiles = game.getBoard().getReachableTilesForDiver(sunkTile);
                    if (!reachableTiles.isEmpty()) {
                        player.moveToTile(reachableTiles.get(0));
                        continue;
                    }
                }

                // 检查相邻的可用板块
                List<Tile> adjacentTiles = game.getBoard().getAdjacentTiles(sunkTile);
                List<Tile> availableTiles = new ArrayList<>();

                for (Tile tile : adjacentTiles) {
                    if (tile.isNavigable()) {
                        availableTiles.add(tile);
                    }
                }

                // 如果有可用的相邻板块，移动到第一个可用的板块
                if (!availableTiles.isEmpty()) {
                    player.moveToTile(availableTiles.get(0));
                } else {
                    // 如果没有可用的相邻板块，游戏结束
                    game.triggerGameOver("玩家在板块沉没时无处可逃！");
                }
            }
        }
    }

    // 结束当前回合
    private void endTurn() {
        // 检查游戏是否结束
        if (game.checkWinCondition()) {
            game.triggerGameOver("恭喜！你们成功收集了所有宝藏并逃离了岛屿！");
            return;
        }

        if (game.checkLoseCondition()) {
            return; // 游戏已经在checkLoseCondition中结束
        }

        // 进入下一个玩家的回合
        nextPhase();
    }

    // 立即结束当前玩家的回合（可用于某些特殊卡牌效果）
    public void forceEndTurn() {
        currentPhase = TurnPhase.END;
        endTurn();
    }

}
