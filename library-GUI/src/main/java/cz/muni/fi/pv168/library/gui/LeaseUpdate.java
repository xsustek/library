package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.sql.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Milan on 25.04.2016.
 */
public class LeaseUpdate {
    private JButton updateButton;
    private JButton zrušitButton;
    private JComboBox cbBook;
    private JComboBox cbCustomer;
    private JDatePickerImpl endTimeDatePicker;
    private JDatePickerImpl realTimeDatePicker;
    private JPanel main;

    private JFrame parent;
    private JDialog dialog;

    private Lease toUpdate;
    private List<Book> books;
    private List<Customer> customers;

    public LeaseUpdate(JFrame parent, Lease toUpdate, List<Book> books, List<Customer> customers) {
        this.parent = parent;
        this.toUpdate = toUpdate;
        this.books = books;
        this.customers = customers;

        books.forEach(book -> cbBook.addItem(book));
        customers.forEach(customer -> cbCustomer.addItem(customer));

        updateButton.addActionListener(e -> {
            toUpdate.setBook((Book) cbBook.getSelectedItem());
            toUpdate.setCustomer((Customer) cbCustomer.getSelectedItem());
            Date endTime = (Date) endTimeDatePicker.getModel().getValue();
            if (endTime != null) {
                toUpdate.setEndTime(endTime.toLocalDate());
            }
            Date realTime = (Date) realTimeDatePicker.getModel().getValue();
            if (realTime != null) {
                toUpdate.setRealEndTime(realTime.toLocalDate());
            }

            dialog.dispose();
        });
    }

    public void display() {
        dialog = new JDialog(parent, true);

        dialog.setContentPane(main);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        SqlDateModel endTimeDateModel = new SqlDateModel();
        JDatePanelImpl endTimeDatePanel = new JDatePanelImpl(endTimeDateModel, p);
        endTimeDatePicker = new JDatePickerImpl(endTimeDatePanel, new DateLabelFormatter());

        SqlDateModel realTimeDateModel = new SqlDateModel();
        JDatePanelImpl realTimeDatePanel = new JDatePanelImpl(realTimeDateModel, p);
        realTimeDatePicker = new JDatePickerImpl(realTimeDatePanel, new DateLabelFormatter());
    }
}
