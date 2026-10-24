package it.unibo.Controller;

import java.util.Objects;
import it.unibo.Model.items.ItemType;

/** An item is required only for USE_OBJECT actions. */
public record Action(ActionType type, ItemType item) {
    public Action {
        Objects.requireNonNull(type);
        if ((type == ActionType.USE_OBJECT) != (item != null)) {
            throw new IllegalArgumentException("Only object actions must specify an item");
        }
    }

    public static Action shootSelf() { return new Action(ActionType.SHOOT_SELF, null); }
    public static Action shootOpponent() { return new Action(ActionType.SHOOT_OPPONENT, null); }
    public static Action useItem(final ItemType item) { return new Action(ActionType.USE_OBJECT, item); }
}
