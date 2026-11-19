package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Participant;

public final class Saw implements SpecialItem {
    @Override
    public ItemType getType() { return ItemType.SAW; }

    @Override
    public ItemResult use(final Participant user, final Participant opponent, final Shotgun shotgun) {
        if (shotgun.isEmpty() || shotgun.isSawed()) {
            return new ItemResult.NotUsable();
        }
        shotgun.cut();
        return new ItemResult.ShotgunSawed();
    }
}
