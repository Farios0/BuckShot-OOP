package it.unibo.Model;

import java.util.Objects;
import it.unibo.Model.State.CharacterState;
import it.unibo.Model.State.ShotgunState;

/** Immutable snapshot suitable for views and AI decisions. */
public record GameState(ShotgunState shotgun, CharacterState dealer, CharacterState player,
        boolean playerTurn, Winner winner) {
    public enum Winner { NONE, PLAYER, DEALER }

    public GameState {
        Objects.requireNonNull(shotgun);
        Objects.requireNonNull(dealer);
        Objects.requireNonNull(player);
        Objects.requireNonNull(winner);
    }

    public ShotgunState getShotgun() { return shotgun; }
    public CharacterState getDealer() { return dealer; }
    public CharacterState getPlayer() { return player; }
}
