package it.unibo.Controller;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.unibo.Model.GameState;
import it.unibo.Model.ModelImpl;
import it.unibo.Model.engine.BasicStrategy;
import it.unibo.Model.engine.Shell;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.entities.ParticipantImpl;
import it.unibo.Model.items.Beer;
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
        var model = new ModelImpl(new ParticipantImpl(4, 4), new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK, Shell.BLANK)), new BasicStrategy());
        var controller = new Controller(model, view);
        controller.start();
        controller.onActionChosen(Action.shootOpponent());
        assertNotNull(view.pending);
        controller.onActionChosen(Action.shootSelf());
        assertEquals(1, view.errors);
        assertEquals(2, view.state.shotgun().blankShells());
        view.advance();
        assertEquals("Il dealer ha sparato a sé stesso: cartuccia a salve.", view.message);
        assertFalse(view.state.playerTurn());
        assertNotNull(view.pending);
        controller.stop();
        assertNull(view.pending);
    }

    @Test
    void blankSelfShotKeepsPlayerTurn() {
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(new ParticipantImpl(4, 4), new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.BLANK, Shell.LIVE)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.shootSelf());
        assertTrue(view.state.playerTurn());
        assertNull(view.pending);
        assertEquals(4, view.state.player().lives());
    }

    @Test
    void gameOverStopsDealerAndFurtherActions() {
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(new ParticipantImpl(1, 1), new ParticipantImpl(1, 1),
            new ShotgunImpl(List.of(Shell.LIVE)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.shootOpponent());
        assertEquals(GameState.Winner.PLAYER, view.winner);
        assertNull(view.pending);
        controller.onActionChosen(Action.shootSelf());
        assertEquals(1, view.state.player().lives());
    }

    @Test
    void magnifyingGlassRevealsShellAndRemovesItem() {
        var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new MagnifyingGlass()));
        var view = new FakeView();
        var controller = new Controller(new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.useItem(ItemType.MAGNIFYING_GLASS));
        assertTrue(view.state.player().items().isEmpty());
        assertEquals("Prossima cartuccia: carica.", view.message);
        controller.onActionChosen(Action.useItem(ItemType.MAGNIFYING_GLASS));
        assertEquals(1, view.errors);
    }

    @Test
    void beerEjectsShellUpdatesCountAndDisplaysResult() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new Beer()));
        final var view = new FakeView();
        final var controller = new Controller(new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.useItem(ItemType.BEER));
        assertTrue(view.state.player().items().isEmpty());
        assertEquals("Cartuccia espulsa: carica.", view.message);
        assertEquals(0, view.state.shotgun().liveShells());
        assertEquals(1, view.state.shotgun().blankShells());
    }

    @Test
    void shotOutcomeReportsReloadAndDealerShotToPlayer() {
        final var view = new FakeView();
        final var controller = new Controller(new ModelImpl(new ParticipantImpl(4, 4), new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.LIVE)), new BasicStrategy()), view);
        controller.start();
        controller.onActionChosen(Action.shootOpponent());
        assertEquals("Hai sparato al dealer: danno 1.", view.message);
        view.advance();
        assertEquals("Il dealer ti ha sparato: danno 1. Fucile ricaricato e nuovi oggetti distribuiti.", view.message);
        assertEquals(6, view.state.shotgun().liveShells() + view.state.shotgun().blankShells());
        assertTrue(view.state.playerTurn());
    }
}
