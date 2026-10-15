package it.unibo;

import it.unibo.View.JavaFXView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX entry point; window lifecycle is separate from view construction. */
public final class BuckshotApplication extends Application {
    @Override
    public void start(final Stage primaryStage) {
        final JavaFXView view = new JavaFXView();
        primaryStage.setScene(new Scene(view.getRoot(), 1920, 1080));
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
