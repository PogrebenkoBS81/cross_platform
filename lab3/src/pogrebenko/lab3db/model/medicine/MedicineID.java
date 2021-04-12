package pogrebenko.lab3db.model.medicine;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.util.Date;
import java.util.logging.Logger;

import static pogrebenko.lab3db.model.util.ModelUtil.cutEndChars;

/**
 * Extends the Medicine class, adds ID to an object.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.0.0
 */
public class MedicineID extends Medicine {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Global id to keep track of an object's numbers.
    // ID of the current medicine.
    private Integer id;

    public MedicineID() {
        super();
    }

    public MedicineID(
            String name,
            String form,
            String producer,
            Date expirationDate,
            Date productionDate,
            Integer cost,
            Boolean isPrescriptionOnly,
            Integer id
    ) throws InvalidMedicineException {
        super(name, form, producer, expirationDate, productionDate, cost, isPrescriptionOnly);
        setId(id);
    }

    /**
     * Constructs medicineID from given headers and param string separated be the given delimiter.
     * (Passing unseparated string to the main constructor was required in lab1)
     *
     * @param headers      csv headers in some specific order.
     * @param stringValues string of values in the same order as headers.
     * @param delimiter    delimiter of the stringValues.
     * @throws InvalidMedicineException if invalid number of headers or values were passed.
     */
    public MedicineID(String[] headers, String stringValues, String delimiter) throws InvalidMedicineException {
        super(headers, stringValues, delimiter);

        LOGGER.info("Id for the new MedicineID object: ");
    }

    /**
     * Returns the id of the medicine.
     *
     * @return id of the medicine.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the id of the medicine.
     *
     * @param id id of the current medicine.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Converts object to the string.
     *
     * @return string representation of an object.
     */
    @Override
    public String toString() {
        return super.toString() + "\n\tID = " + getId();

    }

    /**
     * Converts object to the JSON string.
     *
     * @return JSON representation of an object.
     */
    @Override
    public String toJson() {
        // Remove closing brace.
        String superJSON = cutEndChars(super.toJson(), 1);
        // Add ID to JSON:
        return String.format("%s,\n\t\"ID\": %d \n}", superJSON, id);
    }
}
