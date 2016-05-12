package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.*;
import cz.muni.fi.pv168.library.gui.renders.BookCellRender;
import cz.muni.fi.pv168.library.gui.renders.CustomerCellRender;
import cz.muni.fi.pv168.library.gui.tableModels.BooksTableModel;
import cz.muni.fi.pv168.library.gui.tableModels.CustomersTableModel;
import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

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
    private JButton btFindLease;
    private JTextField tfFindLease;
    private JTextField textField3;
    private static JFrame frame;

    private LeaseManager leaseManager;
    private BookManager bookManager;
    private CustomerManager customerManager;
    private LeasesTableModel leasesTableModel;
    private CustomersTableModel customersTableModel;
    private BooksTableModel booksTableModel;

    public LibraryManager() {
        btAddLease.addActionListener(e -> {
            LeaseAdd leaseAdd = new LeaseAdd(frame, bookManager.findAllBooks(), customerManager.findAllCustomers());
            leaseAdd.display();
            new AddLeaseSwingWorker(leaseAdd).execute();
        });


        btAddBook.addActionListener(e -> {
            BookAdd bookAdd = new BookAdd(frame);
            bookAdd.display();

            new AddBookSwingWorker(bookAdd).execute();


        });

        btAddCustomer.addActionListener(e -> {
            CustomerAdd customerAdd = new CustomerAdd(frame);
            customerAdd.display();
            new AddCustomerSwingWorker(customerAdd).execute();

        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
                initFrame());
    }

    private static void initFrame() {
        frame = new JFrame("LibraryManager");

        frame.setContentPane(new LibraryManager().mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        initManagers();
        leasesTableModel = new LeasesTableModel(leaseManager);
        leaseTable = new JTable(leasesTableModel);
        leaseTable.setDefaultRenderer(Book.class, new BookCellRender());
        leaseTable.setDefaultRenderer(Customer.class, new CustomerCellRender());
        // leaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel leaseColumnModel = leaseTable.getColumnModel();
        leaseColumnModel.getColumn(0).setMaxWidth(40);

        customersTableModel = new CustomersTableModel(customerManager);
        customerTable = new JTable(customersTableModel);
        customerTable.setDefaultRenderer(Customer.class, new CustomerCellRender());
        TableColumnModel customerTableModel = customerTable.getColumnModel();
        customerTableModel.getColumn(0).setMaxWidth(40);

        booksTableModel = new BooksTableModel(bookManager);
        bookTable = new JTable(booksTableModel);
        bookTable.setDefaultRenderer(Book.class, new BookCellRender());
        TableColumnModel bookTableModel = bookTable.getColumnModel();
        bookTableModel.getColumn(0).setMaxWidth(40);
    }

    private void initManagers() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                Lease.class.getResource("spring-config.xml").toString());

        bookManager = ctx.getBean(BookManager.class);
        customerManager = ctx.getBean(CustomerManager.class);
        leaseManager = ctx.getBean(LeaseManager.class);
    }

    private class AddLeaseSwingWorker extends SwingWorker<Void, Void> {
        private final LeaseAdd leaseAdd;

        public AddLeaseSwingWorker(LeaseAdd leaseAdd) {
            this.leaseAdd = leaseAdd;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Lease lease;
            if ((lease = leaseAdd.getData()) != null) {
                leaseManager.createLease(lease);
            }
            return null;
        }

        @Override
        protected void done() {
            leasesTableModel.addedLease();
        }
    }

    private class AddBookSwingWorker extends SwingWorker<Void, Void> {
        private final BookAdd bookAdd;

        public AddBookSwingWorker(BookAdd bookAdd) {
            this.bookAdd = bookAdd;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Book book;
            if ((book = bookAdd.getData()) != null) {
                bookManager.createBook(book);
            }
            return null;
        }

        @Override
        protected void done() {
            booksTableModel.addedBook();
        }
    }

    private class AddCustomerSwingWorker extends SwingWorker<Void, Void> {
        private final CustomerAdd customerAdd;

        public AddCustomerSwingWorker(CustomerAdd customerAdd) {
            this.customerAdd = customerAdd;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Customer customer;
            if ((customer = customerAdd.getData()) != null) {
                customerManager.createCustomer(customer);
            }
            return null;
        }

        @Override
        protected void done() {
            customersTableModel.addedCustomer();
        }
    }


}
