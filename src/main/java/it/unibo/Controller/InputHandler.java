package it.unibo.Controller;

@FunctionalInterface
public interface InputHandler {
    void onActionChosen(Action action);
}
