package cz.muni.fi.pv168.library.gui;

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
        dialog.pack();
        dialog.setVisible(true);
    }
}
