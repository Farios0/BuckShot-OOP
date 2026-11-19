package it.unibo.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import it.unibo.Model.Model.Target;
import it.unibo.Model.engine.BasicStrategy;
import it.unibo.Model.engine.Shell;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.engine.TurnManager;
import it.unibo.Model.entities.ParticipantImpl;
import it.unibo.Model.items.Beer;
import it.unibo.Model.items.Cigarette;
import it.unibo.Model.items.Handcuffs;
import it.unibo.Model.items.ItemResult;
import it.unibo.Model.items.ItemType;
import it.unibo.Model.items.MagnifyingGlass;
import it.unibo.Model.items.Saw;

class ModelTest {
    @Test
    void verifiesShotEffectsTurnsSnapshotsAndGameOver() {
        final var gun = new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK));
        gun.cut();
        assertTrue(gun.isSawed());
        assertEquals(2, gun.getDamage());
        assertEquals(2, gun.shoot().damage());
        assertFalse(gun.isSawed());
        assertEquals(0, gun.shoot().damage());
        assertThrows(IllegalStateException.class, gun::shoot);

        final var turns = new TurnManager();
        turns.endTurn(false, false);
        turns.endTurn(true, true);
        assertFalse(turns.isPlayerTurn());

        final var player = new ParticipantImpl(4, 4);
        final var dealer = new ParticipantImpl(4, 4);
        final var model = new ModelImpl(player, dealer,
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK, Shell.LIVE, Shell.LIVE)), new BasicStrategy());
        player.addObjects(List.of(new Saw(), new Handcuffs(), new MagnifyingGlass()));
        final var snapshot = model.getGameData();
        assertEquals(new ItemResult.ShellInspected(Shell.LIVE), model.utilizeObject(ItemType.MAGNIFYING_GLASS));
        assertEquals(new ItemResult.ShotgunSawed(), model.utilizeObject(ItemType.SAW));
        assertEquals(new ItemResult.OpponentRestrained(), model.utilizeObject(ItemType.HANDCUFFS));

        final var sawedShot = model.shoot(Target.OPPONENT);
        assertEquals(2, sawedShot.damage());
        assertEquals(Target.OPPONENT, sawedShot.target());
        assertTrue(sawedShot.isLive());
        assertEquals(Shell.LIVE, sawedShot.shell());
        assertFalse(sawedShot.reloaded());
        assertTrue(model.isPlayerTurn());
        assertEquals(2, dealer.getState().lives());
        assertEquals(4, snapshot.dealer().lives());
        assertEquals(3, snapshot.player().items().size());

        final var blankSelfShot = model.shoot(Target.SELF);
        assertEquals(0, blankSelfShot.damage());
        assertFalse(blankSelfShot.isLive());
        assertEquals(Shell.BLANK, blankSelfShot.shell());
        assertEquals(Target.SELF, blankSelfShot.target());
        assertFalse(blankSelfShot.reloaded());
        assertTrue(model.isPlayerTurn());

        final var liveShot = model.shoot(Target.OPPONENT);
        assertEquals(1, liveShot.damage());
        assertTrue(liveShot.isLive());
        assertFalse(liveShot.reloaded());
        assertFalse(model.isPlayerTurn());

        final var dealerShot = model.playDealerTurn();
        assertEquals(1, dealerShot.damage());
        assertEquals(Target.OPPONENT, dealerShot.target());
        assertTrue(dealerShot.isLive());
        assertTrue(dealerShot.reloaded());
        assertTrue(model.isPlayerTurn());

        final var terminal = new ModelImpl(new ParticipantImpl(1, 1), new ParticipantImpl(1, 1),
            new ShotgunImpl(List.of(Shell.LIVE)), new BasicStrategy());
        final var terminalShot = terminal.shoot(Target.OPPONENT);
        assertEquals(1, terminalShot.damage());
        assertFalse(terminalShot.reloaded());
        assertTrue(terminal.gameIsOver());
        assertEquals(GameState.Winner.PLAYER, terminal.getGameData().winner());
        assertEquals(0, terminal.getGameData().shotgun().liveShells());
        assertThrows(IllegalStateException.class, () -> terminal.shoot(Target.SELF));
    }

    @Test
    void shotgunPeekAndEjectHandleShellsAndEmptyState() {
        final var gun = new ShotgunImpl(List.of(Shell.BLANK, Shell.LIVE));
        assertEquals(Shell.BLANK, gun.peekNextShell());
        assertEquals(2, gun.howManyRemaining());
        assertEquals(Shell.BLANK, gun.eject());
        assertEquals(1, gun.howManyRemaining());
        assertEquals(Shell.LIVE, gun.peekNextShell());
        assertEquals(Shell.LIVE, gun.eject());
        assertTrue(gun.isEmpty());
        assertThrows(IllegalStateException.class, gun::peekNextShell);
        assertThrows(IllegalStateException.class, gun::eject);
    }

    @Test
    void beerEjectsShellReloadsOnLastShellAndRejectsEmptyShotgun() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new Beer(), new Beer()));
        final var model = new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.BLANK, Shell.LIVE)), new BasicStrategy());

        assertEquals(new ItemResult.ShellEjected(Shell.BLANK), model.utilizeObject(ItemType.BEER));
        assertEquals(1, model.getGameData().shotgun().liveShells());
        assertEquals(0, model.getGameData().shotgun().blankShells());
        assertEquals(1, model.getGameData().player().items().size());

        assertEquals(new ItemResult.ShellEjected(Shell.LIVE), model.utilizeObject(ItemType.BEER));
        final var reloaded = model.getGameData().shotgun();
        assertEquals(6, reloaded.liveShells() + reloaded.blankShells());
        assertEquals(2, model.getGameData().player().items().size());
        assertEquals(2, model.getGameData().dealer().items().size());

        final var emptyGun = new ShotgunImpl(List.of());
        assertEquals(new ItemResult.NotUsable(), new Beer().use(player, new ParticipantImpl(4, 4), emptyGun));
    }

    @Test
    void magnifyingGlassInspectsWithoutConsumingShellAndChecksOwnership() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new MagnifyingGlass()));
        final var model = new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.BLANK, Shell.LIVE)), new BasicStrategy());

        assertEquals(new ItemResult.ShellInspected(Shell.BLANK), model.utilizeObject(ItemType.MAGNIFYING_GLASS));
        assertTrue(model.getGameData().player().items().isEmpty());
        assertEquals(1, model.getGameData().shotgun().liveShells());
        assertEquals(1, model.getGameData().shotgun().blankShells());
        assertEquals(new ItemResult.NotUsable(), model.utilizeObject(ItemType.MAGNIFYING_GLASS));
    }

    @Test
    void cigaretteHealsOnlyWhenDamaged() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new Cigarette()));
        final var model = new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.LIVE, Shell.BLANK)), new BasicStrategy());

        assertEquals(new ItemResult.NotUsable(), model.utilizeObject(ItemType.CIGARETTE));
        assertEquals(1, model.getGameData().player().items().size());

        assertEquals(1, model.shoot(Target.SELF).damage());
        assertEquals(3, model.getGameData().player().lives());
        assertEquals(1, model.playDealerTurn().damage());
        assertEquals(2, model.getGameData().player().lives());
        assertTrue(model.isPlayerTurn());
        assertEquals(new ItemResult.Healed(), model.utilizeObject(ItemType.CIGARETTE));
        assertEquals(3, model.getGameData().player().lives());
        assertTrue(model.getGameData().player().items().isEmpty());
    }

    @Test
    void sawAndHandcuffsReturnNotUsableWhenAlreadyActive() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(List.of(new Saw(), new Saw(), new Handcuffs(), new Handcuffs()));
        final var model = new ModelImpl(player, new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK)), new BasicStrategy());

        assertEquals(new ItemResult.ShotgunSawed(), model.utilizeObject(ItemType.SAW));
        assertEquals(new ItemResult.NotUsable(), model.utilizeObject(ItemType.SAW));
        assertEquals(1, model.getGameData().player().items().stream().filter(i -> i == ItemType.SAW).count());

        assertEquals(new ItemResult.OpponentRestrained(), model.utilizeObject(ItemType.HANDCUFFS));
        assertEquals(new ItemResult.NotUsable(), model.utilizeObject(ItemType.HANDCUFFS));
        assertEquals(1, model.getGameData().player().items().stream().filter(i -> i == ItemType.HANDCUFFS).count());
    }

    @Test
    void participantCapsInventoryAtMaxItemsAndValidatesShotInputs() {
        final var player = new ParticipantImpl(4, 4);
        player.addObjects(Collections.nCopies(ParticipantImpl.MAX_ITEMS + 2, new Beer()));
        assertEquals(ParticipantImpl.MAX_ITEMS, player.getState().items().size());
        player.addObjects(List.of(new Saw()));
        assertEquals(ParticipantImpl.MAX_ITEMS, player.getState().items().size());

        final var other = new ParticipantImpl(4, 4);
        final var gun = new ShotgunImpl(List.of(Shell.LIVE));
        assertThrows(NullPointerException.class, () -> player.shoot(null, other));
        assertThrows(NullPointerException.class, () -> player.shoot(gun, null));
    }

    @Test
    void shotOutcomeReportsReloadAndDealerTargetSelection() {
        final var firstModel = new ModelImpl(new ParticipantImpl(4, 4), new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.BLANK)), new BasicStrategy());
        final var playerShot = firstModel.shoot(Target.OPPONENT);
        assertEquals(Target.OPPONENT, playerShot.target());
        assertTrue(playerShot.isLive());
        assertEquals(1, playerShot.damage());
        assertFalse(playerShot.reloaded());

        final var dealerBlankSelf = firstModel.playDealerTurn();
        assertEquals(Target.SELF, dealerBlankSelf.target());
        assertEquals(Shell.BLANK, dealerBlankSelf.shell());
        assertFalse(dealerBlankSelf.isLive());
        assertEquals(0, dealerBlankSelf.damage());
        assertTrue(dealerBlankSelf.reloaded());
        assertEquals(6, firstModel.getGameData().shotgun().liveShells()
            + firstModel.getGameData().shotgun().blankShells());

        final var secondModel = new ModelImpl(new ParticipantImpl(4, 4), new ParticipantImpl(4, 4),
            new ShotgunImpl(List.of(Shell.LIVE, Shell.LIVE, Shell.BLANK)), new BasicStrategy());
        assertFalse(secondModel.shoot(Target.OPPONENT).reloaded());
        final var dealerLiveOpponent = secondModel.playDealerTurn();
        assertEquals(Target.OPPONENT, dealerLiveOpponent.target());
        assertEquals(Shell.LIVE, dealerLiveOpponent.shell());
        assertTrue(dealerLiveOpponent.isLive());
        assertEquals(1, dealerLiveOpponent.damage());
        assertFalse(dealerLiveOpponent.reloaded());
    }
}
