package fil.ui.controllers;

import com.sun.jdi.Location;
import com.sun.jdi.event.*;
import fil.command.Command;
import fil.command.StepCommand;
import fil.dbg.JDISimpleDebuggee;
import fil.dbg.ScriptableDebugger;
import fil.ui.util.DisplayGenerator;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static fil.ui.app.DebuggingApp.ALTERNATIVE_STYLE_CSS;
import static fil.ui.app.DebuggingApp.DEFAULT_STYLES_CSS;
import static java.lang.Thread.sleep;

/**
 * Controller for the DebuggingApp.fxml view.
 */
public class DebuggingAppController {

    private static final int NB_OF_CMDS = 17;
    @FXML
    public TextArea outputArea;
    @FXML
    public TextArea inputArea;
    @FXML
    public TextArea classCode;
    @FXML
    public TextArea currentlyExecuting;
    @FXML
    public VBox commandsContainer;
    @FXML
    public VBox codeContainer;
    @FXML
    public VBox commandsHandling;
    @FXML
    public TitledPane classCodePane;
    private boolean lightMode = false;
    private ScriptableDebugger debugger;
    @FXML
    private Button themeSwitcher;

    /**
     * Generates a displayable string from the result of a debugging command.
     *
     * @param result The result of the debugging command (can be null).
     * @return The displayable string.
     */
    private static String generateDisplay(Object result) {
        if (result instanceof Map)
            return DisplayGenerator.display((Map<?, ?>) result);
        if (result instanceof List)
            return DisplayGenerator.display((List<?>) result);
        if (result instanceof Set)
            return DisplayGenerator.display((Set<?>) result);
        if (result instanceof Object[])
            return DisplayGenerator.display((Object[]) result);
        if (result instanceof String)
            return DisplayGenerator.display((String) result);
        return DisplayGenerator.display(result);
    }

    /**
     * Initializes the debugging environment.
     * This method initializes the debugger, attaches it to a debuggee class, and sets up the user interface.
     *
     * @throws InterruptedException If the thread is interrupted.
     */
    @FXML
    public void initialize() throws InterruptedException {
        debugger = new ScriptableDebugger();
        debugger.setListenerApp(this);
        new Thread(() -> debugger.attachTo(JDISimpleDebuggee.class)).start();

        // Wait for the debugger to have loaded all commands before initializing the user interface
        while (debugger.getCommandManager() == null || debugger.getCommandManager().getCommands().size() < NB_OF_CMDS) {
            System.out.println("Waiting for debugger to be ready...");
            sleep(500);
        }

        initCommandButtons();
    }

    /**
     * Initializes the command buttons in the user interface.
     */
    private void initCommandButtons() {
        Collection<Command<?>> commandsSortedByName = debugger.getCommandManager().getCommands().stream()
                .sorted(Comparator.comparing(c -> c.getClass().getSimpleName()))
                .collect(Collectors.toCollection(ArrayList::new));

        for (Command<?> cmd : commandsSortedByName) {
            // exemple : "BreakOnceCommand"
            String commandFullName = cmd.getClass().getSimpleName();
            // exemple : "Break Once"
            String commandName = commandFullName
                    .replaceAll("Command", "")
                    .replaceAll("([A-Z])", " $1");
            Button cmdButton = new Button(commandName);
            cmdButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {
                    System.out.println("Executing command: " + commandName);
                    onCommandClicked(cmd);
                } catch (Exception e) {
                    updateDebuggingStatus("Erreur lors de l'exécution de la commande\n" + e.getMessage());
                }
            });
            cmdButton.setPrefWidth(300);
            commandsContainer.getChildren().add(cmdButton);
        }
    }

    /**
     * Handles the submission of a debugging command and executes it.
     *
     * @param command The debugging command to execute.
     * @throws Exception If an exception occurs during command execution.
     */
    public void onCommandClicked(Command<?> command) throws Exception {
        if (debugger != null) {
            String[] args = inputArea.getText().split(" ");
            Object result = command.execute(args);
            if (command instanceof StepCommand) {
                debugger.getVm().resume();
            }
            String response = generateDisplay(result);
            updateDebuggingStatus(response);
        }
    }

    /**
     * Switches the user interface theme between dark and light mode.
     */
    @FXML
    public void switchTheme() {
        Scene scene = themeSwitcher.getScene();
        if (lightMode) {
            scene.getStylesheets().remove(ALTERNATIVE_STYLE_CSS);
            scene.getStylesheets().add(DEFAULT_STYLES_CSS[1]);
            themeSwitcher.setText("Mode clair");
        } else {
            scene.getStylesheets().remove(DEFAULT_STYLES_CSS[1]);
            scene.getStylesheets().add(ALTERNATIVE_STYLE_CSS);
            themeSwitcher.setText("Mode sombre");
        }
        lightMode = !lightMode;
    }

    /**
     * Updates the debugging status in the user interface.
     *
     * @param response The response message to display.
     */
    public void updateDebuggingStatus(String response) {
        if (debugger != null) {
            outputArea.setText(response);
        }
    }

    /**
     * Updates the displayed source code in the user interface.
     *
     * @param code The source code to display.
     */
    public void updateCode(String code) {
        classCode.setText(code);
    }

    /**
     * Updates the information about the currently executing code line in the user interface.
     *
     * @param location The location of the currently executing code.
     */
    public void updateCurrentlyExecuting(Location location) {
        String displayLine;
        try {
            String code = classCode.getText();
            int currentLineNumber = location.lineNumber();
            String[] lines = code.split("\\r?\\n");
            displayLine = lines[currentLineNumber - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error while fetching source code: " + e.getMessage());
            displayLine = "Code inatteignable.";
        } catch (Exception e) {
            System.err.println("Error while fetching source code: " + e.getMessage());
            displayLine = "Erreur lors de l'affichage de la ligne " + location.lineNumber();
        }
        currentlyExecuting.setText(displayLine);
    }

    /**
     * Retrieves the source code of a specified class.
     *
     * @param someClass The class for which to fetch the source code.
     * @return The source code of the class.
     */
    private String getSourceCode(Class<?> someClass) {
        try {
            String className = someClass.getName();
            String sourceFileName = className.replace('.', File.separatorChar) + ".java";
            Path sourceFilePath = Paths.get("..", "..", "src", "main", "java", sourceFileName);
            String sourceCode = Files.readString(sourceFilePath);
            String[] lines = sourceCode.split("\n");
            // Adding line numbers at the start of each line
            for (int i = 0; i < lines.length; i++) {
                lines[i] = (i + 1) + "\t" + lines[i];
            }
            return String.join("\n", lines);
        } catch (Exception e) {
            System.err.println("Error while fetching source code: " + e.getMessage());
            System.err.println("Check that the source file is located in the expected folder.");
            System.err.println("If not, you can change the source file path in " +
                    "DebuggingAppController.getSourceCode() method.");
            return "";
        }
    }

    /**
     * Handles debugger events and updates the user interface accordingly.
     *
     * @param event The debug event to handle.
     */
    public void onEvent(Event event) {
        if (event instanceof ClassPrepareEvent) {
            // Initialize the source code display if it is empty (events are received before the GUI is fully initialized)
            if (classCode.getText().isEmpty()) {
                updateCode(getSourceCode(debugger.getDebugClass()));
            }
        }
        if (event instanceof StepEvent) {
            updateCurrentlyExecuting(((StepEvent) event).location());
        } else if (event instanceof BreakpointEvent) {
            updateCurrentlyExecuting(((BreakpointEvent) event).location());
        } else if (event instanceof VMDeathEvent) {
            updateDebuggingStatus("Fin de l'exécution.");
            // lock the commands handling pane
            commandsHandling.setDisable(true);
        }
    }

}
