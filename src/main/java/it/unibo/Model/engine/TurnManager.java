package it.unibo.Model.engine;

public class TurnManager {
    private boolean playerTurn = true;

    public void endTurn(final boolean blank, final boolean selfShot, final boolean opponentRestrained) {
        if (!(blank && selfShot) && !opponentRestrained) {
            playerTurn = !playerTurn;
        }
    }

    public void endTurn(final boolean blank, final boolean selfShot) {
        endTurn(blank, selfShot, false);
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }
}
