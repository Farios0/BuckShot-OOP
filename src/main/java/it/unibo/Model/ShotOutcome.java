package it.unibo.Model;

import java.util.Objects;
import it.unibo.Model.Model.Target;
import it.unibo.Model.engine.Shell;
import it.unibo.Model.engine.ShotResult;

/** Outcome of a single shot, including whether the shotgun reloaded afterwards. */
public record ShotOutcome(Target target, ShotResult shot, boolean reloaded) {
    public ShotOutcome {
        Objects.requireNonNull(target);
        Objects.requireNonNull(shot);
    }

    public Shell shell() {
        return shot.shell();
    }

    public int damage() {
        return shot.damage();
    }

    public boolean isLive() {
        return shot.isLive();
    }
}
