package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.*;

/**
 * Created by robert on 21.4.2016.
 */
public class LibraryManager {
    private JPanel mainPane;
    private JTabbedPane tabbedPane1;
    private JButton btAddLease;
    private JButton btUpdateLease;
    private JButton btDeleteLease;
    private JButton btAddBook;
    private JButton btDeleteBook;
    private JButton btFindBook;
    private JButton btUpdateBook;
    private JButton btAddCustomer;
    private JButton btDeleteCustomer;
    private JButton btFindCustomer;
    private JButton btUpdateCustomer;
    private JTable leaseTable;
    private JScrollPane jsLease;
    private JTable bookTable;
    private JTable customerTable;
    private JPanel leasesTab;
    private JPanel customersTab;
    private JPanel booksTab;
    private JScrollPane jsCustomer;
    private JScrollPane jsBook;
    private JTextField textField1;
    private JButton findButton;
    private JTextField textField2;
    private JTextField textField3;

    public static void main(String[] args) {
        initFrame();
    }

    private static void initFrame() {
        JFrame frame = new JFrame("LibraryManager");



        frame.setContentPane(new LibraryManager().mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        leaseTable = new JTable(new LeasesTableModel());
    }
}
