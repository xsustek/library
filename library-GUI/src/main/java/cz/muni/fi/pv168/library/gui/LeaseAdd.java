package cz.muni.fi.pv168.library.gui;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.util.Properties;

/**
 * Created by robert on 25.4.2016.
 */
public class LeaseAdd {
    private JButton cancelButton;
    private JComboBox cbBook;
    private JComboBox cbCustomer;
    private JButton addButton;
    private JDatePickerImpl endTimeDatePicker;
    private JPanel leaseAddPanel;
    private JDatePickerImpl realTimeDatePicker;

    private void createUIComponents() {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        UtilDateModel endTimeDateModel = new UtilDateModel();
        JDatePanelImpl endTimeDatePanel = new JDatePanelImpl(endTimeDateModel, p);
        endTimeDatePicker = new JDatePickerImpl(endTimeDatePanel, new DateLabelFormatter());

        UtilDateModel realTimeDateModel = new UtilDateModel();
        JDatePanelImpl realTimeDatePanel = new JDatePanelImpl(realTimeDateModel, p);
        realTimeDatePicker = new JDatePickerImpl(realTimeDatePanel, new DateLabelFormatter());
    }

}
