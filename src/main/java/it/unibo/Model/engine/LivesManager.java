package it.unibo.Model.engine;

public class LivesManager {
    private final int maxLives;
    private final int initialLives;
    private int currentLives;

    LivesManager(int max, int initial) {
        maxLives = max;
        initialLives = initial;
        currentLives = initialLives;
    }

    public boolean noLivesRemaining() {
        return currentLives <= 0;
    }

    public void heal() {
        if (currentLives + 1 <= maxLives) {
            currentLives++;
        }
    }

    public void takeDamage(boolean shotgunIsCutted) {
        if (shotgunIsCutted) {
            currentLives -= 2;
        } else {
            currentLives--;
        }
    }
}
