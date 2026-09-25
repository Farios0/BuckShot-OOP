package it.unibo.Model.entities;

import java.util.List;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.items.SpecialItem;

public interface Character {
    void addObjects(List<SpecialItem> newObj);
    boolean useObject(SpecialItem ob);
    int shoot(Shotgun shotgun, Character target);
    boolean isDead();
}
