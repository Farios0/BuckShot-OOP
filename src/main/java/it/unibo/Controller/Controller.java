package it.unibo.Controller;

import java.util.Objects;
import it.unibo.Model.GameState;
import it.unibo.Model.Model;
import it.unibo.Model.items.ItemType;
import it.unibo.View.View;

/** Coordinates a passive view and the model on the UI event thread. */
public final class Controller implements InputHandler {
    private final Model model;
    private final View view;
    private boolean running;
    private boolean dealerPending;

    public Controller(final Model model, final View view) {
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
    }

    public void start() {
        if (running) { return; }
        running = true;
        view.setInputHandler(this);
        view.showMessage("Scegli se sparare o usare un oggetto.");
        refresh();
    }

    public void stop() {
        running = false;
        dealerPending = false;
        view.cancelPendingActions();
    }

    @Override
    public void onActionChosen(final Action action) {
        Objects.requireNonNull(action);
        if (!running || model.gameIsOver()) { return; }
        if (dealerPending || !model.isPlayerTurn()) {
            view.showError("Attendi il turno del dealer.");
            return;
        }
        try {
            switch (action.type()) {
                case SHOOT_SELF -> reportShot("Hai sparato a te stesso", model.shoot(Model.Target.SELF));
                case SHOOT_OPPONENT -> reportShot("Hai sparato al dealer", model.shoot(Model.Target.OPPONENT));
                case USE_OBJECT -> useItem(action.item());
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showError(exception.getMessage());
        }
        refresh();
    }

    private void useItem(final ItemType item) {
        if (!model.utilizeObject(item)) {
            view.showError("Questo oggetto non può essere usato adesso.");
        } else if (item == ItemType.MAGNIFYING_GLASS) {
            view.showMessage(model.inspectNextShell()
                ? "Prossima cartuccia: carica." : "Prossima cartuccia: a salve.");
        } else {
            view.showMessage("Oggetto utilizzato.");
        }
    }

    private void reportShot(final String subject, final int damage) {
        view.showMessage(subject + (damage == 0 ? ": cartuccia a salve." : ": danno " + damage + "."));
    }

    private void refresh() {
        final GameState state = model.getGameData();
        view.display(state);
        if (state.winner() != GameState.Winner.NONE) {
            stop();
            view.showGameOver(state.winner());
        } else if (running && !state.playerTurn() && !dealerPending) {
            dealerPending = true;
            view.scheduleDealerAction(this::playDealerAction);
        }
    }

    private void playDealerAction() {
        if (!running || !dealerPending) { return; }
        dealerPending = false;
        if (!model.gameIsOver() && !model.isPlayerTurn()) {
            try {
                reportShot("Il dealer ha sparato", model.playDealerTurn());
            } catch (IllegalArgumentException | IllegalStateException exception) {
                stop();
                view.showError("Turno del dealer interrotto: " + exception.getMessage());
                return;
            }
        }
        refresh();
    }
}
