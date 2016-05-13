package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.Validator;
import cz.muni.fi.pv168.library.Customer;

import javax.swing.*;

/**
 * Created by robert on 25.4.2016.
 */
public class CustomerAdd {
    private JButton addButton;
    private JButton cancelButton;
    private JTextField tfName;
    private JTextField tfAddress;
    private JTextField tfPhoneNumb;
    private JPanel main;
    private JFrame parent;
    private JDialog dialog;
    private Customer customer;


    public CustomerAdd(JFrame parent) {
        this.parent = parent;
        addButton.addActionListener(e -> {
            customer = new Customer();
            customer.setName(tfName.getText());
            customer.setAddress(tfAddress.getText());
            customer.setPhoneNumber(tfPhoneNumb.getText());

            try {
                Validator.validateCustomer(customer);
                dialog.dispose();
            } catch (ValidationException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Warning", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
    }

    public Customer getData() {
        return customer;
    }

    public void display() {
        dialog = new JDialog(parent, "Add Customer", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(main);
        dialog.pack();
        dialog.setVisible(true);
    }
}
