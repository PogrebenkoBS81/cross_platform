package pogrebenko.lab3db.sqldatabase.database.mysql.tools;

import pogrebenko.lab3db.sqldatabase.common.contract.ICore;
import pogrebenko.lab3db.sqldatabase.common.contract.IToolsDB;
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
public class MySQLTools implements IToolsDB {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    ICore SQLCore;

    /**
     * Creates an MySQL connection for the DB tools.
     *
     * @param SQLCore SQL core for queries execution.
     */
    public MySQLTools(ICore SQLCore) {
        this.SQLCore = SQLCore;
    }

    /**
     * Creates DB with given name.
     *
     * @param dbName name of the DB to create.
     * @throws SQLException on a database access error or other errors.
     */
    public void createDB(String dbName) throws SQLException {
        LOGGER.info("Trying to create new DB with name: " + dbName);

        SQLCore.execute(Queries.CREATE_DATABASE + String.format(" `%s`", dbName));
    }
}
