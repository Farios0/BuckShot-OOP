package it.unibo.Model;

import it.unibo.Model.items.ItemType;

public interface Model {
    enum Target { SELF, OPPONENT }
    int shoot(Target target);
    boolean utilizeObject(ItemType item);
    boolean gameIsOver();
    void distributeObjects();
    boolean isPlayerTurn();
    GameState getGameData();
    boolean inspectNextShell();
    int playDealerTurn();
}
