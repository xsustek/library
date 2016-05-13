package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.Validator;
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
    private JButton cancelButton;
    private JComboBox cbBook;
    private JComboBox cbCustomer;
    private JDatePickerImpl endTimeDatePicker;
    private JDatePickerImpl realTimeDatePicker;
    private JPanel main;

    private JFrame parent;
    private JDialog dialog;

    private Lease leaseToUpdate;

    public LeaseUpdate(JFrame parent, List<Book> books, List<Customer> customers, Lease leaseToUpdate) {
        this.parent = parent;
        this.leaseToUpdate = leaseToUpdate;

        books.forEach(book -> cbBook.addItem(book));
        customers.forEach(customer -> cbCustomer.addItem(customer));

        updateButton.addActionListener(e -> {
            this.leaseToUpdate.setBook((Book) cbBook.getSelectedItem());
            this.leaseToUpdate.setCustomer((Customer) cbCustomer.getSelectedItem());
            Date endTime = (Date) endTimeDatePicker.getModel().getValue();
            if (endTime != null) {
                this.leaseToUpdate.setEndTime(endTime.toLocalDate());
            }
            Date realTime = (Date) realTimeDatePicker.getModel().getValue();
            if (realTime != null) {
                this.leaseToUpdate.setRealEndTime(realTime.toLocalDate());
            }

            try {
                Validator.validateLease(leaseToUpdate);
                dialog.dispose();
            } catch (ValidationException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Warning", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
    }

    public Lease getData() {
        return leaseToUpdate;
    }

    public void display() {
        dialog = new JDialog(parent, true);

        dialog.setContentPane(main);

        if (leaseToUpdate.getEndTime() != null) {
            endTimeDatePicker.getModel().setDate(leaseToUpdate.getEndTime().getYear(),
                    leaseToUpdate.getEndTime().getMonthValue(),
                    leaseToUpdate.getEndTime().getDayOfMonth());
            endTimeDatePicker.getModel().setSelected(true);
        }
        if (leaseToUpdate.getRealEndTime() != null) {
            realTimeDatePicker.getModel().setDate(leaseToUpdate.getRealEndTime().getYear(),
                    leaseToUpdate.getRealEndTime().getMonthValue(),
                    leaseToUpdate.getRealEndTime().getDayOfMonth());
            realTimeDatePicker.getModel().setSelected(true);
        }
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
