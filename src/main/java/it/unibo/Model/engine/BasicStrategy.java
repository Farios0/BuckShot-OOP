package it.unibo.Model.engine;

import it.unibo.Model.GameState;
import it.unibo.Model.Model.Target;

/** Uses public counts only; does not inspect the shell order. */
public final class BasicStrategy implements AIStrategy {
    @Override
    public Target chooseTarget(final GameState state) {
        return state.shotgun().liveShells() == 0 ? Target.SELF : Target.OPPONENT;
    }
}
