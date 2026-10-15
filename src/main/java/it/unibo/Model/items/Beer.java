package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public final class Beer implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.BEER; }

    @Override
    public boolean use(final Character user, final Character opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty()) { return false; }
        shotgun.skip();
        return true;
    }
}
