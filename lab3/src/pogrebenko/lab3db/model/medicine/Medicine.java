package pogrebenko.lab3db.model.medicine;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents a medicine.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.0.0
 */
public class Medicine implements Comparable<Medicine> {
    // Formatting for the date fields.
    public final static SimpleDateFormat DateFmt = new SimpleDateFormat("dd/MM/yyyy");
    // Indicates the number of "real" object fields.
    private static final int fieldsNum = 7;
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    // Create fieldName:setter map for simple initialization of object from csv\tsv\etc...
    final Map<String, MedicineSetter<String>> fieldSetterMap;
    // Sorted by data type and "importance" of the field.
    private String name = "";
    private String form = "";
    private String producer = "";
    private Date expirationDate = null;
    private Date productionDate = null;
    private Integer cost = 0;
    private Boolean isPrescriptionOnly = false;

    // Prepare fieldSetterMap, set every field from data file to corresponding method.
    {
        fieldSetterMap = new HashMap<>();
        fieldSetterMap.put("name", this::setName);
        fieldSetterMap.put("form", this::setForm);
        fieldSetterMap.put("producer", this::setProducer);
        fieldSetterMap.put("expirationDate", this::setExpirationDate);
        fieldSetterMap.put("productionDate", this::setProductionDate);
        fieldSetterMap.put("cost", this::setCost);
        fieldSetterMap.put("isPrescriptionOnly", this::setPrescriptionOnly);
    }

    /**
     * Constructs new empty medicine.
     */
    public Medicine() {
    }

    /**
     * Constructs medicine from given parameters.
     *
     * @param name               medicine name.
     * @param form               medicine form.
     * @param producer           medicine producer.
     * @param expirationDate     medicine expirationDate.
     * @param productionDate     medicine productionDate.
     * @param cost               medicine cost.
     * @param isPrescriptionOnly is medicine for prescription only.
     * @throws InvalidMedicineException if invalid params were passed.
     */
    public Medicine(
            String name,
            String form,
            String producer,
            Date expirationDate,
            Date productionDate,
            Integer cost,
            Boolean isPrescriptionOnly
    ) throws InvalidMedicineException {
        setName(name);
        setForm(form);
        setProducer(producer);
        setExpirationDate(expirationDate);
        setProductionDate(productionDate);
        setCost(cost);
        setPrescriptionOnly(isPrescriptionOnly);
    }

    /**
     * Constructs medicine from given headers and param string separated be the given delimiter.
     *
     * @param headers      csv headers in some specific order.
     * @param stringValues string of values in the same order as headers.
     * @param delimiter    delimiter of the stringValues.
     * @throws InvalidMedicineException if invalid number of headers or values were passed.
     */
    public Medicine(String[] headers, String stringValues, String delimiter) throws InvalidMedicineException {
        // Simple log without printing passed data due to it will be print in setHeaderField function.
        // And since there is no async methods to mess up logging order, it will be totally fine.
        LOGGER.fine("Creating new object, checking input string for validity ...");
        // Check for null values to avoid dealing with exceptions related it.
        if (stringValues == null)
            throw new InvalidMedicineException("Input values string cannot be empty!");
        // No check for delimiter due to no user input for it provided, so  no error possible.
        String[] values = stringValues.split(delimiter);
        // Check if given data valid.
        if (headers.length != fieldsNum || values.length != fieldsNum) {
            throw new InvalidMedicineException(
                    String.format(
                            "Length of headers (%d) or values (%d) doesn't match the number of the class fields (%d)",
                            headers.length,
                            values.length,
                            fieldsNum
                    )
            );
        }

        LOGGER.fine("Setting object values...");
        // Set values to the object via fieldSetterMap.
        for (int i = 0; i < headers.length; i++) {
            MedicineSetter<String> method = fieldSetterMap.get(headers[i]);

            if (method == null) {
                LOGGER.warning("Given unrecognizable header field: " + headers[i]);
                continue;
            }

            method.setField(values[i]);
        }
    }

    /**
     * Returns the name a medicine.
     *
     * @return string with the medicine name.
     */
    public String getName() {
        // TODO: add "finest" logging
        // No logging for now, due to there is almost nothing to log.
        // Will be fixed in future, if necessary.
        return this.name;
    }

    /**
     * Sets the medicine name.
     *
     * @param name the name of the medicine.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the dosage form of a medicine.
     *
     * @return string with the medicine dosage form.
     */
    public String getForm() {
        return this.form;
    }

    /**
     * Sets the medicine form.
     *
     * @param form the form of the medicine.
     */
    public void setForm(String form) {
        this.form = form;
    }

    /**
     * Returns the producer of a medicine.
     *
     * @return string with the producer of the medicine.
     */
    public String getProducer() {
        return this.producer;
    }

    /**
     * Sets the medicine producer.
     *
     * @param producer the producer of the medicine.
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * Returns the expiration date of a medicine (returning a string were required in the lab).
     *
     * @return string with the medicine expiration date.
     */
    public Date getExpirationDate() {
        return this.expirationDate;
    }

    /**
     * Sets the medicine expiration date.
     *
     * @param expirationDate the expiration date of the medicine.
     * @throws InvalidMedicineException if production date is not null, and it's after expiration date.
     */
    public void setExpirationDate(Date expirationDate) throws InvalidMedicineException {
        if (getProductionDate() != null && expirationDate.before(getProductionDate())) {
            throw new InvalidMedicineException("Expiration date cannot be before production date!");
        }

        this.expirationDate = expirationDate;
    }

    /**
     * Converts string to Date object and sets the medicine expiration date.
     *
     * @param expirationDate the expiration date of the medicine as string.
     * @throws InvalidMedicineException if production date is not null, and it's after expiration date.
     */
    public void setExpirationDate(String expirationDate) throws InvalidMedicineException {
        try {
            setExpirationDate(DateFmt.parse(expirationDate));
        } catch (ParseException e) {
            throw new InvalidMedicineException("Invalid string date!");
        }
    }

    /**
     * Returns the production date of a medicine.
     *
     * @return string with the medicine production date.
     */
    public Date getProductionDate() {
        return this.productionDate;
    }

    /**
     * Sets the medicine production date.
     *
     * @param productionDate the production date of the medicine.
     * @throws InvalidMedicineException if expiration date is not null, and it's before production date.
     */
    public void setProductionDate(Date productionDate) throws InvalidMedicineException {
        if (getExpirationDate() != null && getExpirationDate().before(productionDate)) {
            throw new InvalidMedicineException("Production date  cannot be before expiration date!");
        }

        this.productionDate = productionDate;
    }

    /**
     * Converts string to Date object and sets the medicine production date.
     *
     * @param productionDate the expiration date of the medicine as string.
     * @throws InvalidMedicineException if expiration date is not null, and it's before production date.
     */
    public void setProductionDate(String productionDate) throws InvalidMedicineException {
        try {
            setProductionDate(DateFmt.parse(productionDate));
        } catch (ParseException e) {
            throw new InvalidMedicineException("Invalid string date! ");
        }
    }

    /**
     * Returns the cost of a medicine.
     *
     * @return string with the medicine dosage form.
     */
    public Integer getCost() {
        return this.cost;
    }

    /**
     * Sets the medicine cost.
     *
     * @param cost the cost of the medicine.
     * @throws InvalidMedicineException if below zero cost was passed to the method.
     */
    public void setCost(Integer cost) throws InvalidMedicineException {
        if (cost < 0) {
            throw new InvalidMedicineException("Cannot set the negative cost for the medicine!");
        }

        this.cost = cost;
    }

    /**
     * Converts cost from string to an integer, and sets it to the object.
     *
     * @param cost the cost of the medicine as string.
     * @throws InvalidMedicineException if invalid date was passed to the method.
     */
    public void setCost(String cost) throws InvalidMedicineException {
        try {
            setCost(Integer.parseInt(cost));
        } catch (NumberFormatException e) {
            throw new InvalidMedicineException("Cost must be a number! ");
        }
    }

    /**
     * Returns if the medicine is sold by prescription only.
     *
     * @return if the medicine is sold by prescription only.
     */
    public Boolean getPrescriptionOnly() {
        return this.isPrescriptionOnly;
    }

    /**
     * Sets if the medicine is sold by prescription only.
     *
     * @param isPrescriptionOnly if the medicine is sold by prescription only.
     */
    public void setPrescriptionOnly(boolean isPrescriptionOnly) {
        this.isPrescriptionOnly = isPrescriptionOnly;
    }

    /**
     * Converts prescription status from string to a boolean val, and sets it to the object.
     *
     * @param isPrescriptionOnly if the medicine is sold by prescription only ('true'/'false' as string).
     */
    public void setPrescriptionOnly(String isPrescriptionOnly) {
        setPrescriptionOnly(Boolean.parseBoolean(isPrescriptionOnly));
    }

    /**
     * Converts object to the JSON string.
     * TODO: input with '"' will break json. Should add proper escaping or use external lib for marshalling.
     *
     * @return JSON representation of an object.
     */
    public String toJson() {
        return ("{"
                + "\n\t\"name\": " + "\"" + getName() + "\""
                + ",\n\t\"form\": " + "\"" + getForm() + "\""
                + ",\n\t\"producer\": " + "\"" + getProducer() + "\""
                + ",\n\t\"expirationDate\": " + "\"" + Medicine.DateFmt.format(getExpirationDate()) + "\""
                + ",\n\t\"productionDate\": " + "\"" + Medicine.DateFmt.format(getProductionDate()) + "\""
                + ",\n\t\"cost\": " + getCost()
                + ",\n\t\"prescriptionOnly\": " + "\"" + getPrescriptionOnly() + "\""
                + "}");
    }

    /**
     * Converts object to the string.
     *
     * @return string representation of an object.
     */
    @Override
    public String toString() {
        return "\nMedicine obj:" +
                "\n\tname = " + getName() +
                "\n\tform = " + getForm() +
                "\n\tproducer = " + getProducer() +
                "\n\texpirationDate = " + getExpirationDate() +
                "\n\tproductionDate = " + getProductionDate() +
                "\n\tcost = " + getCost() +
                "\n\tprescriptionOnly = " + getPrescriptionOnly();

    }

    /**
     * Implements Comparable interface. Firstly compares by name, then by producer of the medicine.
     *
     * @param other the other object to compare with.
     */
    @Override
    public int compareTo(Medicine other) {
        int result = this.getName().compareTo(other.getName());
        // If values are not equal - return result.
        if (result != 0) {
            return result;
        }
        // Compare by producer otherwise.
        return this.getProducer().compareTo(other.getProducer());
    }

    // Functional interface for setters.
    @FunctionalInterface
    public interface MedicineSetter<T> {
        /**
         * Interface that describes Medicine setters.
         *
         * @param t parameter to set.
         * @throws InvalidMedicineException if invalid data was passed.
         */
        void setField(T t) throws InvalidMedicineException;
    }
}
