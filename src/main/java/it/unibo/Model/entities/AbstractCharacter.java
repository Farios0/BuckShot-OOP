package it.unibo.Model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.unibo.Model.State.CharacterState;
import it.unibo.Model.engine.LivesManager;
import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.engine.ShotResult;
import it.unibo.Model.items.SpecialItem;

public abstract class AbstractCharacter implements Character {
    private final List<SpecialItem> items = new ArrayList<>();
    private final LivesManager lives;
    private boolean restrained;

    protected AbstractCharacter(final int maxLives, final int initialLives) {
        lives = new LivesManager(maxLives, initialLives);
    }

    @Override
    public void addObjects(final List<SpecialItem> newItems) {
        items.addAll(List.copyOf(newItems));
    }

    @Override
    public boolean useObject(final SpecialItem item, final Character opponent, final Shotgun shotgun) {
        Objects.requireNonNull(item);
        if (!items.contains(item) || !item.use(this, opponent, shotgun)) {
            return false;
        }
        items.remove(item);
        return true;
    }

    @Override
    public ShotResult shoot(final Shotgun shotgun, final Character target) {
        Objects.requireNonNull(target);
        final ShotResult result = shotgun.shoot();
        target.takeDamage(result.damage());
        return result;
    }

    @Override
    public void heal() { lives.heal(); }
    @Override
    public void takeDamage(final int damage) { lives.takeDamage(damage); }
    @Override
    public void restrain() { restrained = true; }
    @Override
    public boolean isRestrained() { return restrained; }
    @Override
    public boolean consumeRestraint() {
        final boolean result = restrained;
        restrained = false;
        return result;
    }
    @Override
    public boolean isDead() { return lives.noLivesRemaining(); }
    @Override
    public List<SpecialItem> getItems() { return List.copyOf(items); }
    @Override
    public CharacterState getState() {
        return new CharacterState(lives.getCurrentLives(), lives.getMaxLives(),
            items.stream().map(SpecialItem::getType).toList(), restrained);
    }
}
