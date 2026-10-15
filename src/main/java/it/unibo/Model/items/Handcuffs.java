package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public final class Handcuffs implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.HANDCUFFS; }

    @Override
    public boolean use(final Character user, final Character opponent, final Shotgun shotgun) {
        if (opponent.isRestrained()) { return false; }
        opponent.restrain();
        return true;
    }
}
