package pogrebenko.lab3db.model.message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a log message.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class Message {
    // DateTime format for the log message.
    public final static SimpleDateFormat messageDateFmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    // Log message.
    private String message;
    // Log Time
    private Date messageTime;

    /**
     * Constructs new empty message.
     */
    public Message() {
    }

    /**
     * Constructs new message from passed string.
     *
     * @param message message to create.
     */
    public Message(String message) {
        setMessage(message);
    }

    /**
     * Returns the log message.
     *
     * @return the log message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the log message.
     *
     * @param message the log message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the message time.
     *
     * @return the message time.
     */
    @SuppressWarnings("unused")
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * Sets the message time.
     *
     * @param messageTime the message time.
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * Converts the message object to the string.
     *
     * @return string representation of an object.
     */
    @Override
    public String toString() {
        if (messageTime == null) {
            return message;
        }

        return String.format("%s: %s", messageDateFmt.format(messageTime), message);
    }
}
