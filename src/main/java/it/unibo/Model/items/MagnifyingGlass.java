package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public final class MagnifyingGlass implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.MAGNIFYING_GLASS; }

    @Override
    public ItemResult use(final Participant user, final Participant opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty()) {
            return new ItemResult.NotUsable();
        }
        return new ItemResult.ShellInspected(shotgun.peekNextShell());
    }
}
