package pogrebenko.lab3db.controller;

@SuppressWarnings("SameParameterValue")
public enum DBType {
    MYSQL("MySQL");
    //POSTGRESQL("PostgreSQL"),
    //etc....

    private final String label;

    DBType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}