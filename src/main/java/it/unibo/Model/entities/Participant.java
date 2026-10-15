package it.unibo.Model.entities;

/** Both player and dealer share the same game mechanics. */
public final class Participant extends AbstractCharacter {
    public Participant(final int maxLives, final int initialLives) {
        super(maxLives, initialLives);
    }
}
