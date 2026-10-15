package it.unibo.Model.entities;

import java.util.List;
import it.unibo.Model.State.CharacterState;
import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.engine.ShotResult;
import it.unibo.Model.items.SpecialItem;

public interface Character {
    void addObjects(List<SpecialItem> items);
    boolean useObject(SpecialItem item, Character opponent, Shotgun shotgun);
    ShotResult shoot(Shotgun shotgun, Character target);
    void heal();
    void takeDamage(int damage);
    void restrain();
    boolean consumeRestraint();
    boolean isRestrained();
    boolean isDead();
    CharacterState getState();
    List<SpecialItem> getItems();
}
