package pogrebenko.lab3db.sqldatabase.database.mysql.medicine;

/**
 * Possible filters for the medicine DB.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public enum FilterField {
    NONE("None"),
    NAME("Name"),
    FORM("Form"),
    PRODUCER("Producer"),
    EXPIRATION_DATE("Exp. date"),
    PRODUCTION_DATE("Prod. date"),
    COST("Cost"),
    PRESCRIPTION_ONLY("PS only"),
    ID("ID");

    private final String label;

    /**
     * Constructs FilterField.
     *
     * @param label name of the filter.
     */
    FilterField(String label) {
        this.label = label;
    }

    /**
     * Transforms the FilterField to string.
     *
     * @return name of the filter.
     */
    public String toString() {
        return label;
    }
}