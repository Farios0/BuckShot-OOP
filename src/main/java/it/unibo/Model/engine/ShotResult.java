package it.unibo.Model.engine;

import java.util.Objects;

public record ShotResult(Shell shell, int damage) {
    public ShotResult {
        Objects.requireNonNull(shell);
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
    }

    public boolean isLive() {
        return shell.isLive();
    }
}
