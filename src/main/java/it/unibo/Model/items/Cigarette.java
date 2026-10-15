package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public final class Cigarette implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.CIGARETTE; }

    @Override
    public boolean use(final Character user, final Character opponent, final Shotgun shotgun) {
        if (user.getState().lives() == user.getState().maxLives()) { return false; }
        user.heal();
        return true;
    }
}
