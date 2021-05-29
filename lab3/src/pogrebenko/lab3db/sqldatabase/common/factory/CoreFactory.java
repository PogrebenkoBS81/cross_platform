package pogrebenko.lab3db.sqldatabase.common.factory;

import pogrebenko.lab3db.sqldatabase.common.contract.ICore;
import pogrebenko.lab3db.sqldatabase.database.core.Core;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Represents an SQL medicine DB factory.
 * Returns required database under IMedicineDB interface.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class CoreFactory {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private CoreFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns MySQL core database.
     *
     * @param host     host of the DB to connect.
     * @param port     port of the DB to connect.
     * @param dbName   database name of the DB to connect.
     * @param userName user of the DB to connect.
     * @param password user password of the DB to connect.
     * @return IMedicineDB interface with MySQL under it.
     * @throws SQLException on a database access error or other errors.
     */
    public static ICore getMySQLCore(
            String host,
            int port,
            String dbName,
            String userName,
            String password,
            String... params
    ) throws SQLException {
        LOGGER.info("Creating new MySQL DB core...");

        return new Core(
                String.format("jdbc:mysql://%s:%d/%s", host, port, dbName),
                new com.mysql.cj.jdbc.Driver(),
                userName,
                password,
                params
        );
    }
}
