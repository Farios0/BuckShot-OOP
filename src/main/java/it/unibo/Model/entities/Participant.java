package it.unibo.Model.entities;

import java.util.List;
import it.unibo.Model.State.CharacterState;
import it.unibo.Model.engine.ShotResult;
import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.items.ItemResult;
import it.unibo.Model.items.ItemType;
import it.unibo.Model.items.SpecialItem;

public interface Participant {
    void addObjects(List<SpecialItem> items);
    ItemResult useObject(ItemType type, Participant opponent, Shotgun shotgun);
    ShotResult shoot(Shotgun shotgun, Participant target);
    void heal();
    void takeDamage(int damage);
    void restrain();
    boolean consumeRestraint();
    boolean isRestrained();
    boolean isDead();
    CharacterState getState();
}
