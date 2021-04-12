package pogrebenko.lab3db.sqldatabase.common.abstraction;

import pogrebenko.lab3db.model.medicine.Medicine;
import pogrebenko.lab3db.model.medicine.MedicineID;
import pogrebenko.lab3db.sqldatabase.database.mysql.medicine.FilterField;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents an SQL medicine DB, that will handle all requests that required for the controller.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public interface IMedicineDB extends ICore {
    /**
     * Prepares all required tables in DB for work.
     *
     * @throws SQLException on a database access error or other errors.
     */
    void initialize() throws SQLException;

    /**
     * Deletes all values from medicine table in DB.
     *
     * @throws SQLException on a database access error or other errors.
     */
    void truncateTable() throws SQLException;

    /**
     * Writes medicines array to DB. Preferred way of the implementation - execute batch (due to performance boost).
     * No indices are returned due to most SQL drivers can't return indices generated via batch insert.
     * For now I heard only about PostgreSQL driver that supports that feature.
     * Other drivers may give unpredictable results.
     *
     * @param medicines medicines to insert via batch.
     * @throws SQLException on a database access error or other errors.
     */
    void writeMedicines(ArrayList<? extends Medicine> medicines) throws SQLException;

    /**
     * Writes medicine to DB.
     *
     * @param medicine medicine to insert to DB.
     * @return index of the inserted medicine.
     * @throws SQLException on a database access error or other errors.
     */
    int writeMedicine(Medicine medicine) throws SQLException;

    /**
     * Returns medicines filtered by the specified parameters (adds WHERE clause with given parameters).
     *
     * @param filter the medicine field by which the filtering will be performed.
     * @param param  the medicine field value by which the filtering will be performed.
     * @return array of medicines from DB that have been filtered by the specified field and its value.
     * @throws SQLException on a database access error or other errors.
     */
    ArrayList<MedicineID> getFilteredMedicines(FilterField filter, Object param) throws SQLException;

    /**
     * Returns all medicines from DB. Simplified usage of getFilteredMedicines(NONE, null)
     *
     * @return array of medicines from DB.
     * @throws SQLException on a database access error or other errors.
     */
    ArrayList<MedicineID> getMedicines() throws SQLException;

    /**
     * Returns medicine with given ID. Simplified usage of getFilteredMedicines(ID, id).
     *
     * @param medicineID id of the medicine to get from DB.
     * @throws SQLException on a database access error or other errors.
     */
    MedicineID getMedicine(int medicineID) throws SQLException;

    /**
     * Updates medicine with given ID with new parameters.
     *
     * @param medicine updated medicine to insert into DB.
     * @throws SQLException on a database access error or other errors.
     */
    void updateMedicine(MedicineID medicine) throws SQLException;

    /**
     * Deletes medicine with given ID.
     *
     * @param medicineID ID of the medicine to delete.
     * @throws SQLException on a database access error or other errors.
     */
    void deleteMedicine(int medicineID) throws SQLException;
}
