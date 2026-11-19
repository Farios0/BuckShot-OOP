package it.unibo.Controller;

import java.util.Objects;
import it.unibo.Model.GameState;
import it.unibo.Model.Model;
import it.unibo.Model.ShotOutcome;
import it.unibo.Model.engine.Shell;
import it.unibo.Model.items.ItemResult;
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
                case SHOOT_SELF -> reportShot(true, model.shoot(Model.Target.SELF));
                case SHOOT_OPPONENT -> reportShot(true, model.shoot(Model.Target.OPPONENT));
                case USE_OBJECT -> useItem(action.item());
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showError(exception.getMessage());
        }
        refresh();
    }

    private void useItem(final ItemType item) {
        switch (model.utilizeObject(item)) {
            case ItemResult.NotUsable() -> view.showError("Questo oggetto non può essere usato adesso.");
            case ItemResult.ShellInspected(final Shell shell) ->
                view.showMessage("Prossima cartuccia: " + shellLabel(shell) + ".");
            case ItemResult.ShellEjected(final Shell shell) ->
                view.showMessage("Cartuccia espulsa: " + shellLabel(shell) + ".");
            case ItemResult.Healed() -> view.showMessage("Hai recuperato una vita.");
            case ItemResult.ShotgunSawed() -> view.showMessage("Canna segata: il prossimo colpo fa danno doppio.");
            case ItemResult.OpponentRestrained() -> view.showMessage("Avversario ammanettato.");
        }
    }

    private static String shellLabel(final Shell shell) {
        return shell.isLive() ? "carica" : "a salve";
    }

    private void reportShot(final boolean playerActed, final ShotOutcome outcome) {
        final String subject = playerActed
            ? (outcome.target() == Model.Target.SELF ? "Hai sparato a te stesso" : "Hai sparato al dealer")
            : (outcome.target() == Model.Target.SELF ? "Il dealer ha sparato a sé stesso" : "Il dealer ti ha sparato");
        final String impact = outcome.isLive()
            ? ": danno " + outcome.damage() + "."
            : ": cartuccia a salve.";
        final String reloadNote = outcome.reloaded() ? " Fucile ricaricato e nuovi oggetti distribuiti." : "";
        view.showMessage(subject + impact + reloadNote);
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
                reportShot(false, model.playDealerTurn());
            } catch (IllegalArgumentException | IllegalStateException exception) {
                stop();
                view.showError("Turno del dealer interrotto: " + exception.getMessage());
                return;
            }
        }
        refresh();
    }
}
