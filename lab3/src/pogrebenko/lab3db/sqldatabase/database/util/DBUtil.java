package pogrebenko.lab3db.sqldatabase.database.util;

import java.sql.SQLException;
import java.util.ArrayList;

public class DBUtil {
    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private DBUtil() {
        throw new UnsupportedOperationException();
    }

    public static int getKey(ArrayList<Integer> keys) throws SQLException {
        if (keys.size() != 1) {
            throw new SQLException("Invalid number of rows inserted, expected 1, got " + keys.size());
        }

        return keys.get(0);
    }
}
