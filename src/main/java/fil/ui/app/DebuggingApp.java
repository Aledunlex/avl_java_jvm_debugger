package fil.ui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * The `DebuggingApp` class is the entry point for the JavaFX application.
 * It sets up the graphical user interface for the debugging application.
 */
public class DebuggingApp extends Application {

    public static final String TITLE = "Simple Debugger";
    public static final String VIEW_FXML = "/DebuggingAppView.fxml";
    public static final String[] DEFAULT_STYLES_CSS = {"/common.css", "/dark_theme.css"};
    public static final String ALTERNATIVE_STYLE_CSS = "/light_theme.css";

    /**
     * The main entry point for the Java application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and starts the JavaFX application.
     *
     * @param primaryStage The primary stage for the application's GUI.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(VIEW_FXML)));

            Scene scene = new Scene(root);
            scene.getStylesheets().addAll(DEFAULT_STYLES_CSS);
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            primaryStage.setOnCloseRequest(e -> System.exit(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
