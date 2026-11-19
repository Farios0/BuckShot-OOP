package it.unibo.Model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.unibo.Model.State.CharacterState;
import it.unibo.Model.engine.LivesManager;
import it.unibo.Model.engine.ShotResult;
import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.items.ItemResult;
import it.unibo.Model.items.ItemType;
import it.unibo.Model.items.SpecialItem;

/** Default participant implementation for both the player and the dealer. */
public final class ParticipantImpl implements Participant {
    public static final int MAX_ITEMS = 8;

    private final List<SpecialItem> items = new ArrayList<>();
    private final LivesManager lives;
    private boolean restrained;

    public ParticipantImpl(final int maxLives, final int initialLives) {
        lives = new LivesManager(maxLives, initialLives);
    }

    @Override
    public void addObjects(final List<SpecialItem> newItems) {
        final List<SpecialItem> safeItems = List.copyOf(newItems);
        final int availableSlots = MAX_ITEMS - items.size();
        if (availableSlots > 0) {
            items.addAll(safeItems.subList(0, Math.min(availableSlots, safeItems.size())));
        }
    }

    @Override
    public ItemResult useObject(final ItemType type, final Participant opponent, final Shotgun shotgun) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(opponent);
        Objects.requireNonNull(shotgun);
        final SpecialItem item = items.stream()
            .filter(candidate -> candidate.getType() == type)
            .findFirst()
            .orElse(null);
        if (item == null) {
            return new ItemResult.NotUsable();
        }
        final ItemResult result = item.use(this, opponent, shotgun);
        if (result.isApplied()) {
            items.remove(item);
        }
        return result;
    }

    @Override
    public ShotResult shoot(final Shotgun shotgun, final Participant target) {
        Objects.requireNonNull(shotgun);
        Objects.requireNonNull(target);
        final ShotResult result = shotgun.shoot();
        target.takeDamage(result.damage());
        return result;
    }

    @Override
    public void heal() {
        lives.heal();
    }

    @Override
    public void takeDamage(final int damage) {
        lives.takeDamage(damage);
    }

    @Override
    public void restrain() {
        restrained = true;
    }

    @Override
    public boolean isRestrained() {
        return restrained;
    }

    @Override
    public boolean consumeRestraint() {
        final boolean result = restrained;
        restrained = false;
        return result;
    }

    @Override
    public boolean isDead() {
        return lives.noLivesRemaining();
    }

    @Override
    public CharacterState getState() {
        return new CharacterState(
            lives.getCurrentLives(),
            lives.getMaxLives(),
            items.stream().map(SpecialItem::getType).toList(),
            restrained);
    }
}
