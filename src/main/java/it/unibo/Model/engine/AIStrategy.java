package it.unibo.Model.engine;

import it.unibo.Model.GameState;
import it.unibo.Model.Model.Target;

public interface AIStrategy {
    Target chooseTarget(GameState state);
}
