package pogrebenko.labsix.util;

import java.io.File;

/**
 * Util methods that may be used in various labs.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public final class CommonUtil {
    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private CommonUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given file exists.
     *
     * @param fileName file name to check.
     * @return true if file exists, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isFileExists(String fileName) {
        if (fileName == null) {
            return false;
        }

        File f = new File(fileName);

        return f.exists() && !f.isDirectory();
    }
}