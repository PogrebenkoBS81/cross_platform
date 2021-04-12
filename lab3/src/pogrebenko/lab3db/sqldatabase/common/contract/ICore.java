package pogrebenko.lab3db.sqldatabase.common.contract;

import java.sql.SQLException;

/**
 * Represents an SQL core, that will handle all basic operations and options.
 * Implements AutoCloseable, so auto-resource management with try(...) is possible.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public interface ICore extends AutoCloseable {
    /**
     * Opens a DB connection.
     *
     * @param userName username of the DB user.
     * @param password password of the DB user.
     * @throws SQLException on a database access error or other errors.
     */
    @SuppressWarnings("unused")
    void open(String userName, String password) throws SQLException;

    /**
     * Closes the DB connection.
     *
     * @throws SQLException on a database access error or other errors.
     */
    void close() throws SQLException;
}
