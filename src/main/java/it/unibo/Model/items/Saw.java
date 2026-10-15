package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public final class Saw implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.SAW; }

    @Override
    public boolean use(final Character user, final Character opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty() || shotgun.isSawed()) { return false; }
        shotgun.cut();
        return true;
    }
}
