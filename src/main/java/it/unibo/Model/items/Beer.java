package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public final class Beer implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.BEER; }

    @Override
    public ItemResult use(final Participant user, final Participant opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty()) {
            return new ItemResult.NotUsable();
        }
        return new ItemResult.ShellEjected(shotgun.eject());
    }
}
