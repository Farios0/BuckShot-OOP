package it.unibo.Model;

import java.util.List;
import it.unibo.Model.Model.Target;
import it.unibo.Model.engine.*;
import it.unibo.Model.entities.Participant;
import it.unibo.Model.items.*;

class ModelTest {
    private static void check(boolean condition) {
        if (!condition) { throw new AssertionError(); }
    }
    @org.junit.jupiter.api.Test
    void verifiesShotEffectsTurnsSnapshotsAndGameOver() {
        var gun = new ShotgunImpl(List.of(true, false));
        gun.cut();
        check(gun.isSawed() && gun.isSawed());
        check(gun.getDamage() == 2 && gun.getDamage() == 2);
        check(gun.shoot().damage() == 2 && !gun.isSawed());
        check(gun.shoot().damage() == 0);
        try { gun.shoot(); throw new AssertionError(); }
        catch (IllegalStateException expected) { }
        var turns = new TurnManager();
        turns.endTurn(false, false);
        turns.endTurn(true, true);
        check(!turns.isPlayerTurn());
        var player = new Participant(4, 4);
        var dealer = new Participant(4, 4);
        var model = new ModelImpl(player, dealer,
            new ShotgunImpl(List.of(true, false, true, true)), new BasicStrategy());
        player.addObjects(List.of(new Saw(), new Handcuffs(), new MagnifyingGlass()));
        var snapshot = model.getGameData();
        check(model.utilizeObject(ItemType.MAGNIFYING_GLASS));
        check(model.inspectNextShell());
        check(model.utilizeObject(ItemType.SAW));
        check(model.utilizeObject(ItemType.HANDCUFFS));
        check(model.shoot(Target.OPPONENT) == 2);
        check(model.isPlayerTurn() && dealer.getState().lives() == 2);
        check(snapshot.dealer().lives() == 4 && snapshot.player().items().size() == 3);
        check(model.shoot(Target.SELF) == 0 && model.isPlayerTurn());
        check(model.shoot(Target.OPPONENT) == 1 && !model.isPlayerTurn());
        check(model.playDealerTurn() == 1 && model.isPlayerTurn());
        var terminal = new ModelImpl(new Participant(1, 1), new Participant(1, 1),
            new ShotgunImpl(List.of(true)), new BasicStrategy());
        terminal.shoot(Target.OPPONENT);
        check(terminal.gameIsOver() && terminal.getGameData().winner() == GameState.Winner.PLAYER);
        check(terminal.getGameData().shotgun().liveShells() == 0);
        try { terminal.shoot(Target.SELF); throw new AssertionError(); }
        catch (IllegalStateException expected) { }

    }
}
