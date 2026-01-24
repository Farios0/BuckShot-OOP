package it.unibo.Model;

import it.unibo.Model.entities.Character;
import it.unibo.Model.items.SpecialObject;

public interface Model {
    void shoot(Character target);
    void utilizeObject(SpecialObject obj);
    boolean gameIsOver();
} 
