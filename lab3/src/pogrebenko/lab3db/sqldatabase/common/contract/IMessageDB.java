package pogrebenko.lab3db.sqldatabase.common.contract;

import pogrebenko.lab3db.model.message.Message;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents an SQL message DB, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
@SuppressWarnings("UnusedReturnValue")
public interface IMessageDB extends ICore {
    /**
     * Prepares all required tables in DB for work.
     *
     * @throws SQLException on a database access error or other errors.
     */
    void initialize() throws SQLException;

    /**
     * Deletes all values from messages table in DB.
     *
     * @throws SQLException on a database access error or other errors.
     */
    void truncateTable() throws SQLException;

    /**
     * Writes message to DB.
     *
     * @param message message to insert to DB.
     * @return index of the inserted message.
     * @throws SQLException on a database access error or other errors.
     */
    int writeMessage(Message message) throws SQLException;

    /**
     * Returns all messages from DB.
     *
     * @return array of messages from DB.
     * @throws SQLException on a database access error or other errors.
     */
    ArrayList<Message> getMessages() throws SQLException;
}
