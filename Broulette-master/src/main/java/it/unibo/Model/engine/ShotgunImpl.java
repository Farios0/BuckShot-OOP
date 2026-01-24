package it.unibo.Model.engine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ShotgunImpl implements Shotgun {
    private final Queue<Boolean> bullets;
    private final Random bulletsRandomizer;
    private boolean cutted;

    public ShotgunImpl() {
        bulletsRandomizer = new Random();
        bullets = new LinkedList<>();
        setUpBullets(6);
    }

    private void setUpBullets(int howMany) {
        for (int i = 0; i < howMany; i++) {
            bullets.add(bulletsRandomizer.nextBoolean());
        }
    }

    @Override
    public int getDamage() {
        return isBulletAlive()? 
        isCutted()? 2 : 1 
        : 0;
    }

    @Override
    public boolean isBulletAlive() {
        return bullets.element();
    }

    @Override
    public boolean shoot() {
        return bullets.remove();
    }

    @Override
    public int howManyRemaining() {
        return bullets.size();
    }

    @Override
    public void skip() {
        bullets.remove();
    }

    @Override
    public void cut() {
        cutted = true;
    }

    @Override
    public boolean isCutted() {
        if (cutted) {
            cutted = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return bullets.size() == 0;
    }

    @Override
    public void recharge() {
        setUpBullets(3);
    }
}
