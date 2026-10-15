package it.unibo.View;

import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/** Builds and owns the main game screen. */
public final class JavaFXView {
    private final BorderPane root;
    private final Label messageLabel;
    private final Button actionButton;
    private final ImageView dealerImage;
    private final ImageView tableImage;
    private final ImageView shotgunImage;
    private final HBox topBar;
    private final StackPane tableArea;

    public JavaFXView() {
        messageLabel = new Label("ops ops ops");
        actionButton = new Button("cuspide");
        dealerImage = new ImageView(loadImage("/images/dealer.jpg"));
        tableImage = new ImageView(loadImage("/images/table.png"));
        shotgunImage = new ImageView(loadImage("/images/shotgun.png"));

        topBar = new HBox(10, messageLabel, actionButton, dealerImage);
        topBar.setAlignment(Pos.CENTER);
        actionButton.setOnAction(event -> messageLabel.setText("Paride"));

        tableImage.setFitHeight(600);
        tableImage.setPreserveRatio(true);
        shotgunImage.setFitHeight(150);
        shotgunImage.setPreserveRatio(true);
        tableArea = new StackPane(tableImage, shotgunImage);

        root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tableArea);
        root.setBackground(new Background(new BackgroundImage(
            loadImage("/images/prato.jpg"), null, null, null, null)));
        root.getStylesheets().add(Objects.requireNonNull(
            JavaFXView.class.getResource("/css/style.css"), "Missing stylesheet: /css/style.css")
            .toExternalForm());
    }

    private static Image loadImage(final String path) {
        return new Image(Objects.requireNonNull(
            JavaFXView.class.getResource(path), "Missing image: " + path).toExternalForm());
    }

    /** The application needs only the root to attach the view to a scene. */
    public Parent getRoot() {
        return root;
    }
}
