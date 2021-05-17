package pogrebenko.labfive.controller.main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pogrebenko.labfive.model.DICOMImage;
import pogrebenko.labfive.model.DICOMImageSlicer;
import pogrebenko.labfive.model.FTPClientLoader;
import pogrebenko.labfive.util.CommonUtil;
import pogrebenko.loggerwrapper.LoggerWrapper;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    FTPClientLoader ftpClientLoader;
    DICOMImageSlicer dicomImageSlicer;
    PictureDrawer pictureDrawer;
    // Fields with required parameters for the app.
    // Must be initialized before opening the page.
    private String logPath = "";
    // DB settings fields.
    private String FTPHost;
    private int FTPPort;
    private String FTPUsername;
    // TODO: Secure password, move password to the main page and do not save it anywhere.
    private String FTPPassword;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="listViewImages"
    private ListView<DICOMSliceRow> listViewImages; // Value injected by FXMLLoader

    @FXML // fx:id="canvasImage"
    private Canvas canvasImage; // Value injected by FXMLLoader

    @FXML // fx:id="pbFilesLoading"
    private ProgressBar pbFilesLoading; // Value injected by FXMLLoader

    @FXML // fx:id="tfFilesDir"
    private TextField tfFilesDir; // Value injected by FXMLLoader

    @FXML // fx:id="tfSaveDir"
    private TextField tfSaveDir; // Value injected by FXMLLoader

    @FXML // fx:id="cbOverwriteSave"
    private CheckBox cbOverwriteSave; // Value injected by FXMLLoader

    @FXML // fx:id="btnLoadImg"
    private Button btnLoadImg; // Value injected by FXMLLoader

    @FXML // fx:id="btnSelectSaveDir"
    private Button btnSelectSaveDir; // Value injected by FXMLLoader

    @FXML // fx:id="textProgress"
    private Text textProgress; // Value injected by FXMLLoader

    @FXML // fx:id="lbImageData"
    private Label lbImageData; // Value injected by FXMLLoader

    private static boolean isDICOMFile(String filePath) {
        LOGGER.finest("Checking is file is DICOM file...");

        return CommonUtil.getFileExtension(filePath).equals("dcm");
    }

    @FXML
    void onChooseSaveDir(ActionEvent event) {
        LOGGER.info("Choosing save dir...");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File chosenDir = directoryChooser.showDialog(new Stage());

        if (chosenDir == null) {
            LOGGER.info("No save dir chosen...");

            return;
        }

        String chosenDirStr = chosenDir.getAbsolutePath();
        tfSaveDir.setText(chosenDirStr);
    }

    @FXML
    private void onLoadImages() {
        LOGGER.info("Starting files processing...");

        disableUI(true);
        stopPictureDrawer();
        setEmptySlicesList();

        Task<Void> loadTask = prepareProcessFilesTask(processFilesTask());
        Thread th = new Thread(loadTask);

        LOGGER.info("Starting file processing task...");
        th.setDaemon(true);
        th.start();
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.info("Initialization...");

        initializeCellFactories();
        initializeListClickListener();
    }

    private void initializeCellFactories() {
        LOGGER.info("Initializing listview cell factories...");

        listViewImages.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DICOMSliceRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getSliceLabel() == null) {
                    setText(null);
                } else {
                    setText(item.getSliceLabel());
                }
            }
        });
    }

    private void initializeListClickListener() {
        LOGGER.info("Initializing listview onclick listener");

        listViewImages.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    try {
                        if (newValue == null) {
                            LOGGER.info("Listview was updated...");

                            return;
                        }

                        if (oldValue == null) {
                            LOGGER.info(
                                    "Listview, first image were chosen, starting picture drawer. " +
                                            "Slice location: " + newValue.getSliceLocation()
                            );
                            runPictureDrawer(dicomImageSlicer.getSliceByLocation(newValue.getSliceLocation()));

                            return;
                        }

                        if (oldValue == newValue) {
                            LOGGER.info("Same item was selected...");

                            return;
                        }

                        LOGGER.info("Setting new slice to draw, slice location: " + newValue.getSliceLocation());
                        pictureDrawer.setCurrentSlice(dicomImageSlicer.getSliceByLocation(newValue.getSliceLocation()));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        new Alert(
                                Alert.AlertType.ERROR,
                                "Cannot show given slice! Please, contact the app developer.",
                                ButtonType.YES
                        ).showAndWait();
                    }
                });
    }

    private Task<Void> processFilesTask() {
        LOGGER.info("Creating file processing task...");

        return new Task<>() {
            @Override
            public Void call() throws Exception {
                updateMessage("Listing files on the host machine...");
                List<String> listedFiles = listDICOMFiles(ftpClientLoader.listDirectoryFiles(getFilesDir()));

                updateMessage("Loading files from the host machine...");
                List<String> loadedFiles = loadFTPFiles(
                        this::updateProgress,
                        this::updateMessage,
                        listedFiles,
                        getSaveDir(),
                        getOverwrite()
                );

                updateMessage("Creating slices from loaded files...");
                dicomImageSlicer = new DICOMImageSlicer(loadedFiles);
                SortedList<DICOMSliceRow> sortedRows = getSortedRows();

                Platform.runLater(() -> listViewImages.setItems(sortedRows));
                updateMessage("Done!");
                LOGGER.info("Process files task finished");

                return null;
            }
        };
    }

    private Task<Void> prepareProcessFilesTask(Task<Void> loadTask) {
        LOGGER.info("Preparing file processing task...");

        pbFilesLoading.progressProperty().bind(loadTask.progressProperty());
        textProgress.textProperty().bind(loadTask.messageProperty());

        loadTask.setOnSucceeded(e -> {
            LOGGER.info("Data files were successfully loaded...");
            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Data successfully loaded",
                    ButtonType.YES
            ).showAndWait();

            disableUI(false);
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();

            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            new Alert(
                    Alert.AlertType.ERROR,
                    "Files download failed! Maybe, server closed connection." +
                            "Please, try to hit 'load' button again.",
                    ButtonType.YES
            ).showAndWait();

            reconnectFTPClient();
            disableUI(false);
        });

        return loadTask;
    }

    private SortedList<DICOMSliceRow> getSortedRows() {
        LOGGER.info("Creating sorted rows for the listview...");

        return new SortedList<>(
                FXCollections.observableArrayList(formListViewRows()),
                Collections.reverseOrder(
                        Comparator.comparingDouble(DICOMSliceRow::getSliceLocation)
                )
        );
    }

    private List<DICOMSliceRow> formListViewRows() {
        LOGGER.info("Creating row values for the listview...");
        List<DICOMSliceRow> rowValues = new ArrayList<>();

        for (Map.Entry<Double, List<String>> entry : dicomImageSlicer.getSlices().entrySet()) {
            rowValues.add(new DICOMSliceRow(entry.getKey(), entry.getValue().size()));
        }

        return rowValues;
    }

    private List<String> loadFTPFiles(
            BiConsumer<Long, Long> progressBar,
            Consumer<String> messageWindow,
            List<String> listedDirFiles,
            String toSave,
            boolean overwrite
    ) {
        LOGGER.info("Loading files via ftp from the host machine...");
        List<String> savePaths = new ArrayList<>();

        for (int i = 0; i < listedDirFiles.size(); i++) {
            String fileName = new File(listedDirFiles.get(i)).getName();

            try {
                String loadPath = Paths.get(getFilesDir(), listedDirFiles.get(i)).toString();
                String savePath = Paths.get(toSave, fileName).toString();

                messageWindow.accept("Loading file: " + loadPath);
                boolean isSaved = !ftpClientLoader.downloadFile(loadPath, savePath, overwrite);

                if (!overwrite && isSaved) {
                    LOGGER.finest(String.format("File '%s' already exist in path '%s': ", fileName, savePath));
                }

                savePaths.add(savePath);
                progressBar.accept((long) i, (long) listedDirFiles.size());
            } catch (IOException e) {
                LOGGER.severe("Cannot load file: " + fileName);
                new Alert(
                        Alert.AlertType.WARNING,
                        "Cannot load file: " + fileName +
                                "\nMaybe, server has fallen. If this file is important, please, try to run download again.",
                        ButtonType.YES
                ).showAndWait();
            }
        }

        progressBar.accept((long) 1, (long) 1);
        LOGGER.info("Number of Loading files from the host machine: " + savePaths.size());

        return savePaths;
    }

    private List<String> listDICOMFiles(List<String> files) {
        LOGGER.info("filtering DICOM files from the given files...");
        List<String> listedDICOM = files.stream()
                .filter(MainController::isDICOMFile)
                .collect(Collectors.toList());
        LOGGER.info("Number of DICOM files after filter: " + listedDICOM.size());

        return listedDICOM;
    }

    private void runPictureDrawer(List<DICOMImage> selectedSlice) throws InvalidAttributeValueException {
        LOGGER.info("Running picture drawer...");

        pictureDrawer = new PictureDrawer(selectedSlice, canvasImage, lbImageData);
        pictureDrawer.run();
    }

    private void stopPictureDrawer() {
        LOGGER.info("Stopping picture drawer...");

        if (pictureDrawer == null) {
            LOGGER.info("Picture drawer already stopped.");

            return;
        }

        pictureDrawer.stop();
        pictureDrawer = null;
    }

    private void reconnectFTPClient() {
        try {
            ftpClientLoader = new FTPClientLoader(getFTPHost(), getFTPPort(), getFTPUsername(), getFTPPassword());
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            new Alert(
                    Alert.AlertType.ERROR,
                    "Error during FTP reconnection! Please, try again.",
                    ButtonType.YES
            ).showAndWait();
        }
    }

    public void setNodesDisabled(boolean disable, Node... nodes) {
        LOGGER.info("Disabling UI nodes: " + disable);

        for (Node node : nodes) {
            node.setDisable(disable);
        }
    }

    // TODO: Add graceful download thread stop.
    public void onStageClosing(WindowEvent windowEvent) {
        LOGGER.info("Stage is closing...");

        try {
            ftpClientLoader.close();
            stopPictureDrawer();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void prepareController() throws IOException {
        LOGGER.info("Preparing controller...");

        LoggerWrapper.getWrapper().addFileHandler(getLogFilePath());
        ftpClientLoader = new FTPClientLoader(getFTPHost(), getFTPPort(), getFTPUsername(), getFTPPassword());
    }

    private void setEmptySlicesList() {
        listViewImages.setItems(FXCollections.observableArrayList());
    }

    private void disableUI(boolean disable) {
        LOGGER.info("Disable UI: " + disable);

        setNodesDisabled(
                disable,
                btnLoadImg,
                btnSelectSaveDir,
                cbOverwriteSave,
                tfFilesDir,
                tfSaveDir,
                cbOverwriteSave,
                listViewImages
        );

        listViewImages.setMouseTransparent(disable);
        listViewImages.setFocusTraversable(!disable);
    }

    public void setLogPath(String logPath) {
        LOGGER.finest("Setting log file path...");

        this.logPath = logPath;
    }

    public String getLogFilePath() {
        LOGGER.finest("Retrieving log file path...");

        return logPath;
    }

    public String getFTPHost() {
        LOGGER.finest("Retrieving FTP connection host...");

        return FTPHost;
    }

    public void setFTPHost(String FTPHost) {
        LOGGER.finest("Setting FTP connection host...");

        this.FTPHost = FTPHost;
    }

    public int getFTPPort() {
        LOGGER.finest("Retrieving FTP connection port...");

        return FTPPort;
    }

    public void setFTPPort(int FTPPort) {
        LOGGER.finest("Setting FTP connection port...");

        this.FTPPort = FTPPort;
    }

    public String getFTPUsername() {
        LOGGER.finest("Retrieving FTP connection username...");

        return FTPUsername;
    }

    public void setFTPUsername(String FTPUsername) {
        LOGGER.finest("Setting FTP connection username...");

        this.FTPUsername = FTPUsername;
    }

    public String getFTPPassword() {
        LOGGER.finest("Retrieving FTP connection password...");

        return FTPPassword;
    }

    public void setFTPPassword(String FTPPassword) {
        LOGGER.finest("Setting FTP connection username...");

        this.FTPPassword = FTPPassword;
    }

    public String getFilesDir() {
        LOGGER.finest("Retrieving FTP connection files dir...");

        return tfFilesDir.getText();
    }

    public String getSaveDir() {
        LOGGER.finest("Retrieving save dir...");

        return tfSaveDir.getText();
    }

    public boolean getOverwrite() {
        LOGGER.finest("Retrieving save dir...");

        return cbOverwriteSave.isSelected();
    }
}

