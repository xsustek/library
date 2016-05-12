package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;
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
    private Lease lease;
    private JDialog frame;
    private JFrame parent;
    private List<Book> books;
    private List<Customer> customers;

    public LeaseAdd(JFrame parent, List<Book> books, List<Customer> customers) {
        this.parent = parent;
        this.books = books;
        this.customers = customers;

        books.forEach(book -> cbBook.addItem(book));
        customers.forEach(customer -> cbCustomer.addItem(customer));

        addButton.addActionListener(e -> {
            lease = new Lease();
            lease.setBook((Book) cbBook.getSelectedItem());
            lease.setCustomer((Customer) cbCustomer.getSelectedItem());

            Date endTime = (Date) endTimeDatePicker.getModel().getValue();
            if (endTime != null) {
                lease.setEndTime(endTime.toLocalDate());
            }
            Date realEndTime = (Date) realTimeDatePicker.getModel().getValue();
            if (realEndTime != null) {
                lease.setRealEndTime(realEndTime.toLocalDate());
            }
            frame.dispose();
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


    }

    public Lease getData() {
        return lease;
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

    public void display() {
        frame = new JDialog(parent, true);

        frame.setContentPane(leaseAddPanel);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
