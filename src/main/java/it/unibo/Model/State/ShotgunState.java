package it.unibo.Model.State;

/** Public shell counts; never exposes shell order. */
public record ShotgunState(int liveShells, int blankShells, boolean sawed) { }
