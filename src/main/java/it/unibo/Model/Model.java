package it.unibo.Model;

import it.unibo.Model.items.ItemResult;
import it.unibo.Model.items.ItemType;

public interface Model {
    enum Target { SELF, OPPONENT }
    ShotOutcome shoot(Target target);
    ItemResult utilizeObject(ItemType item);
    boolean gameIsOver();
    boolean isPlayerTurn();
    GameState getGameData();
    ShotOutcome playDealerTurn();
}
