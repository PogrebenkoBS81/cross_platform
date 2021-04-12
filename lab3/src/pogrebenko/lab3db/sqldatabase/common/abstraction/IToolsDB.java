package pogrebenko.lab3db.sqldatabase.common.abstraction;

import java.sql.SQLException;

/**
 * Represents an SQL DB tools, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public interface IToolsDB extends ICore {
    /**
     * Creates database with provided name.
     *
     * @param dbName database name to create.
     * @throws SQLException on a database access error or other errors.
     */
    void createDB(String dbName) throws SQLException;
}
