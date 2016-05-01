package cz.muni.fi.pv168.library.gui;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by robert on 28.4.2016.
 */
public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private String pattern = "dd-MM-yyyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);


    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormat.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormat.format(cal.getTime());
        }

        return "";
    }
}
