package pogrebenko.lab3db.sqldatabase.database.mysql.tools;

import pogrebenko.lab3db.sqldatabase.common.contract.IToolsDB;
import pogrebenko.lab3db.sqldatabase.database.core.Core;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * IToolsDB SQL implementation.
 * Represents an SQL DB tools, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MySQLTools extends Core implements IToolsDB {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    /**
     * Creates an MySQL connection for the DB tools.
     *
     * @param host     host of the DB to connect.
     * @param port     port of the DB to connect.
     * @param dbName   database name of the DB to connect.
     * @param userName user of the DB to connect.
     * @param password user password of the DB to connect.
     * @throws SQLException on a database access error or other errors.
     */
    public MySQLTools(
            String host,
            int port,
            String dbName,
            String userName,
            String password
    ) throws SQLException {
        super(
                String.format("jdbc:mysql://%s:%d/%s", host, port, dbName),
                new com.mysql.cj.jdbc.Driver(),
                userName,
                password
        );
    }

    /**
     * Creates DB with given name.
     *
     * @param dbName name of the DB to create.
     * @throws SQLException on a database access error or other errors.
     */
    public void createDB(String dbName) throws SQLException {
        LOGGER.info("Trying to create new DB with name: " + dbName);
        // Check if DB name is valid.
        if (!dbName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new SQLException("Invalid DB name provided!");
        }

        execute(Queries.CREATE_DATABASE + " " + dbName);
    }
}
