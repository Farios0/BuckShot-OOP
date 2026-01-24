package it.unibo.Model.engine;

public interface Shotgun {

    int getDamage();

    boolean isBulletAlive();

    boolean shoot();

    int howManyRemaining();

    void skip();

    void cut();

    boolean isCutted();

    boolean isEmpty();

    void recharge();

}