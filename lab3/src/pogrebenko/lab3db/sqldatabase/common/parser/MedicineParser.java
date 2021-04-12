package pogrebenko.lab3db.sqldatabase.common.parser;

import pogrebenko.lab3db.model.medicine.InvalidMedicineException;
import pogrebenko.lab3db.model.medicine.Medicine;
import pogrebenko.lab3db.model.medicine.MedicineID;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL medicine DB util, that parse ResultSet objects into medicine objects,
 * and contains various methods for usage in the medicine DB.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.3.0
 */
public final class MedicineParser {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private MedicineParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses medicines from the given result set.
     *
     * @param rs ResultSet to parse.
     * @return list of parsed medicines.
     * @throws SQLException on a database access error or other errors.
     */
    private static ArrayList<MedicineID> parseMedicines(ResultSet rs) throws SQLException {
        LOGGER.info("Parsing ResultSet into medicine objects...");
        ArrayList<MedicineID> medicineIDList = new ArrayList<>();

        while (rs.next()) {
            try {
                medicineIDList.add(parseMedicine(rs));
            } catch (InvalidMedicineException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return medicineIDList;
    }

    /**
     * Parses single medicine from ResultSet.
     *
     * @param rs ResultSet to parse.
     * @return parsed medicine.
     * @throws SQLException             on a database access error or other errors.
     * @throws InvalidMedicineException if invalid data were read from DB.
     */
    private static MedicineID parseMedicine(ResultSet rs) throws SQLException, InvalidMedicineException {
        LOGGER.finest("Extracting medicine from ResultSet...");
        MedicineID medicine = new MedicineID();

        medicine.setName(rs.getString("name"));
        medicine.setForm(rs.getString("form"));
        medicine.setProducer(rs.getString("producer"));
        medicine.setExpirationDate(rs.getDate("expiration_date"));
        medicine.setProductionDate(rs.getDate("production_date"));
        medicine.setCost(rs.getInt("cost"));
        medicine.setPrescriptionOnly(rs.getBoolean("prescription_only"));
        medicine.setId(rs.getInt("id"));

        return medicine;
    }

    /**
     * Parses medicines from the given result set and closes that ResultSet afterwards.
     *
     * @param rs ResultSet to parse.
     * @return list of parsed medicines.
     * @throws SQLException on a database access error or other errors.
     */
    public static ArrayList<MedicineID> loadMedicines(ResultSet rs) throws SQLException {
        LOGGER.finest("Trying to parse ResultSet object into the medicine objects...");

        try (rs) {
            return parseMedicines(rs);
        }
    }

    /**
     * Parses first medicine from the given result set and closes that ResultSet afterwards..
     *
     * @param rs ResultSet to parse.
     * @return parsed medicine.
     * @throws SQLException on a database access error or other errors.
     */
    public static MedicineID loadMedicine(ResultSet rs) throws SQLException {
        LOGGER.finest("Trying to parse ResultSet object into the single medicine object...");
        ArrayList<MedicineID> res = loadMedicines(rs);

        if (res.size() == 0) {
            LOGGER.info("No medicine found in ResultSet, returning null...");
            return null;
        }

        return res.get(0);
    }

    /**
     * Unwraps medicine object fields into an Object array. Used to simplify query preparation.
     * With this function, query preparation will look like: execute(Query, getMedicineParams(med)),
     * and no like: execute(Query, med.getName(), med.getForm(), ...).
     * Order of unwrapped arguments should correlate with order of params in each SQL query,
     * where this function will be used (SELECT a, b, c ....;  Object[]{a, b, c ...).
     *
     * @param medicine Medicine to unwrap into an Object array.
     * @return Object array with Medicine fields.
     */
    public static Object[] getMedicineParams(Medicine medicine) {
        LOGGER.finest("Unwrapping Medicine params...");

        return new Object[]{
                medicine.getName(),
                medicine.getForm(),
                medicine.getProducer(),
                medicine.getExpirationDate(),
                medicine.getProductionDate(),
                medicine.getCost(),
                medicine.getPrescriptionOnly()
        };
    }

    /**
     * Same as {@link pogrebenko.lab3db.sqldatabase.common.parser.MedicineParser#getMedicineParams},
     * but for MedicineID. It doesn't extend getMedicineParams in any way,
     * due to adding any extra object into const size array will be quite expensive performance-wise.
     *
     * @param medicine MedicineID to unwrap into an Object array.
     * @return Object array with MedicineID fields.
     */
    public static Object[] getMedicineIDParams(MedicineID medicine) {
        LOGGER.finest("Unwrapping MedicineID params...");

        return new Object[]{
                medicine.getName(),
                medicine.getForm(),
                medicine.getProducer(),
                medicine.getExpirationDate(),
                medicine.getProductionDate(),
                medicine.getCost(),
                medicine.getPrescriptionOnly(),
                medicine.getId()
        };
    }
}
