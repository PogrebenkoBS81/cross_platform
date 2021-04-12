package pogrebenko.lab3db.model.util;

import java.io.File;

/**
 * Util methods that may be used in various labs.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public final class ModelUtil {
    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private ModelUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes extension from given file. Throws an IllegalArgumentException if no "." in "fileName".
     *
     * @param fileName file name from which extension must be removed.
     * @throws IllegalArgumentException if there is no "." in "fileName".
     */
    public static String removeFileExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");

        if (pos < 0) {
            return fileName;
        }

        return fileName.substring(0, pos);
    }

    /**
     * Returns the extension of the given file name.
     *
     * @param fileName file name to parse.
     * @return file extension (without dot).
     * @throws IllegalArgumentException if there is no "." in "fileName".
     */
    public static String getFileExtension(String fileName) throws IllegalArgumentException {
        int pos = fileName.lastIndexOf('.');

        if (pos < 0) {
            throw new IllegalArgumentException(String.format("File %s doesn't have an extension!", fileName));
        }

        return fileName.substring(pos + 1);
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

    /**
     * Removes arbitrary number of chars from an end of the string.
     *
     * @param str     string to modify.
     * @param charNum number of chars to remove.
     * @return modified string.
     */
    public static String cutEndChars(String str, int charNum) {
        if (str != null && str.length() > charNum - 1) {
            str = str.substring(0, str.length() - charNum);
        }

        return str;
    }
}