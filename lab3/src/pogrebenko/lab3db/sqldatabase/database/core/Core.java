package pogrebenko.lab3db.sqldatabase.database.core;

import pogrebenko.lab3db.sqldatabase.common.contract.ICore;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ICore SQL core implementation.
 * Handles all basic operations and options that will be common for any SQL database.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class Core implements ICore {
    // Statement and SQL ping timeouts.
    private final static int defaultTimeoutPing = 2;
    private final static int defaultTimeoutStmt = 3;

    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Connection string for given DB.
    private final String connString;
    // DB username of the current connection.
    private final String userName;

    // DB password of the current connection.
    // TODO: Somehow do not store passwords in memory.
    //  For now, password stored in memory all the time, which is very insecure.
    // For now, password is stored in memory to achieve
    // the ability of the DB reconnection (if, for example, DB goes down).
    // Password stored as char[] because Strings are immutable. That means once String is created,
    // there's no way (aside from reflection) we can get rid of the data before garbage collection.
    // And with an array, we can explicitly wipe the data after we done with it,
    // and the password won't be present anywhere in the system, even before garbage collection.
    private final char[] password;
    // DB connection
    private Connection conn = null;

    /**
     * Creates an SQL connection.
     *
     * @param connString connection string of the current DB.
     * @param driver     driver of the current DB to register.
     * @param userName   user of the DB to connect.
     * @param password   user password of the DB to conn
     * @throws SQLException on a database access error or other errors.
     */
    protected Core(
            String connString,
            Driver driver,
            String userName,
            String password
    ) throws SQLException {
        LOGGER.info("Trying to establish SQL connection...");

        this.connString = connString;
        this.userName = userName;
        this.password = password.toCharArray();
        // Register required SQL driver
        DriverManager.registerDriver(driver);
        open(userName, password);
    }

    /**
     * Opens a DB connection.
     *
     * @param userName username of the DB user.
     * @param password password of the DB user.
     * @throws SQLException on a database access error or other errors.
     */
    public synchronized void open(String userName, String password) throws SQLException {
        LOGGER.info("Opening DB... ");

        if (conn != null) {
            return;
        }

        conn = DriverManager.getConnection(connString, userName, password);
    }

    /**
     * Closes the DB connection.
     *
     * @throws SQLException on a database access error or other errors.
     */
    public synchronized void close() throws SQLException {
        LOGGER.info("Closing DB... ");
        Arrays.fill(password, ' ');

        if (conn == null) {
            return;
        }

        conn.close();
    }

    /**
     * Restarts the DB connection, in attempt to reconnect to DB.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private synchronized void restart() throws SQLException {
        LOGGER.info("Trying to restart DB... ");
        // Try to close connection anyway, even if it's already closed.
        try {
            conn.close();
        } catch (Exception ignored) {
        }

        conn = null;

        open(userName, new String(password));
    }

    /**
     * Executes the query built from the passed parameters, and returns the ResultSet of the execution.
     *
     * @param query  query to prepare.
     * @param params query parameters.
     * @return ResultSet with retrieved data
     * @throws SQLException on a database access error or other errors.
     */
    protected synchronized ResultSet executeQuery(String query, Object... params) throws SQLException {
        LOGGER.info("Executing SQL query... ");

        PreparedStatement exec = prepareStatement(getStatement(query), params);
        ResultSet rs = exec.executeQuery();
        exec.closeOnCompletion(); // Close statement after ResultSet is closed.

        return rs;
    }

    /**
     * Executes the prepared batch statement. Preferred way of executing large amount of, for example, insert queries.
     *
     * @param batchStmt batch to execute.
     * @return an array of update counts containing one element for each command in the batch.
     * @throws SQLException on a database access error or other errors.
     */
    @SuppressWarnings("UnusedReturnValue")
    protected synchronized int[] executeBatch(PreparedStatement batchStmt) throws SQLException {
        LOGGER.info("Executing SQL batch query... ");

        // TODO: Since there is only 1 thread, and all methods are "synchronized"
        //  (well, no point in this for now, but let it be), setting autocommit off and on again is fine.
        //  But if there will be multiple threads, complex, nested queries,
        //  (JDBC3+ problems with complex queries and autocommit)
        //  commit and rollback should be manual + connection pool is recommended.
        //  May use RWMutexes, with W on close\open\etc and R on execute methods.
        //  https://stackoverflow.com/questions/4453782/why-set-autocommit-to-true
        try (batchStmt) {
            // Disable auto-commit, because if it's on,
            // some SQL drivers may execute each query in batch separately,
            // and then the whole idea of increasing performance loses its meaning.
            conn.setAutoCommit(false);

            int[] res = batchStmt.executeBatch();
            // Commit changes after batch is executed.
            conn.commit();

            return res;
        } catch (SQLException e) {
            // Rollback changes if something goes wrong.
            conn.rollback();

            throw e;
        } finally {
            // Set autocommit on, so other methods would be simpler to implement.
            conn.setAutoCommit(true);
        }
    }

    /**
     * Executes the query built from the passed parameters, and returns the new generated id's, if any.
     *
     * @param query  query to prepare.
     * @param params query parameters.
     * @return newly generated keys.
     * @throws SQLException on a database access error or other errors.
     */
    protected synchronized ArrayList<Integer> execute(String query, Object... params) throws SQLException {
        LOGGER.info("Executing SQL statement... ");

        try (PreparedStatement exec = prepareStatement(getStatement(query), params)) {
            exec.execute();
            return getInsertedKeys(exec.getGeneratedKeys());
        }
    }

    /**
     * Checks for connection. If it's invalid - makes an attempt to reconnect via "restart" method.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private void ensureConnection() throws SQLException {
        try {
            // TODO: Checking connection with every query is a massive overkill.
            //  In golang i usually create a "pinger" function, that ping DB every N seconds,
            //  and do something if DB is down. But I'm not sure if we are allowed to use threads,
            //  so I`ll leave it like this for now.
            LOGGER.info("Checking SQL connection... ");

            if (conn == null) {
                throw new SQLException("Cannot do query on DB, connection is null!");
            }

            if (!conn.isValid(defaultTimeoutPing)) {
                throw new SQLException("Cannot do query on DB, connection isn't valid!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error, trying to reconnect to DB: " + e.getMessage(), e);
            restart();
        }
    }

    /**
     * Returns new statement from given connection with specified parameters.
     *
     * @param query the query from which the statement is formed.
     * @throws SQLException on a database access error or other errors.
     */
    protected PreparedStatement getStatement(String query) throws SQLException {
        LOGGER.info(String.format("Generating SQL statement with timeout %d... ", defaultTimeoutStmt));
        ensureConnection();
        // TODO: RETURN_GENERATED_KEYS depends on driver realisation, and some drivers may not work correctly.
        //  Better change it on explicit key definition (pass required key field to this function).
        //  It will be not so pretty, but more reliable.
        PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setQueryTimeout(defaultTimeoutStmt);
        return statement;
    }

    /**
     * Prepares statement for the execution.
     *
     * @param statement statement to prepare
     * @param params    query parameters.
     * @return prepared statement ready for the execution.
     * @throws SQLException on a database access error or other errors.
     */
    protected PreparedStatement prepareStatement(
            PreparedStatement statement,
            Object... params
    ) throws SQLException {
        LOGGER.finest("Preparing SQL statement...");
        // A little bit of an overhead here, but it's almost nothing,
        // and saves a lot amount of code. So it's fine.
        for (int i = 1; i < params.length + 1; i++) {
            statement.setObject(i, params[i - 1]);
        }

        return statement;
    }

    /**
     * Parses the auto generated keys from the ResultSet.
     *
     * @param rs ResultSet to parse.
     * @return array of generated indices.
     * @throws SQLException on a database access error or other errors.
     */
    private ArrayList<Integer> getInsertedKeys(ResultSet rs) throws SQLException {
        LOGGER.finest("Parsing SQL quto generated keys...");
        ArrayList<Integer> keys = new ArrayList<>();

        try (rs) {
            while (rs.next()) {
                keys.add(rs.getInt(1));
            }
        }

        return keys;
    }
}
