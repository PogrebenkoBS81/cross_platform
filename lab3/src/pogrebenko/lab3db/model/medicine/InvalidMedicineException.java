package pogrebenko.lab3db.model.medicine;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom exception implementation.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.0.0
 */
public class InvalidMedicineException extends Exception {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // In lab description log name must be stored in the exception class.

    /**
     * Custom exception constructor.
     *
     * @param message The string error message.
     */
    public InvalidMedicineException(String message) {
        super(message);
    }

    public InvalidMedicineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Almost no sense in this method. It was required to implement in lab1.
     *
     * @param level The level of logging to write in file.
     */
    public void writeToLog(Level level, String additionalMessage) {
        LOGGER.log(level, additionalMessage + " " + this.getMessage(), this);
    }

/*
    Task was to create custom exception, that receives string as an argument,
    so no methods that accepts Throwable is implemented:

    public InvalidMedicineException(Throwable cause) {
        super(cause);
    }
*/

}