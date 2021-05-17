package pogrebenko.labfive.controller.initialization;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pogrebenko.labfive.controller.Main;
import pogrebenko.labfive.controller.main.MainController;
import pogrebenko.labfive.util.CommonUtil;
import pogrebenko.labfive.util.JavaFxUtil;
import pogrebenko.labfive.view.ViewRoutes;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @FXML // fx:id="tfFTPHost"
    private TextField tfFTPHost; // Value injected by FXMLLoader

    @FXML // fx:id="tfFTPPort"
    private TextField tfFTPPort; // Value injected by FXMLLoader

    @FXML // fx:id="tfFTPUsername"
    private TextField tfFTPUsername; // Value injected by FXMLLoader

    @FXML // fx:id="tfFTPPassword"
    private TextField pfFTPPassword; // Value injected by FXMLLoader

    /**
     * On log file browse event. Chooses log file to load in the app.
     *
     * @param event browse event.
     */
    @FXML
    void onBrowseLogFile(ActionEvent event) {
        LOGGER.info("Browsing for log file...");
        FileChooser fc = JavaFxUtil.browseForFile(filterLabelLogFile, extensionsLogFile);
        File logFile = fc.showOpenDialog(new Stage());
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
            getFTPPort();
        } catch (NumberFormatException e) {
            LOGGER.severe("Given DB port is invalid: " + getFTPPort());
            new Alert(Alert.AlertType.ERROR, "Invalid DB port provided!", ButtonType.YES).showAndWait();

            return;
        }

        // Open main page, and load data to it.
        try {
            prepareMainController(event);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            new Alert(Alert.AlertType.ERROR, "Cannot start app! " + e.getMessage(), ButtonType.YES).showAndWait();
        }
    }

    private void prepareMainController(ActionEvent event) throws IOException {
        LOGGER.info("Switching to the new window, loading scene...");
        FXMLLoader loader = JavaFxUtil.getSceneLoader(ViewRoutes.APP_MAIN_SCREEN);
        Parent root = loader.load();

        LOGGER.info("Retrieving main controller and setting the init values...");
        MainController mainController = loader.getController();

        LOGGER.info("Setting main controller params...");
        setMainControllerParams(mainController);

        LOGGER.info("Preparing main controller...");
        mainController.prepareController();

        LOGGER.info("Opening new window...");
        Stage mainStage = JavaFxUtil.getNewStage(root, Main.MAIN_SCREEN_TITLE);
        mainStage.setOnHiding(mainController::onStageClosing);
        mainStage.show();

        LOGGER.info("Closing old window...");
        JavaFxUtil.closeWindow(event);
    }

    private void setMainControllerParams(MainController mainController) {
        LOGGER.info("Setting the init values for the main controller...");
        mainController.setLogPath(getLogFilePath());

        mainController.setFTPHost(getFTPHost());
        mainController.setFTPPort(getFTPPort());
        mainController.setFTPUsername(getFTPUsername());
        mainController.setFTPPassword(getFTPPassword());
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

        JavaFxUtil.closeWindow(event);
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.info("Initialization...");
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

    public String getFTPHost() {
        LOGGER.finest("Retrieving FTP connection host...");

        return tfFTPHost.getText();
    }

    public int getFTPPort() throws NumberFormatException {
        LOGGER.finest("Retrieving FTP connection port...");

        return Integer.parseInt(tfFTPPort.getText());
    }

    public String getFTPUsername() {
        LOGGER.finest("Retrieving FTP connection username...");

        return tfFTPUsername.getText();
    }

    public String getFTPPassword() {
        LOGGER.finest("Retrieving FTP connection password...");

        return pfFTPPassword.getText();
    }
}
