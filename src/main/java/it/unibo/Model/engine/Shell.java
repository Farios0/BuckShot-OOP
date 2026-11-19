package it.unibo.Model.engine;

public enum Shell {
    LIVE,
    BLANK;

    public boolean isLive() {
        return this == LIVE;
    }
}
