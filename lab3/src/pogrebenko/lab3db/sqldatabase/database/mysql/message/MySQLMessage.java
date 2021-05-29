package pogrebenko.lab3db.sqldatabase.database.mysql.message;

import pogrebenko.lab3db.model.message.Message;
import pogrebenko.lab3db.sqldatabase.common.contract.ICore;
import pogrebenko.lab3db.sqldatabase.common.contract.IMessageDB;
import pogrebenko.lab3db.sqldatabase.common.parser.MessageParser;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static pogrebenko.lab3db.sqldatabase.database.util.DBUtil.getKey;

/**
 * IMessageDB SQL implementation.
 * Represents an MySQL message DB, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MySQLMessage implements IMessageDB {

    private static final Logger LOGGER = LoggerWrapper.getLogger();
    ICore SQLCore;

    /**
     * Creates an MySQL connection for the message DB.
     *
     * @param SQLCore SQL core for queries execution.
     */
    public MySQLMessage(ICore SQLCore) {
        this.SQLCore = SQLCore;
    }

    /**
     * Prepares all required tables in Message DB for work.
     *
     * @throws SQLException on a database access error or other errors.
     */
    public void initialize() throws SQLException {
        LOGGER.info("Initializing MySQL Message DB...");

        createTable();
    }

    /**
     * Writes message to DB.
     *
     * @param message message to insert to DB.
     * @return index of the inserted message.
     * @throws SQLException on a database access error or other errors.
     */
    public int writeMessage(Message message) throws SQLException {
        LOGGER.info("Writing new message to DB...");
        int key = getKey(SQLCore.execute(Queries.INSERT_MESSAGE, message.getMessage()));
        LOGGER.info("New message generated ID is: " + key);

        return key;
    }

    /**
     * Returns all messages from DB.
     *
     * @return array of messages from DB.
     * @throws SQLException on a database access error or other errors.
     */
    public ArrayList<Message> getMessages() throws SQLException {
        LOGGER.info("Returning all messages from the DB...");

        return MessageParser.loadMessages(SQLCore.executeQuery(Queries.SELECT_MESSAGES));
    }

    /**
     * Deletes all values from messages table in DB.
     *
     * @throws SQLException on a database access error or other errors.
     */
    public void truncateTable() throws SQLException {
        LOGGER.info("Returning all messages from to DB...");

        SQLCore.execute(Queries.TRUNCATE_TABLE);
    }

    /**
     * Creates table for the messages.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private void createTable() throws SQLException {
        LOGGER.info("Creating table for the message DB if not exists... ");

        SQLCore.execute(Queries.CREATE_TABLE);
    }
}