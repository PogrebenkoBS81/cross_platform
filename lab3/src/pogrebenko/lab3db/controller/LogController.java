package pogrebenko.lab3db.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Controller for the execution log window.
 * Shows the execution log.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class LogController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fx:id="taExecLogConsole"
    private TextArea taExecLogConsole; // Value injected by FXMLLoader

    /**
     * Exits the window when the "close" button is pressed.
     *
     * @param event browse event.
     */
    @FXML
    void onExitPressed(ActionEvent event) {
        LOGGER.info("ExecutionLog window exit button pressed...");
        ControllerUtil.closeWindow(event);
    }

    /**
     * Clears the execution log window when the corresponding button is pressed.
     *
     * @param event browse event.
     */
    @FXML
    void onLogClearPressed(ActionEvent event) {
        taExecLogConsole.clear();
    }

    /**
     * Initializes app execution log page.
     */
    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.addHandler(new TextAreaLogger(taExecLogConsole));
        LOGGER.info("Logger write handler added  successfully.");
    }

    /**
     * Custom handler for logger, to add logging to a text area.
     *
     * @author Pogrebenko Vasily, BS-81
     * @version 1.0
     * @since 1.0
     */
    public static class TextAreaLogger extends Handler {
        private final TextArea console;

        /**
         * Create custom log handler.
         *
         * @param console TextArea to log into.
         */
        TextAreaLogger(TextArea console) {
            this.console = console;
        }

        /**
         * Forms date display for the TextArea.
         *
         * @param millis millisecond to convert to a date.
         */
        private String formatMillis(long millis) {
            Date date = new Date(millis);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        /**
         * Prints message to the TextArea.
         *
         * @param record record to print.
         */
        @Override
        public void publish(LogRecord record) {
            console.appendText(String.format(
                    "%s: %s: %s %s: %s \n",
                    formatMillis(record.getMillis()),
                    record.getLevel(),
                    record.getSourceClassName(),
                    record.getSourceMethodName(),
                    record.getMessage()
            ));
        }

        /**
         * Default implementation for flush.
         */
        @Override
        public void flush() {
        }

        /**
         * Default implementation for close.
         */
        @Override
        public void close() throws SecurityException {
        }
    }
}
