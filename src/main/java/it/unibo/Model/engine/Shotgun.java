package it.unibo.Model.engine;

import it.unibo.Model.State.ShotgunState;

public interface Shotgun {
    int getDamage();
    boolean isBulletAlive();
    ShotResult shoot();
    int howManyRemaining();
    void skip();
    void cut();
    boolean isSawed();
    boolean isEmpty();
    void recharge();
    ShotgunState getState();
}
