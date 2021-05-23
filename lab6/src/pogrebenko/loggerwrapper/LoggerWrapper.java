package pogrebenko.loggerwrapper;

import java.io.IOException;
import java.util.logging.*;

/**
 * Wrapper for default logger.
 * Allows to simply log to a single file across different classes.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class LoggerWrapper {
    // Init default logger.
    private static final Logger logger = Logger.getLogger(LoggerWrapper.class.getName());
    // Static wrapper variable for implementing singleton pattern.
    private static LoggerWrapper wrapper = null;

    /**
     * Private constructor to ensure singleton.
     */
    private LoggerWrapper() {
    }

    /**
     * Returns singleton instance of the logger wrapper.
     * No need in "synchronized" keyword for now,
     * but if app will be multithreaded, it will be necessary.
     *
     * @return LoggerWrapper the singleton instance of the logger wrapper.
     */
    public static synchronized LoggerWrapper getWrapper() {
        if (wrapper == null) {
            wrapper = new LoggerWrapper();
        }

        return wrapper;
    }

    /**
     * Returns singleton logger instance of the logger.
     *
     * @return logger instance.
     */
    public static synchronized Logger getLogger() {
        return logger;
    }

    /**
     * Adds file handler to the logger.
     *
     * @param LogName name of the log file.
     */
    public synchronized void addFileHandler(String LogName) throws IOException {
        FileHandler fh = new FileHandler(LogName);
        fh.setFormatter(new SimpleFormatter());
        addHandler(fh);
    }

    /**
     * Adds custom handler to the logger.
     *
     * @param handler handler to add.
     */
    public synchronized void addHandler(Handler handler) {
        logger.addHandler(handler);
    }

    /**
     * Sets the logger logging level.
     *
     * @param level logging level.
     */
    public synchronized void setLoggingLevel(Level level) {
        logger.setLevel(level);
    }

    /**
     * Sets the logger log format.
     */
    public synchronized void setLogFormat() {
        // Set timestamp format for logger.
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
    }
}