package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public final class Handcuffs implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.HANDCUFFS; }

    @Override
    public ItemResult use(final Participant user, final Participant opponent, final Shotgun shotgun) {
        if (opponent.isRestrained()) {
            return new ItemResult.NotUsable();
        }
        opponent.restrain();
        return new ItemResult.OpponentRestrained();
    }
}
