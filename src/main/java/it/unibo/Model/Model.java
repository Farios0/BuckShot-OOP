package it.unibo.Model;

import java.util.List;

import it.unibo.Model.entities.Character;
import it.unibo.Model.items.SpecialItem;

public interface Model {
    int shoot(Character target);
    boolean utilizeObject(SpecialItem obj);
    boolean gameIsOver();
    List<SpecialItem> distributeObjects();
    boolean isPlayerTurn();
} 
