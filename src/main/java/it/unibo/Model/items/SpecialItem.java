package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public interface SpecialItem {
    ItemType getType();
    /** Returns false when the effect cannot be applied; the item is then retained. */
    boolean use(Character user, Character opponent, Shotgun shotgun);
}
