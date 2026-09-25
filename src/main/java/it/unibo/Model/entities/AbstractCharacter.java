package it.unibo.Model.entities;

import java.util.List;

import it.unibo.Model.engine.LivesManager;
import it.unibo.Model.items.SpecialItem;

public abstract class AbstractCharacter implements Character {
    List<SpecialItem> objects;
    LivesManager lives;

    @Override
    public boolean isDead() {
        return lives.noLivesRemaining();
    }
}
