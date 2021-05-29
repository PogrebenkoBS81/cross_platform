package pogrebenko.lab3db.sqldatabase.common.contract;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    /**
     * Executes the query built from the passed parameters, and returns the new generated id's, if any.
     *
     * @param query  query to prepare.
     * @param params query parameters.
     * @return newly generated keys.
     * @throws SQLException on a database access error or other errors.
     */
    ArrayList<Integer> execute(String query, Object... params) throws SQLException;

    /**
     * Executes the query built from the passed parameters, and returns the ResultSet of the execution.
     *
     * @param query  query to prepare.
     * @param params query parameters.
     * @return ResultSet with retrieved data
     * @throws SQLException on a database access error or other errors.
     */
    ResultSet executeQuery(String query, Object... params) throws SQLException;

    /**
     * Executes the prepared batch statement. Preferred way of executing large amount of, for example, insert queries.
     *
     * @param batchStmt batch to execute.
     * @return an array of update counts containing one element for each command in the batch.
     * @throws SQLException on a database access error or other errors.
     */
    @SuppressWarnings("UnusedReturnValue")
    int[] executeBatch(PreparedStatement batchStmt) throws SQLException;

    /**
     * Returns new statement from given connection with specified parameters.
     *
     * @param query the query from which the statement is formed.
     * @throws SQLException on a database access error or other errors.
     */
    PreparedStatement getStatement(String query) throws SQLException;

    /**
     * Prepares statement for the execution.
     *
     * @param statement statement to prepare
     * @param params    query parameters.
     * @return prepared statement ready for the execution.
     * @throws SQLException on a database access error or other errors.
     */
    PreparedStatement prepareStatement(PreparedStatement statement, Object... params) throws SQLException;
}
