package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public interface SpecialItem {
    ItemType getType();
    /** Returns {@link ItemResult.NotUsable} when the effect cannot be applied; the item is then retained. */
    ItemResult use(Participant user, Participant opponent, Shotgun shotgun);
}
