package pogrebenko.lab3db.sqldatabase.common.parser;

import pogrebenko.lab3db.model.message.Message;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * SQL message DB util, that parse ResultSet objects into message objects,
 * and contains various methods for usage in the medicine DB.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MessageParser {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private MessageParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses messages from the given result set.
     *
     * @param rs ResultSet to parse.
     * @return list of parsed messages.
     * @throws SQLException on a database access error or other errors.
     */
    public static ArrayList<Message> parseMessages(ResultSet rs) throws SQLException {
        LOGGER.info("Parsing ResultSet into message objects...");
        ArrayList<Message> messageList = new ArrayList<>();

        while (rs.next()) {
            try {
                messageList.add(parseMessage(rs));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messageList;
    }

    /**
     * Parses single message from ResultSet.
     *
     * @param rs ResultSet to parse.
     * @return parsed message.
     * @throws SQLException on a database access error or other errors.
     */
    private static Message parseMessage(ResultSet rs) throws SQLException {
        Message message = new Message();

        message.setMessage(rs.getString("message"));
        message.setMessageTime(rs.getTimestamp("message_time"));

        return message;
    }

    /**
     * Parses messages from the given result set and closes that ResultSet afterwards.
     *
     * @param rs ResultSet to parse.
     * @return list of parsed messages.
     * @throws SQLException on a database access error or other errors.
     */
    public static ArrayList<Message> loadMessages(ResultSet rs) throws SQLException {
        try (rs) {
            return parseMessages(rs);
        }
    }
}
