package it.unibo.Model.engine;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import it.unibo.Model.State.ShotgunState;

public class ShotgunImpl implements Shotgun {
    private final Queue<Shell> shells = new ArrayDeque<>();
    private final Random random;
    private boolean sawed;

    public ShotgunImpl() {
        this(new Random());
    }

    public ShotgunImpl(final Random random) {
        this.random = Objects.requireNonNull(random);
        recharge();
    }

    /** Creates a known load, useful for deterministic games and tests. */
    public ShotgunImpl(final List<Shell> load) {
        random = new Random();
        shells.addAll(List.copyOf(load));
    }

    @Override
    public int getDamage() {
        return peekNextShell().isLive() ? (sawed ? 2 : 1) : 0;
    }

    @Override
    public Shell peekNextShell() {
        if (isEmpty()) {
            throw new IllegalStateException("The shotgun is empty");
        }
        return shells.element();
    }

    @Override
    public ShotResult shoot() {
        final Shell shell = peekNextShell();
        final ShotResult result = new ShotResult(shell, getDamage());
        shells.remove();
        sawed = false;
        return result;
    }

    @Override
    public int howManyRemaining() {
        return shells.size();
    }

    @Override
    public Shell eject() {
        if (isEmpty()) {
            throw new IllegalStateException("The shotgun is empty");
        }
        return shells.remove();
    }

    @Override
    public void cut() {
        sawed = true;
    }

    @Override
    public boolean isSawed() {
        return sawed;
    }

    @Override
    public boolean isEmpty() {
        return shells.isEmpty();
    }

    /** Reloading is allowed only after all shells have been removed. */
    @Override
    public void recharge() {
        if (!isEmpty()) {
            throw new IllegalStateException("Cannot reload a loaded shotgun");
        }
        for (int i = 0; i < 6; i++) {
            shells.add(random.nextBoolean() ? Shell.LIVE : Shell.BLANK);
        }
        sawed = false;
    }

    @Override
    public ShotgunState getState() {
        final int live = (int) shells.stream().filter(Shell::isLive).count();
        return new ShotgunState(live, shells.size() - live, sawed);
    }
}
