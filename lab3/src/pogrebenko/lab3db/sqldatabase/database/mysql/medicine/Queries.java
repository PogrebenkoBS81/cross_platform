package pogrebenko.lab3db.sqldatabase.database.mysql.medicine;

/**
 * Util class that holds all required queries for the MySQL medicine DB.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
class Queries {
    public static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS medicine (
                name TEXT NULL,
                form TEXT NULL,
                producer TEXT NULL,
                expiration_date DATE NULL,
                production_date DATE NULL,
                cost INT UNSIGNED NULL,
                prescription_only BOOL NULL,
                id INT UNSIGNED auto_increment NOT NULL,
                CONSTRAINT medicine_PK PRIMARY KEY (id)
            )""";
    public static final String INSERT_MEDICINE = """
            INSERT INTO medicine (
                name,
                form,
                producer,
                expiration_date,
                production_date,
                cost,
                prescription_only
            ) VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    public static final String UPDATE_MEDICINE = """
            UPDATE
                medicine m
            SET
                m.name = ?,
                m.form = ?,
                m.producer = ?,
                m.expiration_date = ?,
                m.production_date = ?,
                m.cost = ?,
                m.prescription_only = ?
            WHERE
                m.id = ?;
            """;
    public static final String SELECT_MEDICINES = """
            SELECT
                m.name,
                m.form,
                m.producer,
                m.expiration_date,
                m.production_date,
                m.cost,
                m.prescription_only,
                m.id
            FROM
                medicine m
            """;
    public static final String DELETE_MEDICINE = """
            DELETE FROM medicine m
                WHERE m.id = ?
            """;
    public static final String TRUNCATE_TABLE = "truncate medicine";
    public static final String NAME_FILTER = SELECT_MEDICINES + "WHERE m.name = ?";
    public static final String FORM_FILTER = SELECT_MEDICINES + "WHERE m.form = ?";
    public static final String PRODUCER_FILTER = SELECT_MEDICINES + "WHERE m.producer = ?";
    public static final String EXPIRATION_DATE_FILTER = SELECT_MEDICINES + "WHERE m.expiration_date = ?";
    public static final String PRODUCTION_DATE_FILTER = SELECT_MEDICINES + "WHERE m.production_date = ?";
    public static final String COST_FILTER = SELECT_MEDICINES + "WHERE m.cost = ?";
    public static final String PRESCRIPTION_ONLY_FILTER = SELECT_MEDICINES + "WHERE m.prescription_only = ?";
    public static final String ID_FILTER = SELECT_MEDICINES + "WHERE m.id = ?";

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private Queries() {
        throw new UnsupportedOperationException();
    }
}
