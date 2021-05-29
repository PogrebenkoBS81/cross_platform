package pogrebenko.lab3db.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pogrebenko.lab3db.commonutil.CommonUtil;
import pogrebenko.lab3db.view.ViewRoutes;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the initialization window.
 * Initializes app settings: log path, data path.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class InitializationController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    // Settings for log file browse window.
    private final static String[] extensionsLogFile = {"*.txt", "*.log"};
    private final static String filterLabelLogFile = "Choose or create *.txt or *.log file";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tfLogFilePath"
    private TextField tfLogFilePath; // Value injected by FXMLLoader

    @FXML // fx:id="tfDBHost"
    private TextField tfDBHost; // Value injected by FXMLLoader

    @FXML // fx:id="tfDBPort"
    private TextField tfDBPort; // Value injected by FXMLLoader

    @FXML // fx:id="tfDBName"
    private TextField tfDBName; // Value injected by FXMLLoader

    @FXML // fx:id="tfDBUser"
    private TextField tfDBUser; // Value injected by FXMLLoader

    @FXML // fx:id="tfDBPass"
    private TextField tfDBPass; // Value injected by FXMLLoader

    @FXML // fx:id="cbDBChoose"
    private ComboBox<DBType> cbDBChoose; // Value injected by FXMLLoader

    /**
     * On log file browse event. Chooses log file to load in the app.
     *
     * @param event browse event.
     */
    @FXML
    void onBrowseLogFile(ActionEvent event) {
        LOGGER.info("Browsing for log file...");
        File logFile = ControllerUtil.browseForFile(filterLabelLogFile, extensionsLogFile);
        // Check if any file has been selected.
        if (logFile == null) {
            LOGGER.info("No log file chosen...");

            return;
        }
        // Get path to the selected file.
        String absPath = logFile.getAbsolutePath();
        tfLogFilePath.setText(logFile.getAbsolutePath());
        LOGGER.info("Chosen log file is: " + absPath);
    }

    /**
     * On continue button press event. Initializes the app, and opens the main window with loaded data.
     *
     * @param event browse event.
     */
    @FXML
    void onContinuePressed(ActionEvent event) {
        LOGGER.info("Continue button pressed, validating input...");
        // Check if the selected files exist.
        if (!CommonUtil.isFileExists(getLogFilePath())) {
            LOGGER.severe("Given log file path is invalid: " + getLogFilePath());
            new Alert(Alert.AlertType.ERROR, "Invalid log file path!", ButtonType.YES).showAndWait();

            return;
        }

        try {
            Integer.parseInt(getDBPort());
        } catch (NumberFormatException e) {
            LOGGER.severe("Given DB port is invalid: " + getDBPort());
            new Alert(Alert.AlertType.ERROR, "Invalid DB port provided!", ButtonType.YES).showAndWait();

            return;
        }

        if (getDBType() == null) {
            LOGGER.severe("DB type must be selected!");
            new Alert(Alert.AlertType.ERROR, "DB type must be selected!", ButtonType.YES).showAndWait();

            return;
        }

        // Open main page, and load data to it.
        try {
            prepareMainController(event);
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            new Alert(Alert.AlertType.ERROR, "Cannot start app! " + e.getMessage(), ButtonType.YES).showAndWait();
        }
    }

    /**
     * Initializes app initialization page.
     */
    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.info("Initialization...");
        cbDBChoose.getItems().setAll(DBType.values());
    }

    /**
     * Prepares the main page of the app to work.
     * <p>
     * Firstly, I wanted to create functional interface, that will contain preparation method,
     * to avoid duplicate code like: loader.load, open window, etc.
     * <p>
     * In such realization, preparation lambda should contain all required actions of the page preparation, like:
     * setDataPath
     * setLogPath
     * etc..
     * Such lambda will be passed and called from openWindow function between all duplicated code.
     * <p>
     * But since there is only 1 window (so no need in it for now),
     * and realisation above would be really hard-to-read, i decided to do it simple.
     *
     * @param event browse event.
     * @throws IOException, on import data errors.
     */
    private void prepareMainController(ActionEvent event) throws IOException, SQLException {
        LOGGER.info("Switching to the new window, loading scene...");
        FXMLLoader loader = ControllerUtil.getSceneLoader(ViewRoutes.APP_MAIN_SCREEN);
        Parent root = loader.load();

        LOGGER.info("Retrieving main controller and setting the init values...");
        MainController mainController = loader.getController();

        LOGGER.info("Setting main controller params...");
        setMainControllerParams(mainController);

        LOGGER.info("Preparing main controller...");
        mainController.prepareController();

        LOGGER.info("Opening new window...");
        Stage mainStage = ControllerUtil.getNewStage(root, Main.MAIN_SCREEN_TITLE);
        mainStage.setOnHiding(mainController::onStageClosing);
        mainStage.show();

        LOGGER.info("Closing old window...");
        ControllerUtil.closeWindow(event);
    }

    private void setMainControllerParams(MainController mainController) {
        LOGGER.info("Setting the init values for the main controller...");
        mainController.setLogPath(getLogFilePath());

        mainController.setDBType(getDBType());
        mainController.setDBHost(getDBHost());
        mainController.setDBPort(Integer.parseInt(getDBPort()));
        mainController.setDBName(getDBName());

        mainController.setDBUser(getDBUser());
        mainController.setDBPassword(getDBPassword());
    }

    /**
     * Exits the window when the "close" button is pressed.
     *
     * @param event browse event.
     */
    @FXML
    void onExitPressed(ActionEvent event) {
        // Alternatively i could use: Stage stage = (Stage)btnExit.getScene().getWindow(); stage.close();
        LOGGER.info("Exit button on init page pressed...");

        ControllerUtil.closeWindow(event);
    }

    /**
     * Return the path to the selected log file.
     *
     * @return path to the selected log file.
     */
    public String getLogFilePath() {
        LOGGER.finest("Retrieving log file path...");

        return tfLogFilePath.getText();
    }

    private DBType getDBType() {
        LOGGER.finest("Retrieving db type...");

        return cbDBChoose.getValue();
    }

    public String getDBHost() {
        LOGGER.finest("Retrieving db host...");

        return tfDBHost.getText();
    }

    public String getDBPort() {
        LOGGER.finest("Retrieving db port...");

        return tfDBPort.getText();
    }

    public String getDBName() {
        LOGGER.finest("Retrieving db name...");

        return tfDBName.getText();
    }

    public String getDBUser() {
        LOGGER.finest("Retrieving db user...");

        return tfDBUser.getText();
    }

    public String getDBPassword() {
        LOGGER.finest("Retrieving db password...");

        return tfDBPass.getText();
    }
}

