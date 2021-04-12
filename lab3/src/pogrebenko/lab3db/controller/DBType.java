package pogrebenko.lab3db.controller;

/**
 * Supported DBs for the medicines.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
@SuppressWarnings("SameParameterValue")
public enum DBType {
    MYSQL("MySQL");
    //POSTGRESQL("PostgreSQL"),
    //etc....

    private final String label;

    /**
     * Constructs DBType.
     *
     * @param label label of the DB.
     */
    DBType(String label) {
        this.label = label;
    }

    /**
     * Returns the DB label.
     *
     * @return string representation of the DBType.
     */
    public String toString() {
        return label;
    }
}