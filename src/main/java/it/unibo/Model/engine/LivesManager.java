package it.unibo.Model.engine;

public class LivesManager {
    private final int maxLives;
    private int currentLives;

    public LivesManager(final int max, final int initial) {
        if (max <= 0 || initial <= 0 || initial > max) {
            throw new IllegalArgumentException("Invalid initial lives");
        }
        maxLives = max;
        currentLives = initial;
    }

    public boolean noLivesRemaining() {
        return currentLives == 0;
    }

    public void heal() {
        currentLives = Math.min(maxLives, currentLives + 1);
    }

    public void takeDamage(final int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        currentLives = Math.max(0, currentLives - damage);
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }
}
