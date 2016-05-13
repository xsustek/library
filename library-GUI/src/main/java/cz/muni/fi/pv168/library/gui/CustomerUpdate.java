package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Customer;

import javax.swing.*;

/**
 * Created by Milan on 25.04.2016.
 */
public class CustomerUpdate {
    private JButton updateButton;
    private JButton cancelButton;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPanel main;

    private JFrame parent;
    private Customer customer;
    private JDialog dialog;

    public CustomerUpdate(JFrame parent, Customer customerToUpdate) {
        this.parent = parent;
        this.customer = customerToUpdate;

        updateButton.addActionListener(e -> {
            customer.setName(tfName.getText());
            customer.setAddress(tfAddress.getText());
            customer.setPhoneNumber(tfPhone.getText());

            dialog.dispose();
        });
    }

    public Customer getData() {
        return customer;
    }

    public void display() {
        dialog = new JDialog(parent, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(main);

        setTextFields();

        dialog.pack();
        dialog.setVisible(true);
    }

    private void setTextFields() {
        tfName.setText(customer.getName());
        tfAddress.setText(customer.getAddress());
        tfPhone.setText(customer.getPhoneNumber());
    }
}
