package it.unibo.View;

import it.unibo.Controller.InputHandler;
import it.unibo.Model.GameState;

/** UI operations needed by the controller, independent of JavaFX controls. */
public interface View {
    void setInputHandler(InputHandler handler);
    void display(GameState state);
    void showMessage(String message);
    void showError(String message);
    void showGameOver(GameState.Winner winner);
    /** Enqueue one action on the UI thread, without blocking it. */
    void scheduleDealerAction(Runnable action);
    void cancelPendingActions();
}
