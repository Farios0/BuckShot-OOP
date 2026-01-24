package it.unibo.Model.engine;

public class TurnManager {
    private enum Turn {
        PLAYER,
        DEALER;
    }
    private Turn currentTurn;

    TurnManager() {
        currentTurn = Turn.PLAYER;
    }

    public void endTurn(boolean blank, boolean selfShot) {
        if (currentTurn.equals(Turn.PLAYER)) {
            if (blank && selfShot) {
                return;
            }
            currentTurn = Turn.DEALER;
            return;
        }
        currentTurn = Turn.PLAYER;
    }

    public boolean isPlayerTurn() {
        return currentTurn.equals(Turn.PLAYER);
    }
}
