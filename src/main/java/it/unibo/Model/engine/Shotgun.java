package it.unibo.Model.engine;

import it.unibo.Model.State.ShotgunState;

public interface Shotgun {
    int getDamage();
    Shell peekNextShell();
    ShotResult shoot();
    int howManyRemaining();
    Shell eject();
    void cut();
    boolean isSawed();
    boolean isEmpty();
    void recharge();
    ShotgunState getState();
}
