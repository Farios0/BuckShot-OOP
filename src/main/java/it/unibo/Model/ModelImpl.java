package it.unibo.Model;

import java.util.List;

import it.unibo.Model.engine.Shotgun;
import it.unibo.Model.engine.ShotgunImpl;
import it.unibo.Model.engine.TurnManager;
import it.unibo.Model.entities.Character;
import it.unibo.Model.items.SpecialItem;

public class ModelImpl implements Model {
    private final Character dealer;
    private final Character player;
    private final Shotgun shotgun;
    private final TurnManager turnManager;

    public ModelImpl() {
        dealer = null;
        player = null;
        shotgun = new ShotgunImpl();
        turnManager = new TurnManager();
    }

    @Override
    public int shoot(Character target) {
        return player.shoot(shotgun, target);
    }

    @Override
    public boolean utilizeObject(SpecialItem obj) {
        if (turnManager.isPlayerTurn())
            return player.useObject(obj);
        return dealer.useObject(obj);
    }

    @Override
    public boolean gameIsOver() {
        return player.isDead() || dealer.isDead();
    }

    @Override
    public List<SpecialItem> distributeObjects() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distributeObjects'");
    }

    @Override
    public boolean isPlayerTurn() {
        return turnManager.isPlayerTurn();
    }
}
