package milo.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import milo.Milo;

/**
 * Controls Milo's main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final int AVATAR_SIZE = 64;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = createAvatar(Color.web("#5B6CFF"));
    private final Image miloImage = createAvatar(Color.web("#26A269"));
    private Milo milo;

    /** Keeps the newest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Milo instance that processes commands.
     *
     * @param milo Milo instance shared by this window.
     */
    public void setMilo(Milo milo) {
        this.milo = milo;
        dialogContainer.getChildren().add(
                DialogBox.getMiloDialog("Hello! I'm Milo.\nHow can I help you?", miloImage));
    }

    /** Processes input submitted using either the text field or Send button. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        String response = milo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMiloDialog(response, miloImage));
        userInput.clear();
        if (input.equals("bye")) {
            Platform.exit();
        }
    }

    /** Creates a simple circular avatar without requiring platform-specific image assets. */
    private static Image createAvatar(Color color) {
        WritableImage avatar = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter pixels = avatar.getPixelWriter();
        double center = (AVATAR_SIZE - 1) / 2.0;
        double radiusSquared = center * center;
        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                double distanceSquared = Math.pow(x - center, 2) + Math.pow(y - center, 2);
                pixels.setColor(x, y, distanceSquared <= radiusSquared ? color : Color.TRANSPARENT);
            }
        }
        return avatar;
    }
}
