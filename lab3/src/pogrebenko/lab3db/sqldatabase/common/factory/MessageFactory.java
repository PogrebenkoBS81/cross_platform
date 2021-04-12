package pogrebenko.lab3db.sqldatabase.common.factory;

import pogrebenko.lab3db.sqldatabase.common.contract.IMessageDB;
import pogrebenko.lab3db.sqldatabase.database.mysql.message.MySQLMessage;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Represents an SQL message DB factory.
 * Returns required database under IMessageDB interface.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MessageFactory {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private MessageFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns MySQL messages database.
     *
     * @param host     host of the DB to connect.
     * @param port     port of the DB to connect.
     * @param dbName   database name of the DB to connect.
     * @param userName user of the DB to connect.
     * @param password user password of the DB to connect.
     * @return IMedicineDB interface with MySQL under it.
     * @throws SQLException on a database access error or other errors.
     */
    public static IMessageDB getMySQLDatabase(
            String host,
            int port,
            String dbName,
            String userName,
            String password
    ) throws SQLException {
        LOGGER.info("Creating new MySQL message database...");
        return new MySQLMessage(host, port, dbName, userName, password);
    }
}
