package pogrebenko.lab3db.sqldatabase.database.mysql.medicine;

import pogrebenko.lab3db.model.medicine.Medicine;
import pogrebenko.lab3db.model.medicine.MedicineID;
import pogrebenko.lab3db.sqldatabase.common.contract.ICore;
import pogrebenko.lab3db.sqldatabase.common.contract.IMedicineDB;
import pogrebenko.lab3db.sqldatabase.common.parser.MedicineParser;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static pogrebenko.lab3db.sqldatabase.database.util.DBUtil.getKey;

/**
 * IMedicineDB SQL implementation.
 * Represents an MySQL medicine DB, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public class MySQLMedicine implements IMedicineDB {

    private static final Logger LOGGER = LoggerWrapper.getLogger();
    ICore SQLCore;

    /**
     * Creates an MySQL connection for the medicine DB.
     *
     * @param SQLCore SQL core for queries execution.
     */
    public MySQLMedicine(ICore SQLCore) {
        this.SQLCore = SQLCore;
    }

    /**
     * Prepares all required tables in Medicine DB for work.
     *
     * @throws SQLException on a database access error or other errors.
     */
    public void initialize() throws SQLException {
        LOGGER.info("Initializing MySQL Medicine DB...");

        createTable();
    }

    /**
     * Writes medicines array to DB.
     *
     * @param medicines medicines to insert via batch.
     * @throws SQLException on a database access error or other errors.
     */
    public void writeMedicines(ArrayList<? extends Medicine> medicines) throws SQLException {
        LOGGER.info("Writing medicines to DB...");
        PreparedStatement stmt = SQLCore.getStatement(Queries.INSERT_MEDICINE);

        for (Medicine medicine : medicines) {
            SQLCore.prepareStatement(stmt, MedicineParser.getMedicineParams(medicine));
            stmt.addBatch();
        }

        SQLCore.executeBatch(stmt);
    }

    /**
     * Writes medicine to DB.
     *
     * @param medicine medicine to insert to DB.
     * @return index of the inserted medicine.
     * @throws SQLException on a database access error or other errors.
     */
    public int writeMedicine(Medicine medicine) throws SQLException {
        LOGGER.info("Writing new medicine to DB...");
        int key = getKey(SQLCore.execute(Queries.INSERT_MEDICINE, MedicineParser.getMedicineParams(medicine)));
        LOGGER.info("New medicine generated ID is: " + key);

        return key;
    }

    /**
     * Returns all medicines from DB. Simplified usage of getFilteredMedicines(NONE, null)
     *
     * @return array of medicines from DB.
     * @throws SQLException on a database access error or other errors.
     */
    public ArrayList<MedicineID> getMedicines() throws SQLException {
        LOGGER.info("Returning all medicines from the DB...");

        return MedicineParser.loadMedicines(SQLCore.executeQuery(Queries.SELECT_MEDICINES));
    }

    /**
     * Returns medicine with given ID. Simplified usage of getFilteredMedicines(ID, id).
     *
     * @param medicineID id of the medicine to get from DB.
     * @throws SQLException on a database access error or other errors.
     */
    public MedicineID getMedicine(int medicineID) throws SQLException {
        LOGGER.info("Returning single from DB, medicine ID: " + medicineID);

        return MedicineParser.loadMedicine(SQLCore.executeQuery(Queries.ID_FILTER, medicineID));
    }

    /**
     * Updates medicine with given ID with new parameters.
     *
     * @param medicine updated medicine to insert into DB.
     * @throws SQLException on a database access error or other errors.
     */
    public void updateMedicine(MedicineID medicine) throws SQLException {
        LOGGER.info("Updating medicine with ID: " + medicine.getId());

        SQLCore.execute(Queries.UPDATE_MEDICINE, MedicineParser.getMedicineIDParams(medicine));
    }

    /**
     * Deletes medicine with given ID.
     *
     * @param medicineID ID of the medicine to delete.
     * @throws SQLException on a database access error or other errors.
     */
    public void deleteMedicine(int medicineID) throws SQLException {
        LOGGER.info("Deleting medicine with ID: " + medicineID);

        SQLCore.execute(Queries.DELETE_MEDICINE, medicineID);
    }

    /**
     * Creates table for the medicines.
     *
     * @throws SQLException on a database access error or other errors.
     */
    private void createTable() throws SQLException {
        LOGGER.info("Creating table for the medicine DB if not exists... ");

        SQLCore.execute(Queries.CREATE_TABLE);
    }

    /**
     * Deletes all values from medicine table in DB.
     *
     * @throws SQLException on a database access error or other errors.
     */
    public void truncateTable() throws SQLException {
        LOGGER.info("Truncating table with medicines... ");

        SQLCore.execute(Queries.TRUNCATE_TABLE);
    }

    /**
     * Returns medicines filtered by the specified parameters (adds WHERE clause with given parameters).
     *
     * @param filter the medicine field by which the filtering will be performed.
     * @param param  the medicine field value by which the filtering will be performed.
     * @return array of medicines from DB that have been filtered by the specified field and its value.
     * @throws SQLException on a database access error or other errors.
     */
    public ArrayList<MedicineID> getFilteredMedicines(FilterField filter, Object param) throws SQLException {
        LOGGER.info(String.format("Returning medicines from db with filter '%s' and value '%s': ", filter, param));
        ResultSet rs = null;

        switch (filter) {
            case NAME -> rs = SQLCore.executeQuery(Queries.NAME_FILTER, param);
            case FORM -> rs = SQLCore.executeQuery(Queries.FORM_FILTER, param);
            case PRODUCER -> rs = SQLCore.executeQuery(Queries.PRODUCER_FILTER, param);
            case EXPIRATION_DATE -> rs = SQLCore.executeQuery(Queries.EXPIRATION_DATE_FILTER, param);
            case PRODUCTION_DATE -> rs = SQLCore.executeQuery(Queries.PRODUCTION_DATE_FILTER, param);
            case COST -> rs = SQLCore.executeQuery(Queries.COST_FILTER, param);
            case PRESCRIPTION_ONLY -> rs = SQLCore.executeQuery(Queries.PRESCRIPTION_ONLY_FILTER, param);
            case ID -> rs = SQLCore.executeQuery(Queries.ID_FILTER, param);
            case NONE -> rs = SQLCore.executeQuery(Queries.SELECT_MEDICINES);
        }

        return MedicineParser.loadMedicines(rs);
    }
}