package it.unibo.Model.State;

import java.util.List;
import it.unibo.Model.items.ItemType;

public record CharacterState(int lives, int maxLives, List<ItemType> items, boolean restrained) {
    public CharacterState {
        items = List.copyOf(items);
    }
}
