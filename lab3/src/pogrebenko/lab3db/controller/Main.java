package pogrebenko.lab3db.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import pogrebenko.lab3db.view.ViewRoutes;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.util.logging.Level;

/**
 * Implementation of the lab 2.
 * Implements the GUI interface for the lab 1.
 * Code was written in a big hurry with no initial knowledge of the JavaFX,
 * so sanity check is required.
 * <p>
 * On all "internal" functions logging was set like: "ModuleX : FunctionY: Message".
 * This was an attempt to achieve some clumsy analogy of the structured logging.
 * <p>
 * This approach was used because:
 * 1) I'm not sure if we are allowed to use any frameworks
 * except those, that necessary to get the lab done.
 * 2) I'm complete novice in the Java, so any attempt to understand Maven setup for frameworks,
 * or SLF4J for logging, etc, will take a large amount of time.
 * <p>
 * (Considering my chronic undersleep and the huge amounts of work, the second reason is especially important).
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class Main extends Application {
    private static final String APP_NAME = "MEDICINE EDITOR";
    public static final String LOG_SCREEN_TITLE = APP_NAME + ": Execution log";
    public static final String INIT_SCREEN_TITLE = APP_NAME + ": Initialization";
    public static final String MAIN_SCREEN_TITLE = APP_NAME + ": Main";

    // Initialize common logger.
    static {
        LoggerWrapper LOGGER_WRAPPER = LoggerWrapper.getWrapper();
        LOGGER_WRAPPER.setLoggingLevel(Level.INFO);
        LOGGER_WRAPPER.setLogFormat();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX entry method.
     *
     * @param primaryStage - initial stage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.hide(); // Hide given window.
        // And open initialization and log window.
        // Alternatively i could just change scene of the primaryStage,
        // But this approach with the new windows looks a little bit nicer.
        ControllerUtil.openCleanWindow(ViewRoutes.APP_LOG_SCREEN, LOG_SCREEN_TITLE);
        ControllerUtil.openCleanWindow(ViewRoutes.APP_INIT_SCREEN, INIT_SCREEN_TITLE);
    }
}
