package it.unibo;

import it.unibo.Controller.Controller;
import it.unibo.View.JavaFXView;
import it.unibo.Model.ModelImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX entry point; window lifecycle is separate from game coordination. */
public final class BuckshotApplication extends Application {
    private Controller controller;

    @Override
    public void start(final Stage primaryStage) {
        final JavaFXView view = new JavaFXView();
        controller = new Controller(new ModelImpl(), view);
        primaryStage.setScene(new Scene(view.getRoot(), 1920, 1080));
        controller.start();
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (controller != null) { controller.stop(); }
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
