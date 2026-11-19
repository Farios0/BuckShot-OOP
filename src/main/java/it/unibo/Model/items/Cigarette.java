package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public final class Cigarette implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.CIGARETTE; }

    @Override
    public ItemResult use(final Participant user, final Participant opponent, final Shotgun shotgun) {
        if (user.getState().lives() == user.getState().maxLives()) {
            return new ItemResult.NotUsable();
        }
        user.heal();
        return new ItemResult.Healed();
    }
}
