package it.unibo.Model;

import java.util.List;

import it.unibo.Model.entities.Character;
import it.unibo.Model.items.SpecialObject;

public interface Model {
    int shoot(Character target);
    boolean utilizeObject(SpecialObject obj);
    boolean gameIsOver();
    List<SpecialObject> distributeObjects();
    boolean isPlayerTurn();
} 
