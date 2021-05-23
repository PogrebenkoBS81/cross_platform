package pogrebenko.labsix.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pogrebenko.labsix.util.CommonUtil;
import pogrebenko.labsix.util.JavaFxUtil;
import pogrebenko.labsix.view.ViewRoutes;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitializationController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    // Settings for log file browse window.
    private final static String[] extensionsLogFile = {"*.txt", "*.log"};
    private final static String filterLabelLogFile = "Choose or create *.txt or *.log file";

    // Settings for data file browse window.
    private final static String[] extensionsDataFile = {"*.csv"};
    private final static String filterLabelDataFile = "Choose *.csv file";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tfLogFilePath"
    private TextField tfLogFilePath; // Value injected by FXMLLoader

    @FXML // fx:id="tfDataFilePath"
    private TextField tfDataFilePath; // Value injected by FXMLLoader

    @FXML
    void onBrowseDataFile(ActionEvent event) {
        LOGGER.info("Browsing data file...");
        FileChooser fc = JavaFxUtil.browseForFile(filterLabelDataFile, extensionsDataFile);
        File logFile = fc.showOpenDialog(new Stage());
        // Check if any file has been selected.
        if (logFile == null) {
            LOGGER.info("No data file chosen...");

            return;
        }
        // Get path to the selected file.
        String absPath = logFile.getAbsolutePath();
        tfDataFilePath.setText(logFile.getAbsolutePath());
        LOGGER.info("Chosen data file is: " + absPath);
    }

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

    @FXML
    void onContinuePressed(ActionEvent event) {
        LOGGER.info("Continue button pressed, validating input...");
        // Check if the selected files exist.
        if (!CommonUtil.isFileExists(getLogFilePath())) {
            LOGGER.severe("Given log file path is invalid: " + getLogFilePath());
            new Alert(Alert.AlertType.ERROR, "Invalid log file path!", ButtonType.YES).showAndWait();

            return;
        }

        if (!CommonUtil.isFileExists(getDataFilePath())) {
            LOGGER.severe("Given data file path is invalid: " + getDataFilePath());
            new Alert(Alert.AlertType.ERROR, "Invalid data file path!", ButtonType.YES).showAndWait();

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
        mainStage.show();

        LOGGER.info("Closing old window...");
        JavaFxUtil.closeWindow(event);
    }

    private void setMainControllerParams(MainController mainController) throws IOException {
        LOGGER.info("Setting the init values for the main controller...");
        mainController.setLogPath(getLogFilePath());
        mainController.setStringPlotData(loadCSV(getDataFilePath(), ","));
    }

    /**
     * Loads data from the given file.
     *
     * @param fileName  file to load from.
     * @param delimiter delimiters of the file.
     * @throws IOException if some I/O error occurred.
     */
    private ArrayList<String[]> loadCSV(String fileName, String delimiter) throws IOException {
        LOGGER.info(String.format("Loading data from: '%s', with delimiter: '%s'", fileName, delimiter));
        ArrayList<String[]> values = new ArrayList<>();

        File loadFile = new File(fileName);
        FileReader reader = new FileReader(loadFile);

        try (BufferedReader buffReader = new BufferedReader(reader)) {
            for (String line = buffReader.readLine(); line != null; line = buffReader.readLine()) {
                values.add(line.split(delimiter));
            }
        }

        return values;
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

    /**
     * Return the path to the selected log file.
     *
     * @return path to the selected log file.
     */
    public String getDataFilePath() {
        LOGGER.finest("Retrieving data file path...");

        return tfDataFilePath.getText();
    }
}
