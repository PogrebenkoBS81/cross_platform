package pogrebenko.lab3db.sqldatabase.database.mysql.message;

/**
 * Util class that holds all required queries for the MySQL message DB.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
class Queries {
    public static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS message (
            	message TEXT NULL,
            	message_time DATETIME NULL,
            	message_id INT UNSIGNED auto_increment NOT NULL,
            	CONSTRAINT message_PK PRIMARY KEY (message_id)
            )""";
    public static final String SELECT_MESSAGES = """
            SELECT
                m.message_time,
                m.message
            FROM
                message m
            ORDER BY m.message_time DESC
            """;
    public static final String INSERT_MESSAGE = """
            INSERT INTO message (
                message,
                message_time
            ) VALUES (?, NOW())
            """;
    public static final String TRUNCATE_TABLE = "truncate message";

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private Queries() {
        throw new UnsupportedOperationException();
    }
}
