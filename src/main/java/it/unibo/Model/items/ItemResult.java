package it.unibo.Model.items;

import java.util.Objects;
import it.unibo.Model.engine.Shell;

/** Typed outcome produced when applying a {@link SpecialItem}. */
public sealed interface ItemResult {
    default boolean isApplied() {
        return !(this instanceof NotUsable);
    }

    record NotUsable() implements ItemResult { }

    record Healed() implements ItemResult { }

    record ShotgunSawed() implements ItemResult { }

    record OpponentRestrained() implements ItemResult { }

    record ShellInspected(Shell shell) implements ItemResult {
        public ShellInspected {
            Objects.requireNonNull(shell);
        }
    }

    record ShellEjected(Shell shell) implements ItemResult {
        public ShellEjected {
            Objects.requireNonNull(shell);
        }
    }
}
