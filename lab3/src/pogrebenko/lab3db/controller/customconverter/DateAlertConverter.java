package pogrebenko.lab3db.controller.customconverter;

import javafx.util.converter.DateStringConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extends the DateStringConverter class, to change it's behaviour on invalid date insertion.
 * Without it, if GUI user insert invalid data (expiration\production), an uncatchable exception will be thrown.
 * <p>
 * But, according to lab2 task, there should be an error box for every error like this.
 * So, I extended DateStringConverter in a such way, that instead of exception, null will be returned.
 * This allows me to catch this in my code, and show an error alert box.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class DateAlertConverter extends DateStringConverter {
    /**
     * No changes in constructor is required, just use the existing one.
     *
     * @param fmt time format to parse.
     */
    public DateAlertConverter(SimpleDateFormat fmt) {
        super(fmt);
    }

    /**
     * Overloaded fromString, that doesn't throw an error on invalid data.
     * Required due to we need to show alert box on every invalid input error.
     *
     * @param dateSrt string to convert into a Date.
     */
    @Override
    public Date fromString(String dateSrt) throws RuntimeException {
        try { // Just call existing method, and catch an error.
            return super.fromString(dateSrt);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
