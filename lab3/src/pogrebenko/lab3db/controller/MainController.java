package pogrebenko.lab3db.controller;

// TODO: add dependency injection to the project.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.WindowEvent;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.IntegerStringConverter;
import pogrebenko.lab3db.controller.customconverter.DateAlertConverter;
import pogrebenko.lab3db.model.medicine.InvalidMedicineException;
import pogrebenko.lab3db.model.medicine.Medicine;
import pogrebenko.lab3db.model.medicine.MedicineContainer;
import pogrebenko.lab3db.model.medicine.MedicineID;
import pogrebenko.lab3db.model.message.Message;
import pogrebenko.lab3db.model.message.MessageContainer;
import pogrebenko.lab3db.sqldatabase.common.contract.IMedicineDB;
import pogrebenko.lab3db.sqldatabase.common.contract.IMessageDB;
import pogrebenko.lab3db.sqldatabase.common.contract.IToolsDB;
import pogrebenko.lab3db.sqldatabase.common.factory.MedicineFactory;
import pogrebenko.lab3db.sqldatabase.common.factory.MessageFactory;
import pogrebenko.lab3db.sqldatabase.common.factory.ToolsFactory;
import pogrebenko.lab3db.sqldatabase.database.mysql.medicine.FilterField;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the main app window.
 * Displays the loaded data, allows to edit and export it.
 * No edit of ID value is possible, because it supposed to be unique.
 * TODO: IMPROVE NAMING, FOLLOW NAMING CONVENTIONS.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.2.0
 */
public class MainController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Settings for export file dialog.
    private static final String dataExportLabel = "Choose or create json file to export";
    private static final String[] filterExtensionsDataExport = {"*.json"};
    // Settings for data file browse window.
    private final static String[] extensionsDataFile = {"*.tsv", "*.csv"};
    private final static String filterLabelDataFile = "Choose *.tsv or *.csv file";

    private final static String[] extensionsExportLogFile = {"*.log", "*.txt"};
    private final static String filterLabelExportLogFile = "Choose *.log or *.txt file";
    // Additional FilteredList is required for filter field.
    // As I understand filteredMedicines will contain references to the MedicineID objects,
    // So no serious additional memory will be consumed here.
    ObservableList<MedicineID> observableMedicines;
    // Fields with required parameters for the app.
    // Must be initialized before opening the page.
    private String logPath = "";
    // Databases required for the controller.
    // TODO: DBs should start in another thread, or otherwise,
    //  if connection waiting for timeout, app will hang.
    private IMedicineDB medicineDB;
    private IMessageDB messageDB;
    // DB settings fields.
    private DBType dbType;
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPass;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tvMedicineTable"
    private TableView<MedicineID> tvMedicineTable; // Value injected by FXMLLoader

    @FXML // fx:id="columnName"
    private TableColumn<MedicineID, String> columnName; // Value injected by FXMLLoader

    @FXML // fx:id="columnForm"
    private TableColumn<MedicineID, String> columnForm; // Value injected by FXMLLoader

    @FXML // fx:id="columnProducer"
    private TableColumn<MedicineID, String> columnProducer; // Value injected by FXMLLoader

    @FXML // fx:id="columnExpires"
    private TableColumn<MedicineID, Date> columnExpires; // Value injected by FXMLLoader

    @FXML // fx:id="columnProduced"
    private TableColumn<MedicineID, Date> columnProduced; // Value injected by FXMLLoader

    @FXML // fx:id="columnCost"
    private TableColumn<MedicineID, Integer> columnCost; // Value injected by FXMLLoader

    @FXML // fx:id="columnPrescriptionOnly"
    private TableColumn<MedicineID, Boolean> columnPrescriptionOnly; // Value injected by FXMLLoader

    @FXML // fx:id="columnID"
    private TableColumn<MedicineID, Integer> columnID; // Value injected by FXMLLoader

    @FXML // fx:id="tfFilter"
    private TextField tfFilter; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineExpires"
    private TextField tfMedicineExpires; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineProduced"
    private TextField tfMedicineProduced; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineCost"
    private TextField tfMedicineCost; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicinePSOnly"
    private TextField tfMedicinePSOnly; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineName"
    private TextField tfMedicineName; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineForm"
    private TextField tfMedicineForm; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineProducer"
    private TextField tfMedicineProducer; // Value injected by FXMLLoader

    @FXML // fx:id="tfMedicineSearch"
    private TextField tfMedicineSearch; // Value injected by FXMLLoader

    @FXML // fx:id="cbMedicineSearch"
    private ComboBox<FilterField> cbMedicineSearch; // Value injected by FXMLLoader

    /**
     * Edits the Name value in the tableview.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnNameEdit(TableColumn.CellEditEvent<MedicineID, String> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setName);
    }

    /**
     * Edits the Form value in the tableview.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnFormEdit(TableColumn.CellEditEvent<MedicineID, String> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setForm);
    }

    /**
     * Edits the Producer value in the tableview.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnProducerEdit(TableColumn.CellEditEvent<MedicineID, String> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setProducer);
    }

    /**
     * Edits the Expires value in the tableview.
     * Due to alert box on error input is required, this realization of Date converter was created:
     * {@link pogrebenko.lab3db.controller.customconverter.DateAlertConverter}
     * On error null value will be removed without any exception.
     * Then, if input value was incorrect, it will be null, and newValue == null will show an alert box,
     * just as we were told to do in the task.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnExpiresEdit(TableColumn.CellEditEvent<MedicineID, Date> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setExpirationDate);
    }

    /**
     * Edits the Produced value in the tableview.
     * Same as: {@link pogrebenko.lab3db.controller.MainController#onColumnExpiresEdit(TableColumn.CellEditEvent)}.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnProducedEdit(TableColumn.CellEditEvent<MedicineID, Date> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setProductionDate);
    }

    /**
     * Edits the Cost value in the tableview.
     *
     * @param event browse event.
     */
    @FXML
    void onColumnCostEdit(TableColumn.CellEditEvent<MedicineID, Integer> event) throws InvalidMedicineException {
        MedicineID medicine = event.getRowValue();
        onFieldEdit(event, medicine, medicine::setCost);
    }

    /**
     * Edits the Prescription value in the tableview.
     *
     * @param e browse event.
     */
    @FXML
    void onColumnPrescriptionEdit(TableColumn.CellEditEvent<MedicineID, Boolean> e) throws InvalidMedicineException {
        MedicineID medicine = e.getRowValue();
        onFieldEdit(e, medicine, medicine::setPrescriptionOnly);
    }

    /**
     * Common processing patter for all fields edit.
     *
     * @param event          browse event.
     * @param medicine       medicine to process.
     * @param medicineSetter required setter for current medicine.
     */
    private <T> void onFieldEdit(
            TableColumn.CellEditEvent<MedicineID, T> event,
            MedicineID medicine,
            Medicine.MedicineSetter<T> medicineSetter
    ) throws InvalidMedicineException {
        T newValue = event.getNewValue();
        T oldValue = event.getOldValue();

        try {
            if (newValue == null) {
                throw new InvalidMedicineException(String.format(
                        "Null value for medicine with ID %d, column '%s'",
                        medicine.getId(),
                        event.getTableColumn().getText()
                )
                );
            }

            medicineSetter.setField(newValue);
            medicineDB.updateMedicine(medicine);
            LOGGER.info(MessageFormat.format("Medicine ID {0}: new value: {1}", medicine.getId(), newValue));
        } catch (SQLException | InvalidMedicineException e) {
            medicineSetter.setField(oldValue);
            logOnError(e);
        }
        // refresh table to immediately show changes.
        getMedicineTable().refresh();
    }

    /**
     * Runs the search by specified field and it's parameter.
     *
     * @param event browse event.
     */
    @FXML
    void onMedicineSearch(ActionEvent event) {
        if (getMedicineSearchField() == null) {
            logOnError(new IllegalArgumentException("Filter field must be selected!"));
        }

        try {
            updateTableValues(medicineSearchSwitch());
        } catch (SQLException | ParseException e) {
            logOnError(e);
        }
    }

    /**
     * Exports data to JSON (No another format is supported right now).
     *
     * @param event browse event.
     */
    @FXML
    void onDataExport(ActionEvent event) {
        LOGGER.info("Browsing for export file...");
        File exportFile = ControllerUtil.browseForFile(dataExportLabel, filterExtensionsDataExport);

        if (exportFile == null) {
            LOGGER.info("No export file chosen...");

            return;
        }

        String absPath = exportFile.getAbsolutePath();
        LOGGER.info("Chosen log file is: " + absPath);

        try {
            exportData(absPath);
            new Alert(Alert.AlertType.INFORMATION, "Data successfully exported!", ButtonType.YES).showAndWait();
        } catch (InvalidMedicineException e) {
            logOnError(e);
        }
    }

    /**
     * Exports log data from DB to log file.
     *
     * @param event browse event.
     */
    @FXML
    void onLogDBExport(ActionEvent event) {
        LOGGER.info("Browsing for log export file...");
        File exportFile = ControllerUtil.browseForFile(filterLabelExportLogFile, extensionsExportLogFile);

        if (exportFile == null) {
            LOGGER.info("No log export file chosen...");

            return;
        }

        String absPath = exportFile.getAbsolutePath();
        LOGGER.info("Chosen log file is: " + absPath);

        try {
            new MessageContainer(messageDB.getMessages()).exportToLog(absPath);
            new Alert(Alert.AlertType.INFORMATION, "Log successfully exported!", ButtonType.YES).showAndWait();
        } catch (SQLException | IOException e) {
            logOnError(e);
        }
    }

    /**
     * On data file browse event. Chooses data file to load in the app.
     *
     * @param event browse event.
     */
    @FXML
    void onImportBtnPressed(ActionEvent event) {
        LOGGER.info("Browsing for data file...");
        File dataFile = ControllerUtil.browseForFile(filterLabelDataFile, extensionsDataFile);
        // Check if any file has been selected.
        if (dataFile == null) {
            LOGGER.info("No data file chosen...");

            return;
        }
        // Get path to the selected file.
        String absPath = dataFile.getAbsolutePath();
        LOGGER.info("Chosen data file is: " + absPath);

        try {
            importData(absPath);
        } catch (InvalidMedicineException | SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Selects the first element in the table.
     *
     * @param event browse event.
     */
    @FXML
    void onFirstBtnPressed(ActionEvent event) {
        LOGGER.info("Going to first element...");

        getMedicineTable().getSelectionModel().selectFirst();
    }

    /**
     * Selects the last element in the table.
     *
     * @param event browse event.
     */
    @FXML
    void onLastBtnPressed(ActionEvent event) {
        LOGGER.info("Going to the last element...");

        getMedicineTable().getSelectionModel().selectLast();
    }

    /**
     * Selects the next element (relative to the current selected element) in the table.
     *
     * @param event browse event.
     */
    @FXML
    void onNextBtnPressed(ActionEvent event) {
        LOGGER.info("Going to the next element...");

        getMedicineTable().getSelectionModel().selectNext();
    }

    /**
     * Selects the previous element (relative to the current selected element) in the table.
     *
     * @param event browse event.
     */
    @FXML
    void onPreviousBtnPressed(ActionEvent event) {
        LOGGER.info("Going to the previous element...");

        getMedicineTable().getSelectionModel().selectPrevious();
    }

    /**
     * Adds the new medicine to the DB.
     *
     * @param event browse event.
     */
    @FXML
    void onAddMedicinePressed(ActionEvent event) {
        try {
            // Get params from the corresponding GUI fields.
            MedicineID med = new MedicineID(
                    getMedControllerName(),
                    getMedControllerForm(),
                    getMedControllerProducer(),
                    Medicine.DateFmt.parse(getMedControllerExpDate()),
                    Medicine.DateFmt.parse(getMedControllerProdDate()),
                    Integer.parseInt(getMedControllerCost()),
                    Boolean.parseBoolean(getMedControllerPrescSatus()),
                    -1
            );
            // Set ID returned by the DB as medicineID.
            med.setId(medicineDB.writeMedicine(med));
            addMedicineToTable(med);
        } catch (ParseException | NumberFormatException | InvalidMedicineException | SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Drops the log database.
     *
     * @param event browse event.
     */
    @FXML
    void onDropLogDBPressed(ActionEvent event) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you really want do drop log database?",
                ButtonType.YES, ButtonType.NO
        );
        alert.showAndWait();
        // Return if user changed his mind.
        if (alert.getResult() == ButtonType.NO) {
            return;
        }

        try {
            messageDB.truncateTable();
            new Alert(Alert.AlertType.INFORMATION, "Message DB successfully dropped!", ButtonType.YES).showAndWait();
        } catch (SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Drops the medicine database.
     *
     * @param event browse event.
     */
    @FXML
    void onDropMedDBPressed(ActionEvent event) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you really want do drop medicine database?",
                ButtonType.YES, ButtonType.NO
        );
        alert.showAndWait();
        // Return if user changed his mind.
        if (alert.getResult() == ButtonType.NO) {
            return;
        }

        try {
            // Clear table in DB and than clear view table.
            medicineDB.truncateTable();
            clearTableValues();
            new Alert(Alert.AlertType.INFORMATION, "Medicine DB successfully dropped!", ButtonType.YES).showAndWait();
        } catch (SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Displays the help message.
     * TODO: extend help message.
     *
     * @param event browse event.
     */
    @FXML
    void onHelpPressed(ActionEvent event) {
        String message = """
                To search for all medicines, select search medicines selector as "None", and then press "Search".
                """;

        new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES).showAndWait();
    }

    /**
     * Deletes selected medicine from table.
     *
     * @param event browse event.
     */
    @FXML
    void onDeleteSelectedMed(ActionEvent event) {
        MedicineID med = getMedicineTable().getSelectionModel().getSelectedItem();

        try {
            medicineDB.deleteMedicine(med.getId());
            removeMedicineFromTable(med);
        } catch (SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Initializes app main page.
     */
    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.info("Initialization...");
        setFactories();
        prepareComboboxes();
    }

    /**
     * Sets combobox values.
     */
    private void prepareComboboxes() {
        cbMedicineSearch.getItems().setAll(FilterField.values());
    }

    /**
     * Sets factories for every table cell.
     */
    private void setFactories() {
        // Set all required cell factories for every field (except the ID field),
        // to allow the proper cell navigation and modification.
        setTextCellFactories(columnName, "Name");
        setTextCellFactories(columnForm, "Form");
        setTextCellFactories(columnProducer, "Producer");

        setDateCellFactories(columnExpires, "ExpirationDate", Medicine.DateFmt);
        setDateCellFactories(columnProduced, "ProductionDate", Medicine.DateFmt);

        setIntegerCellFactories(columnCost, "Cost");

        setBooleanCellFactories(columnPrescriptionOnly, "PrescriptionOnly");
        // Don't set the CellFactory to avoid editing.
        columnID.setCellValueFactory(new PropertyValueFactory<>("Id"));
    }

    /**
     * Sets the cell factories for the String fields.
     *
     * @param column     table column to which cell factory will be added.
     * @param getterName the getter name in the {@link Medicine}.
     *                   Via this getter JavaFX will get values for it table.
     *                   As example, getter is: "getCost", then getterName should be "Cost".
     */
    public void setTextCellFactories(TableColumn<MedicineID, String> column, String getterName) {
        column.setCellValueFactory(new PropertyValueFactory<>(getterName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Sets the cell factories for the Date fields.
     *
     * @param column     table column to which cell factory will be added.
     * @param getterName the getter name in the {@link Medicine}.
     *                   Same as:
     *                   {@link pogrebenko.lab3db.controller.MainController#setTextCellFactories(TableColumn, String)}.
     */
    public void setDateCellFactories(TableColumn<MedicineID, Date> column, String getterName, SimpleDateFormat fmt) {
        column.setCellValueFactory(new PropertyValueFactory<>(getterName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DateAlertConverter(fmt)));
    }

    /**
     * Sets the cell factories for the Integer fields.
     *
     * @param column     table column to which cell factory will be added.
     * @param getterName the getter name in the {@link Medicine}.
     *                   Same as:
     *                   {@link pogrebenko.lab3db.controller.MainController#setTextCellFactories(TableColumn, String)}.
     */
    public void setIntegerCellFactories(TableColumn<MedicineID, Integer> column, String getterName) {
        column.setCellValueFactory(new PropertyValueFactory<>(getterName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    /**
     * Sets the cell factories for the Boolean fields.
     *
     * @param column     table column to which cell factory will be added.
     * @param getterName the getter name in the {@link Medicine}.
     *                   Same as:
     *                   {@link pogrebenko.lab3db.controller.MainController#setTextCellFactories(TableColumn, String)}.
     */
    public void setBooleanCellFactories(TableColumn<MedicineID, Boolean> column, String getterName) {
        column.setCellValueFactory(new PropertyValueFactory<>(getterName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new BooleanStringConverter()));
    }

    /**
     * Clears medicine table, and sets passed medicines to the view.
     *
     * @param medicines medicines to display in the view.
     */
    private void updateTableValues(ArrayList<MedicineID> medicines) {
        clearTableValues();
        addMedicinesToTable(medicines);
    }

    /**
     * Exports medicines to the given path (into JSON file).
     *
     * @param path the path to the json to which the data will be exported.
     */
    private void exportData(String path) throws InvalidMedicineException {
        // Creating container for each import\export may be inefficient,
        // but otherwise, managing both medicine container and observable list,
        // would be too clumsy.
        MedicineContainer medicineContainer = new MedicineContainer();
        medicineContainer.medicines.addAll(getMedicineTable().getItems());
        medicineContainer.sort();
        medicineContainer.exportData(path, MedicineContainer.ExportType.JSON);
    }

    /**
     * Clears the table via clearing underlying observable list.
     */
    private void clearTableValues() {
        observableMedicines.clear();
    }

    private ArrayList<MedicineID> medicineSearchSwitch() throws SQLException, ParseException {
        FilterField filter = getMedicineSearchField();
        String searchVal = getMedicineSearchValue();
        ArrayList<MedicineID> medicines;

        switch (filter) {
            case EXPIRATION_DATE, PRODUCTION_DATE -> {
                Date date = Medicine.DateFmt.parse(searchVal);
                medicines = medicineDB.getFilteredMedicines(filter, date);
            }
            case COST, ID -> {
                int intVal = Integer.parseInt(searchVal);
                medicines = medicineDB.getFilteredMedicines(filter, intVal);
            }
            case PRESCRIPTION_ONLY -> {
                boolean boolVal = Boolean.parseBoolean(searchVal);
                medicines = medicineDB.getFilteredMedicines(filter, boolVal);
            } // default for NONE and String params.
            default -> medicines = medicineDB.getFilteredMedicines(filter, searchVal);
        }

        return medicines;
    }

    /**
     * Imports data to DB, and displays them on the table.
     *
     * @param dataPath the path to the csv\tsv from which data will be imported.
     */
    private void importData(String dataPath) throws InvalidMedicineException, SQLException {
        MedicineContainer medicines = new MedicineContainer();
        medicines.importData(dataPath);

        medicineDB.writeMedicines(medicines.medicines);
        observableMedicines.clear();
        // In my opinion, just reading whole DB table back to the grid
        // would be better than inserting medicines value-by-value without batch.
        // Anyway, it's really easy to change the implementation of this feature.
        try {
            addMedicinesToTable(medicineDB.getMedicines());
        } catch (SQLException e) {
            logOnError(e);
        }
    }

    /**
     * Prepares controller for the work.
     *
     * @throws IOException on import data errors.
     */
    public void prepareController() throws IOException, SQLException {
        LoggerWrapper.getWrapper().addFileHandler(getLogPath());
        LOGGER.info("Initializing medicine container...");
        // Create database if not exists.
        initDB();
        // Establish connections with medicine and message DB.
        prepareDBs();
        // Set filter and bind sortedlist to the table.
        prepareTable();
        // Fill the table with already existing values from DB.
        addMedicinesToTable(medicineDB.getMedicines());
    }

    /**
     * Creates given DB if not exists.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private void initDB() throws SQLException {
        // Connect without DB name.
        IToolsDB toolsDB = ToolsFactory.getMySQLDatabase(
                getDBHost(),
                getDBPort(),
                "",
                getDBUser(),
                getDBPassword()
        );
        // Create required DB and close DB tools.
        toolsDB.createDB(getDBName());
        toolsDB.close();
    }

    /**
     * Prepares every DB for work.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private void prepareDBs() throws SQLException {
        medicineDB = getMedicineDB(getDBType());
        medicineDB.initialize();

        messageDB = getMessageDB(getDBType());
        messageDB.initialize();
    }

    /**
     * Prepares table grid and it's filter for work.
     */
    private void prepareTable() {
        observableMedicines = FXCollections.observableArrayList();
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<MedicineID> filteredMedicines = new FilteredList<>(observableMedicines, p -> true);
        // 2. Set the filter Predicate whenever the filter changes, implemented in: onTfFilterTyped
        onTfFilterTyped(filteredMedicines);
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<MedicineID> sortedData = new SortedList<>(filteredMedicines);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(getMedicineTable().comparatorProperty());
        // 5. Bind SortedList to the tableview
        getMedicineTable().setItems(sortedData);
    }

    /**
     * Filters all medicines by name.
     *
     * @param filteredMedicines FilteredList list of medicines that bind to table for filtering.
     */
    void onTfFilterTyped(FilteredList<MedicineID> filteredMedicines) {
        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            //  Runs every time when tfFilter field is changed
            filteredMedicines.setPredicate(medicine -> {
                // If filter text is empty, display all persons.
                if (tfFilter.getText() == null || tfFilter.getText().isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = tfFilter.getText().toLowerCase();
                return medicine.getName().toLowerCase().contains(lowerCaseFilter); // Filter matches first name.
            });
        });
    }

    /**
     * Establishes connection with medicines DB.
     *
     * @param dbType type of DB to create.
     */
    private IMedicineDB getMedicineDB(DBType dbType) throws SQLException {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (dbType) {
            case MYSQL -> medicineDB = MedicineFactory.getMySQLDatabase(
                    getDBHost(),
                    getDBPort(),
                    getDBName(),
                    getDBUser(),
                    getDBPassword()
            );
        };
    }

    /**
     * Establishes connection with messages DB.
     *
     * @param dbType type of DB to create.
     */
    private IMessageDB getMessageDB(DBType dbType) throws SQLException {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (dbType) {
            case MYSQL -> messageDB = MessageFactory.getMySQLDatabase(
                    getDBHost(),
                    getDBPort(),
                    getDBName(),
                    getDBUser(),
                    getDBPassword()
            );
        };
    }

    /**
     * Writes error log to console, file, DB and to the console, shows an alert box.
     *
     * @param e error exception.
     */
    private void logOnError(Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
        new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.YES).showAndWait();

        try {
            messageDB.writeMessage(new Message(e.getMessage()));
        } catch (SQLException throwables) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            new Alert(
                    Alert.AlertType.ERROR,
                    "Cannot write error to DB! " + e.getMessage(),
                    ButtonType.YES
            ).showAndWait();
        }
    }

    /**
     * Closes all DB connections on stage exit.
     *
     * @param windowEvent window event.
     */
    public void onStageClosing(WindowEvent windowEvent) {
        try {
            medicineDB.close();
            messageDB.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds given medicines to tableview.
     *
     * @param medicines medicines to add.
     */
    private void addMedicinesToTable(ArrayList<MedicineID> medicines) {
        observableMedicines.addAll(medicines);
    }

    /**
     * Adds given medicine to tableview.
     *
     * @param medicine medicine to add.
     */
    private void addMedicineToTable(MedicineID medicine) {
        observableMedicines.add(medicine);
    }

    /**
     * Removes given medicine from tableview.
     *
     * @param medicine medicine to remove.
     */
    private void removeMedicineFromTable(MedicineID medicine) {
        observableMedicines.remove(medicine);
    }

    /**
     * Returns the path of the log file for the app.
     *
     * @return path to the log file.
     */
    public String getLogPath() {
        LOGGER.finest("Returning log path...");

        return logPath;
    }

    /**
     * Initializes the path of the log file for the app.
     *
     * @param path - path to the log file.
     */
    public void setLogPath(String path) {
        LOGGER.finest("Log path is received: " + path);

        logPath = path;
    }

    /**
     * Returns the medicine tableview.
     *
     * @return medicine tableview.
     */
    private TableView<MedicineID> getMedicineTable() {
        return tvMedicineTable;
    }

    /*Medicine controllers filter fields start*/
    private FilterField getMedicineSearchField() {
        return cbMedicineSearch.getValue();
    }

    private String getMedicineSearchValue() {
        return tfMedicineSearch.getText();
    }
    /*Medicine controllers filter fields end*/


    /*Medicine controllers fields start*/
    private String getMedControllerName() {
        return tfMedicineName.getText();
    }

    private String getMedControllerForm() {
        return tfMedicineForm.getText();
    }

    private String getMedControllerProducer() {
        return tfMedicineProducer.getText();
    }

    private String getMedControllerExpDate() {
        return tfMedicineExpires.getText();
    }

    private String getMedControllerProdDate() {
        return tfMedicineProduced.getText();
    }

    private String getMedControllerCost() {
        return tfMedicineCost.getText();
    }

    private String getMedControllerPrescSatus() {
        return tfMedicinePSOnly.getText();
    }
    /*Medicine controllers fields end*/


    /*Main controller settings start*/
    public DBType getDBType() {
        return dbType;
    }

    public void setDBType(DBType dbtype) {
        this.dbType = dbtype;
    }

    public String getDBHost() {
        return dbHost;
    }

    public void setDBHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public int getDBPort() {
        return dbPort;
    }

    public void setDBPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDBName() {
        return dbName;
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public String getDBUser() {
        return dbUser;
    }

    public void setDBUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDBPassword() {
        return dbPass;
    }

    public void setDBPassword(String dbPass) {
        this.dbPass = dbPass;
    }
    /*Main controller settings end*/
}