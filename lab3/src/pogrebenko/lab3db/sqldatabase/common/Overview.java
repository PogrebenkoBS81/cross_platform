package pogrebenko.lab3db.sqldatabase.common;

/**
 * 'common' package contains classes that will be used by any SQL implementation of any model.
 * If any new model will be added, then you can support it just by analogy with already present classes:
 * 1) Add the new model DB interface to abstraction package.
 * 2) Create factory for the new model that will have methods for creating supported SQL DBs.
 * 3) Create parsers that will be used by any SQL implementation of the new model.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
class Overview {
    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private Overview() {
        throw new UnsupportedOperationException();
    }
}
