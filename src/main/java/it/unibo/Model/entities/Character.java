package it.unibo.Model.entities;

import java.util.List;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.items.SpecialObject;

public interface Character {
    void addObjects(List<SpecialObject> newObj);
    boolean useObject(SpecialObject ob);
    void shoot(Shotgun shotgun, Character target);
    boolean isDead();
}
