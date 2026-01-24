package it.unibo.Model;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.entities.Character;
import it.unibo.Model.items.SpecialObject;

public class ModelImpl implements Model {
    private final Character dealer;
    private final Character player;
    private final Shotgun shotgun;

    public ModelImpl() {
        dealer = null;
        player = null;
        shotgun = new ShotgunImpl();
    }

    @Override
    public void shoot(Character target) {
        player.shoot(shotgun, target);
    }

    @Override
    public void utilizeObject(SpecialObject obj) {
        obj.use();
    }

    @Override
    public boolean gameIsOver() {
        return player.isDead() || dealer.isDead();
    }

}
