package pogrebenko.lab3db.model.medicine;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pogrebenko.lab3db.model.util.ModelUtil.*;

/**
 * Container for the Medicine objects.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.0.0
 */
public class MedicineContainer {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Container for the Medicine objects.
    public final ArrayList<MedicineID> medicines = new ArrayList<>();

    /**
     * Constructs new empty medicine container.
     */
    public MedicineContainer() {
    }

    /**
     * Sorts the medicines ArrayList via compareTo().
     */
    public void sort() {
        LOGGER.info("Sorting container...");
        Collections.sort(medicines);
    }

    /**
     * Loads data from the given file.
     * Check for file is already present in the CLI, but CLI is not a part of an App.
     * And usually it should be moved to another class, and main app should not depend on it.
     * So, additional check is added here, so less exceptions would be handled in future.
     *
     * @param fileName the name of the file to load.
     * @throws InvalidMedicineException if unsupported file extension were given or some I/O error occurred.
     */
    public void importData(String fileName) throws InvalidMedicineException {
        String extension = getFileExtension(fileName);
        String currentDir = System.getProperty("user.dir");

        LOGGER.info(String.format("Loading data from '%s', current dir: '%s'", fileName, currentDir));
        // As said before, additional check for file existence.
        if (!isFileExists(fileName)) {
            throw new InvalidMedicineException(
                    String.format("MedicineContainer: importData: " +
                                    "Given file doesn't exists! Current dir: '%s'; FileName: '%s'",
                            currentDir,
                            fileName
                    )
            );
        }
        // Check for required extension. Throw an exception, if given file is not supported.
        try {
            switch (extension.toLowerCase()) {
                case ("csv") -> loadSeparatedValues(fileName, ",");
                case ("tsv") -> loadSeparatedValues(fileName, "\\t");
                default -> throw new InvalidMedicineException("Unsupported file extension for importing: " + extension);
            }
        } catch (IOException e) {
            throw new InvalidMedicineException("Cannot read data file! ", e);
        }
    }

    /**
     * Loads data from the given file.
     *
     * @param fileName  file to load from.
     * @param delimiter delimiters of the file.
     * @throws IOException if some I/O error occurred.
     */
    private void loadSeparatedValues(String fileName, String delimiter) throws IOException {
        LOGGER.info(String.format("Loading data from: '%s', with delimiter: '%s'", fileName, delimiter));
        // Prepare file readers
        File loadFile = new File(fileName);
        // FileNotFound exception is not possible, because we already checked for it,
        // And there is no async actions that may delete given file.
        FileReader reader = new FileReader(loadFile);
        // So BufferedReader will be created anyway, and it will be closed if something bad will happen.
        try (BufferedReader buffReader = new BufferedReader(reader)) {
            parseSeparatedValues(buffReader, fileName, delimiter);
        }
    }

    /**
     * Helper function for the loadSeparatedValues.
     *
     * @param buffReader file buffer reader.
     * @param fileName   file to load from.
     * @param delimiter  delimiters of the file.
     * @throws IOException if some I/O error occurred.
     */
    private void parseSeparatedValues(BufferedReader buffReader, String fileName, String delimiter) throws IOException {
        // Reading file headers and preparing them.
        String params = buffReader.readLine();
        String[] headers = params.split(delimiter);
        // Reading csv lines and creating objects from them.
        for (int line = 1; (params = buffReader.readLine()) != null; line++) {
            try {
                LOGGER.finest(String.format("Reading line %d, read data: %s", line, params));
                medicines.add((new MedicineID(headers, params, delimiter)));
            } catch (InvalidMedicineException e) {
                e.writeToLog(
                        Level.WARNING,
                        String.format("Error reading line %d of the file %s;", line, fileName)
                );
            }
        }
    }

    /**
     * Exports data to specified format.
     *
     * @param fileName filename to export data.
     * @throws InvalidMedicineException if some I/O error happened.
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void exportData(String fileName, ExportType exportFormat) throws InvalidMedicineException {
        // Remove file extension for further use.
        fileName = removeFileExtension(fileName);

        try {
            switch (exportFormat) {
                case JSON -> {
                    fileName += ".json";
                    LOGGER.info("Exporting data to: " + fileName);
                    exportToJson(fileName);
                }
                // case XML ...
                // case YAML ...
            }
        } catch (IOException e) {
            throw new InvalidMedicineException("Cannot export medicine data!", e);
        }
    }

    /**
     * Exports data to JSON file via implemented JSON serializer.
     *
     * @param fileName filename to export data.
     * @throws IOException if some I/O error happened.
     */
    private void exportToJson(String fileName) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(toJson());
        } // Close writer even if something went wrong.
    }

    /**
     * Converts object to the JSON string.
     *
     * @return JSON representation of an object.
     */
    public String toJson() {
        StringBuilder serializedObject = new StringBuilder("[ \n");

        for (Medicine medicine : medicines) {
            serializedObject.append(medicine.toJson()).append(",\n");
        }

        return cutEndChars(serializedObject.toString(), 2) + "\n]";
    }

    /**
     * Converts object to the string.
     *
     * @return string representation of an object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder("[");

        for (Medicine medicine : medicines) {
            stringRepresentation.append(medicine.toString()).append("\n");
        }

        return stringRepresentation + "]";
    }

    // Enum representing possible formats for data exporting.
    public enum ExportType {
        JSON
        // XML
        // YAML
        // etc...
    }
}
