package it.unibo.Model;

import java.util.List;

import it.unibo.Model.State.CharacterState;
import it.unibo.Model.State.ShotgunState;
import it.unibo.Model.items.SpecialItem;

public class GameState {
    private ShotgunState shotgun;
    private CharacterState dealer;
    private CharacterState player;
    private List<SpecialItem> items;
}
