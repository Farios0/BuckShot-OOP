package it.unibo.Controller;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.unibo.Model.GameState;
import it.unibo.Model.ModelImpl;
import it.unibo.Model.engine.BasicStrategy;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.entities.Participant;
import it.unibo.Model.items.MagnifyingGlass;
import it.unibo.Model.items.ItemType;
import it.unibo.View.View;

class ControllerTest {
    private static final class FakeView implements View {
        private Runnable pending;
        private GameState state;
        private GameState.Winner winner;
        private String message;
        private int errors;
        public void setInputHandler(InputHandler handler) { }
        public void display(GameState state) { this.state = state; }
        public void showMessage(String message) { this.message = message; }
        public void showError(String message) { errors++; }
        public void showGameOver(GameState.Winner winner) { this.winner = winner; }
        public void scheduleDealerAction(Runnable action) {
            assertNull(pending);
            pending = action;
        }
        public void cancelPendingActions() { pending = null; }
        void advance() {
            Runnable action = pending;
            pending = null;
            assertNotNull(action);
            action.run();
        }
    }

    @Test
    void dealerRetainsTurnOnBlankSelfShotAndPlayerCannotInterrupt() {
        var view = new FakeView();
        var model = new ModelImpl(new Participant(4, 4), new Participant(4, 4),
            new ShotgunImpl(List.of(true, false, false)), new BasicStrategy());
        var controller = new Controller(model, view);
        controller.start();
        controller.onActionChosen(Action.shootOpponent());
        assertNotNull(view.pending);
        controller.onActionChosen(Action.shootSelf());
        assertEquals(1, view.errors);
        assertEquals(2, view.state.shotgun().blankShells());
        view.advance();
        assertFalse(view.state.playerTurn());
        assertNotNull(view.pending);
        controller.stop();
        assertNull(view.pending);
    }

    @Test
    void blankSelfShotKeepsPlayerTurn() {
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(new Participant(4, 4), new Participant(4, 4),
            new ShotgunImpl(List.of(false, true)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.shootSelf());
        assertTrue(view.state.playerTurn());
        assertNull(view.pending);
        assertEquals(4, view.state.player().lives());
    }

    @Test
    void gameOverStopsDealerAndFurtherActions() {
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(new Participant(1, 1), new Participant(1, 1),
            new ShotgunImpl(List.of(true)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.shootOpponent());
        assertEquals(GameState.Winner.PLAYER, view.winner);
        assertNull(view.pending);
        controller.onActionChosen(Action.shootSelf());
        assertEquals(1, view.state.player().lives());
    }

    @Test
    void magnifyingGlassRevealsShellAndRemovesItem() {
        var player = new Participant(4, 4);
        player.addObjects(List.of(new MagnifyingGlass()));
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(player, new Participant(4, 4),
            new ShotgunImpl(List.of(true, false)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.useItem(ItemType.MAGNIFYING_GLASS));
        assertTrue(view.state.player().items().isEmpty());
        assertEquals("Prossima cartuccia: carica.", view.message);
        controller.onActionChosen(Action.useItem(ItemType.MAGNIFYING_GLASS));
        assertEquals(1, view.errors);
    }
}
