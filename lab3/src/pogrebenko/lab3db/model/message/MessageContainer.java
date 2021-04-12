package pogrebenko.lab3db.model.message;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static pogrebenko.lab3db.model.util.ModelUtil.cutEndChars;

/**
 * Container for the Message objects.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MessageContainer {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Container for the message objects.
    public final ArrayList<Message> messages;

    /**
     * Constructs new empty message container.
     */
    public MessageContainer(ArrayList<Message> messages) {
        this.messages = messages;
    }

    /**
     * Exports messages data to log file.
     *
     * @param fileName filename to export data.
     * @throws IOException if some I/O error happened.
     */
    public void exportToLog(String fileName) throws IOException {
        LOGGER.info("Exporting data to the log file...");

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(toLog());
        }
    }

    /**
     * Creates text string from log messages to write into a file.
     */
    public String toLog() {
        LOGGER.info("Preparing text string from log messages...");
        StringBuilder serializedObject = new StringBuilder();

        for (Message message : messages) {
            serializedObject.append(message.toString()).append("\n");
        }

        return cutEndChars(serializedObject.toString(), 1);
    }
}
