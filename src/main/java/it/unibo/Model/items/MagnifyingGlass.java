package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public final class MagnifyingGlass implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.MAGNIFYING_GLASS; }

    @Override
    public boolean use(final Character user, final Character opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty()) { return false; }
        // The model records the revealed shell for the acting participant.
        return true;
    }
}
