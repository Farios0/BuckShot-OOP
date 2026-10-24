package it.unibo.View;

import java.util.Objects;
import java.util.List;
import it.unibo.Controller.Action;
import it.unibo.Controller.InputHandler;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import it.unibo.Model.GameState;
import it.unibo.Model.items.ItemType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

/** Builds and owns the main game screen. */
public final class JavaFXView implements View {
    private static final double DESIGN_WIDTH = 1920;
    private static final double DESIGN_HEIGHT = 1080;
    private final StackPane root;
    private final Pane gameArea;
    private final Pane interfaceLayer;
    private final ImageView backgroundImage;
    private final ImageView dealerImage;
    private final ImageView tableImage;
    private final ImageView shotgunImage;

    private final HBox playerInventory = new HBox(12);
    private final HBox dealerInventory = new HBox(8);
    private final Label itemMessage = new Label("Seleziona un oggetto per usarlo.");
    private InputHandler inputHandler = action -> { };
    private final Button shootSelf = new Button("Spara a te stesso");
    private final Button shootOpponent = new Button("Spara al dealer");
    private final Label gameStatus = new Label();
    private final PauseTransition dealerDelay = new PauseTransition(Duration.millis(800));

    public JavaFXView() {
        backgroundImage = new ImageView(loadImage("/images/back.png"));
        backgroundImage.setFitWidth(DESIGN_WIDTH);
        backgroundImage.setFitHeight(DESIGN_HEIGHT);
        backgroundImage.setMouseTransparent(true);

        dealerImage = new ImageView(loadImage("/images/dealer.png"));
        tableImage = new ImageView(loadImage("/images/table.png"));
        shotgunImage = new ImageView(loadImage("/images/shotgun2.png"));
        positionImage(dealerImage, 350, 230);
        positionImage(tableImage, 600, 440);
        positionImage(shotgunImage, 250, 500);

        // Reserved for controls, shell counters and interactive inventory items.
        interfaceLayer = new Pane();
        interfaceLayer.resize(DESIGN_WIDTH, DESIGN_HEIGHT);
        interfaceLayer.setPickOnBounds(false);
        final VBox playerPanel = inventoryPanel("I TUOI OGGETTI", playerInventory, 1000, 130);
        playerPanel.relocate(460, 900);
        itemMessage.getStyleClass().add("inventory-message");
        playerPanel.getChildren().add(itemMessage);
        final VBox dealerPanel = inventoryPanel("OGGETTI DEL DEALER", dealerInventory, 430, 130);
        dealerPanel.relocate(1430, 60);
        shootSelf.getStyleClass().add("inventory-item");
        shootOpponent.getStyleClass().add("inventory-item");
        shootSelf.setOnAction(event -> inputHandler.onActionChosen(Action.shootSelf()));
        shootOpponent.setOnAction(event -> inputHandler.onActionChosen(Action.shootOpponent()));
        final HBox shotControls = new HBox(16, shootSelf, shootOpponent);
        shotControls.setAlignment(Pos.CENTER);
        shotControls.setPrefWidth(1000);
        shotControls.relocate(460, 820);
        gameStatus.getStyleClass().add("inventory-title");
        gameStatus.relocate(60, 60);
        interfaceLayer.getChildren().addAll(playerPanel, dealerPanel, shotControls, gameStatus);

        // Child order defines depth: background, dealer, table, weapon, UI.
        gameArea = new Pane(backgroundImage, dealerImage, tableImage, shotgunImage, interfaceLayer);
        gameArea.resize(DESIGN_WIDTH, DESIGN_HEIGHT);
        gameArea.setMinSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        gameArea.setMaxSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        final Group scaledContent = new Group(gameArea);
        scaledContent.setAutoSizeChildren(false);
        root = new StackPane(scaledContent);
        root.setMinSize(0, 0);
        root.setStyle("-fx-background-color: black;");

        // Scale the whole composition uniformly, including the background and UI.
        final NumberBinding scale = Bindings.min(
            root.widthProperty().divide(DESIGN_WIDTH),
            root.heightProperty().divide(DESIGN_HEIGHT));
        final Scale contentScale = new Scale(1, 1, 0, 0);
        contentScale.xProperty().bind(scale);
        contentScale.yProperty().bind(scale);
        gameArea.getTransforms().add(contentScale);

        root.getStylesheets().add(Objects.requireNonNull(
            JavaFXView.class.getResource("/css/style.css"), "Missing stylesheet: /css/style.css")
            .toExternalForm());
    }

    /** Supplies actions without exposing mutable controls to callers. */
    public void setInputHandler(final InputHandler handler) {
        inputHandler = Objects.requireNonNull(handler);
    }

    /** Updates both inventories using only the public game snapshot. */
    public void display(final GameState state) {
        Objects.requireNonNull(state);
        final boolean canAct = state.playerTurn() && state.winner() == GameState.Winner.NONE;
        shootSelf.setDisable(!canAct);
        shootOpponent.setDisable(!canAct);
        gameStatus.setText("Tu: " + state.player().lives() + " vite | Dealer: " + state.dealer().lives()
            + " vite\nCartucce: " + state.shotgun().liveShells() + " cariche, "
            + state.shotgun().blankShells() + " a salve\n"
            + (state.winner() != GameState.Winner.NONE ? "Partita terminata"
                : state.playerTurn() ? "Tocca a te" : "Turno del dealer"));
        updateInventory(playerInventory, state.player().items(), true,
            state.playerTurn() && state.winner() == GameState.Winner.NONE);
        updateInventory(dealerInventory, state.dealer().items(), false, false);
    }

    public void showMessage(final String message) {
        itemMessage.setText(Objects.requireNonNull(message));
    }

    @Override
    public void showError(final String message) {
        showMessage(message);
    }

    @Override
    public void showGameOver(final GameState.Winner winner) {
        showMessage(winner == GameState.Winner.PLAYER ? "Hai vinto!" : "Ha vinto il dealer.");
    }

    @Override
    public void scheduleDealerAction(final Runnable action) {
        dealerDelay.setOnFinished(event -> action.run());
        dealerDelay.playFromStart();
    }

    @Override
    public void cancelPendingActions() {
        dealerDelay.stop();
        dealerDelay.setOnFinished(null);
    }

    private static VBox inventoryPanel(final String title, final HBox items,
            final double width, final double height) {
        items.setAlignment(Pos.CENTER_LEFT);
        final ScrollPane scroll = new ScrollPane(items);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefViewportHeight(65);
        scroll.getStyleClass().add("inventory-scroll");
        final Label heading = new Label(title);
        heading.getStyleClass().add("inventory-title");
        final VBox panel = new VBox(8, heading, scroll);
        panel.getStyleClass().add("inventory-panel");
        panel.setPrefSize(width, height);
        panel.setMinWidth(width);
        panel.setMaxWidth(width);
        return panel;
    }

    private void updateInventory(final HBox inventory, final List<ItemType> items,
            final boolean interactive, final boolean enabled) {
        inventory.getChildren().clear();
        if (items.isEmpty()) {
            final Label empty = new Label("Nessun oggetto");
            empty.getStyleClass().add("inventory-message");
            inventory.getChildren().add(empty);
        }
        for (final ItemType item : items) {
            if (interactive) {
                final Button button = new Button(itemName(item));
                button.getStyleClass().add("inventory-item");
                button.setTooltip(new Tooltip(itemDescription(item)));
                button.setDisable(!enabled);
                button.setOnAction(event -> inputHandler.onActionChosen(Action.useItem(item)));
                inventory.getChildren().add(button);
            } else {
                final Label label = new Label(itemName(item));
                label.getStyleClass().add("dealer-item");
                Tooltip.install(label, new Tooltip(itemDescription(item)));
                inventory.getChildren().add(label);
            }
        }
    }

    private static String itemName(final ItemType item) {
        return switch (item) {
            case BEER -> "Birra";
            case CIGARETTE -> "Sigaretta";
            case SAW -> "Sega";
            case HANDCUFFS -> "Manette";
            case MAGNIFYING_GLASS -> "Lente";
        };
    }

    private static String itemDescription(final ItemType item) {
        return switch (item) {
            case BEER -> "Espelle la prossima cartuccia senza sparare.";
            case CIGARETTE -> "Recupera una vita, fino al massimo.";
            case SAW -> "Raddoppia il danno del prossimo sparo carico.";
            case HANDCUFFS -> "Fa saltare il prossimo cambio di turno all'avversario.";
            case MAGNIFYING_GLASS -> "Rivela se la prossima cartuccia è carica o a salve.";
        };
    }

    private static void positionImage(final ImageView image, final double height, final double y) {
        image.setFitHeight(height);
        image.setPreserveRatio(true);
        final double width = height * image.getImage().getWidth() / image.getImage().getHeight();
        image.setLayoutX((DESIGN_WIDTH - width) / 2);
        image.setLayoutY(y);
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
