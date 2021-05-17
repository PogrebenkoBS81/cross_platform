package pogrebenko.labfive.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import pogrebenko.labfive.util.JavaFxUtil;
import pogrebenko.labfive.view.ViewRoutes;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.util.logging.Level;

// TODO: Refactor code. It was, as usual, written in a big hurry,
//  so, the architecture of the application was designed before the start of development
//  -> some of the code is a complete mess.
public class Main extends Application {
    private static final String APP_NAME = "DICOM Viewer";
    public static final String MAIN_SCREEN_TITLE = APP_NAME + ": Main";
    public static final String INIT_SCREEN_TITLE = APP_NAME + ": Initialization";

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
        JavaFxUtil.openCleanWindow(ViewRoutes.APP_INIT_SCREEN, INIT_SCREEN_TITLE);
    }
}
