package it.unibo.Model.items;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.entities.Character;

public interface SpecialItem {
    void use(Character utilizzatore, Character avversario, Shotgun shotgun);
}
