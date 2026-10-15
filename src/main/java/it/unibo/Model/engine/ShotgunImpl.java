package it.unibo.Model.engine;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import it.unibo.Model.State.ShotgunState;

public class ShotgunImpl implements Shotgun {
    private final Queue<Boolean> bullets = new ArrayDeque<>();
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
    public ShotgunImpl(final List<Boolean> load) {
        random = new Random();
        bullets.addAll(List.copyOf(load));
    }

    @Override
    public int getDamage() {
        return isBulletAlive() ? (sawed ? 2 : 1) : 0;
    }

    @Override
    public boolean isBulletAlive() {
        if (isEmpty()) {
            throw new IllegalStateException("The shotgun is empty");
        }
        return bullets.element();
    }

    @Override
    public ShotResult shoot() {
        final ShotResult result = new ShotResult(isBulletAlive(), getDamage());
        bullets.remove();
        sawed = false;
        return result;
    }

    @Override
    public int howManyRemaining() {
        return bullets.size();
    }

    @Override
    public void skip() {
        if (isEmpty()) {
            throw new IllegalStateException("The shotgun is empty");
        }
        bullets.remove();
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
        return bullets.isEmpty();
    }

    /** Reloading is allowed only after all shells have been removed. */
    @Override
    public void recharge() {
        if (!isEmpty()) {
            throw new IllegalStateException("Cannot reload a loaded shotgun");
        }
        for (int i = 0; i < 6; i++) {
            bullets.add(random.nextBoolean());
        }
        sawed = false;
    }

    @Override
    public ShotgunState getState() {
        final int live = (int) bullets.stream().filter(Boolean::booleanValue).count();
        return new ShotgunState(live, bullets.size() - live, sawed);
    }
}
