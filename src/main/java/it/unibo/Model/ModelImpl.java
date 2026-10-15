package it.unibo.Model;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import it.unibo.Model.GameState.Winner;
import it.unibo.Model.engine.AIStrategy;
import it.unibo.Model.engine.BasicStrategy;
import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.engine.ShotResult;
import it.unibo.Model.engine.TurnManager;
import it.unibo.Model.entities.Character;
import it.unibo.Model.entities.Participant;
import it.unibo.Model.items.Beer;
import it.unibo.Model.items.Cigarette;
import it.unibo.Model.items.Handcuffs;
import it.unibo.Model.items.ItemType;
import it.unibo.Model.items.MagnifyingGlass;
import it.unibo.Model.items.Saw;
import it.unibo.Model.items.SpecialItem;

public class ModelImpl implements Model {
    private final Character dealer;
    private final Character player;
    private final Shotgun shotgun;
    private final TurnManager turnManager = new TurnManager();
    private final AIStrategy strategy;
    private final Random random = new Random();
    private Boolean revealedShell;

    public ModelImpl() {
        this(new Participant(4, 4), new Participant(4, 4), new ShotgunImpl(), new BasicStrategy());
        distributeObjects();
    }

    public ModelImpl(final Character player, final Character dealer, final Shotgun shotgun,
            final AIStrategy strategy) {
        this.player = Objects.requireNonNull(player);
        this.dealer = Objects.requireNonNull(dealer);
        this.shotgun = Objects.requireNonNull(shotgun);
        this.strategy = Objects.requireNonNull(strategy);
        if (player == dealer) {
            throw new IllegalArgumentException("Participants must be different");
        }
    }

    private Character actor() { return isPlayerTurn() ? player : dealer; }
    private Character opponent() { return isPlayerTurn() ? dealer : player; }

    private void checkActive() {
        if (gameIsOver()) {
            throw new IllegalStateException("The game is over");
        }
    }

    @Override
    public int shoot(final Target target) {
        checkActive();
        Objects.requireNonNull(target);
        final Character other = opponent();
        final ShotResult result = actor().shoot(shotgun, target == Target.SELF ? actor() : other);
        // A blank self-shot does not attempt to pass the turn or consume handcuffs.
        final boolean retainTurn = !result.live() && target == Target.SELF;
        final boolean restrained = !retainTurn && other.consumeRestraint();
        turnManager.endTurn(!result.live(), target == Target.SELF, restrained);
        revealedShell = null;
        reloadIfNeeded();
        return result.damage();
    }

    @Override
    public boolean utilizeObject(final ItemType type) {
        checkActive();
        Objects.requireNonNull(type);
        final SpecialItem item = actor().getItems().stream()
            .filter(candidate -> candidate.getType() == type).findFirst().orElse(null);
        if (item == null || !actor().useObject(item, opponent(), shotgun)) {
            return false;
        }
        if (type == ItemType.MAGNIFYING_GLASS) {
            revealedShell = shotgun.isBulletAlive();
        } else if (type == ItemType.BEER) {
            revealedShell = null;
        }
        reloadIfNeeded();
        return true;
    }

    @Override
    public boolean inspectNextShell() {
        checkActive();
        if (revealedShell == null) {
            throw new IllegalStateException("Use a magnifying glass first");
        }
        return revealedShell;
    }

    private void reloadIfNeeded() {
        if (!gameIsOver() && shotgun.isEmpty()) {
            shotgun.recharge();
            revealedShell = null;
            distributeObjects();
        }
    }

    @Override
    public boolean gameIsOver() { return player.isDead() || dealer.isDead(); }

    @Override
    public void distributeObjects() {
        checkActive();
        player.addObjects(List.of(randomItem(), randomItem()));
        dealer.addObjects(List.of(randomItem(), randomItem()));
    }

    private SpecialItem randomItem() {
        return switch (random.nextInt(5)) {
            case 0 -> new Beer();
            case 1 -> new Cigarette();
            case 2 -> new Saw();
            case 3 -> new Handcuffs();
            default -> new MagnifyingGlass();
        };
    }

    @Override
    public boolean isPlayerTurn() { return turnManager.isPlayerTurn(); }

    @Override
    public GameState getGameData() {
        final Winner winner = player.isDead() ? Winner.DEALER : dealer.isDead() ? Winner.PLAYER : Winner.NONE;
        return new GameState(shotgun.getState(), dealer.getState(), player.getState(), isPlayerTurn(), winner);
    }

    @Override
    public int playDealerTurn() {
        checkActive();
        if (isPlayerTurn()) {
            throw new IllegalStateException("It is the player's turn");
        }
        return shoot(strategy.chooseTarget(getGameData()));
    }
}
