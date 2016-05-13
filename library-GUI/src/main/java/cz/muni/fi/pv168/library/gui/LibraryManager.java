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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

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
    private JTextField textField3;
    private JButton btReturn;
    private static JFrame frame;

    private LeaseManager leaseManager;
    private BookManager bookManager;
    private CustomerManager customerManager;
    private LeasesTableModel leasesTableModel;
    private CustomersTableModel customersTableModel;
    private BooksTableModel booksTableModel;

    private java.util.List<Lease> leases;
    private java.util.List<Book> books;
    private java.util.List<Customer> customers;

    public LibraryManager() {
        updateLists();

        btAddLease.addActionListener(e -> {
            LeaseAdd leaseAdd = new LeaseAdd(frame, books, customers);
            leaseAdd.display();
            new AddLeaseSwingWorker(leaseAdd.getData()).execute();
        });

        btUpdateLease.addActionListener(e -> {
            int selectedRowIndex = leaseTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            LeaseUpdate leaseUpdate = new LeaseUpdate(frame, books, customers, leases.get(selectedRowIndex));
            leaseUpdate.display();

            new UpdateLeaseSwingWorker(leaseUpdate.getData(), selectedRowIndex).execute();
        });

        btReturn.addActionListener(e -> {
            int selectedRowIndex = leaseTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            Lease lease = leases.get(selectedRowIndex);
            lease.setRealEndTime(LocalDate.now());

            new UpdateLeaseSwingWorker(lease, selectedRowIndex).execute();
        });

        btDeleteLease.addActionListener(e -> {
            int selectedRowIndex = leaseTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            new DeleteLeaseSwingWorker(leases.get(selectedRowIndex), selectedRowIndex).execute();

        });


        btAddBook.addActionListener(e -> {
            BookAdd bookAdd = new BookAdd(frame);
            bookAdd.display();

            new AddBookSwingWorker(bookAdd.getData()).execute();
        });

        btUpdateBook.addActionListener(e -> {
            int selectedRowIndex = bookTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            BookUpdate bookUpdate = new BookUpdate(frame, books.get(selectedRowIndex));
            bookUpdate.display();

            new UpdateBookSwingWorker(bookUpdate.getData(), selectedRowIndex).execute();
        });

        btDeleteBook.addActionListener(e -> {
            int selectedRowIndex = bookTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            new DeleteBookSwingWorker(books.get(selectedRowIndex), selectedRowIndex).execute();
        });

        btAddCustomer.addActionListener(e -> {
            CustomerAdd customerAdd = new CustomerAdd(frame);
            customerAdd.display();
            new AddCustomerSwingWorker(customerAdd.getData()).execute();

        });

        btUpdateCustomer.addActionListener(e -> {
            int selectedRowIndex = customerTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            CustomerUpdate customerUpdate = new CustomerUpdate(frame, customers.get(selectedRowIndex));
            customerUpdate.display();

            new UpdateCustomerSwingWorker(customerUpdate.getData(), selectedRowIndex).execute();
        });

        btDeleteCustomer.addActionListener(e -> {
            int selectedRowIndex = customerTable.getSelectedRow();
            if (selectedRowIndex < 0) return;
            new DeleteCustomerSwingWorker(customers.get(selectedRowIndex), selectedRowIndex).execute();
        });
    }

    private void updateLists() {
        updateLeases();
        updateBooks();
        updateCustomers();
    }

    private void updateCustomers() {
        GetCustomerSwingWorker cSw = new GetCustomerSwingWorker();
        cSw.execute();
    }

    private void updateBooks() {
        GetBooksSwingWorker bSw = new GetBooksSwingWorker();
        bSw.execute();
    }

    private void updateLeases() {
        GetLeasesSwingWorker lSw = new GetLeasesSwingWorker();
        lSw.execute();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
                initFrame());
    }

    private static void initFrame() {
        frame = new JFrame("LibraryManager");

        frame.setContentPane(new LibraryManager().mainPane);
        frame.setJMenuBar(createMenu());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        initManagers();
        // Lease Table
        leasesTableModel = new LeasesTableModel();
        leaseTable = new JTable(leasesTableModel);
        leaseTable.setDefaultRenderer(Book.class, new BookCellRender());
        leaseTable.setDefaultRenderer(Customer.class, new CustomerCellRender());
        leaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel leaseColumnModel = leaseTable.getColumnModel();
        leaseColumnModel.getColumn(0).setMaxWidth(40);

        // Customer Table
        customersTableModel = new CustomersTableModel();
        customerTable = new JTable(customersTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel customerColumnModel = customerTable.getColumnModel();
        customerColumnModel.getColumn(0).setMaxWidth(40);

        // Book Table
        booksTableModel = new BooksTableModel();
        bookTable = new JTable(booksTableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel bookColumnModel = bookTable.getColumnModel();
        bookColumnModel.getColumn(0).setMaxWidth(40);
    }

    private void initManagers() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                Lease.class.getResource("spring-config.xml").toString());

        bookManager = ctx.getBean(BookManager.class);
        customerManager = ctx.getBean(CustomerManager.class);
        leaseManager = ctx.getBean(LeaseManager.class);
    }

    private static JMenuBar createMenu() {
        JMenuBar menubar = new JMenuBar();
        menubar.add(createLaFMenu());

        return menubar;
    }

    private static JMenu createLaFMenu() {
        JMenu laf = new JMenu("Look and feel");
        for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            JMenuItem item = new JMenuItem(info.getName());
            laf.add(item);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        SwingUtilities.updateComponentTreeUI(LibraryManager.frame);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return laf;
    }

    private class AddLeaseSwingWorker extends SwingWorker<Void, Void> {
        private final Lease lease;

        public AddLeaseSwingWorker(Lease lease) {
            this.lease = lease;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (lease != null) {
                leaseManager.createLease(lease);
            }
            return null;
        }

        @Override
        protected void done() {
            updateLeases();
            leasesTableModel.setLeases(leases);
            leasesTableModel.fireTableRowsInserted(leases.size() - 1, leases.size() - 1);
        }
    }

    private class UpdateLeaseSwingWorker extends SwingWorker<Void, Void> {

        private Lease leaseToUpdate;
        private int index;

        public UpdateLeaseSwingWorker(Lease leaseToUpdate, int index) {
            this.leaseToUpdate = leaseToUpdate;
            this.index = index;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (leaseToUpdate != null) {
                leaseManager.updateLease(leaseToUpdate);
            }
            return null;
        }

        @Override
        protected void done() {
            updateLeases();
            leasesTableModel.setLeases(leases);
            leasesTableModel.fireTableRowsUpdated(index, index);
        }
    }

    private class DeleteLeaseSwingWorker extends SwingWorker<Void, Void> {

        private Lease lease;
        private int index;

        public DeleteLeaseSwingWorker(Lease lease, int index) {
            this.lease = lease;
            this.index = index;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (lease != null) {
                try {
                    leaseManager.deleteLease(lease);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            updateLeases();
            leasesTableModel.setLeases(leases);
            leasesTableModel.fireTableRowsDeleted(index, index);
        }
    }

    private class AddBookSwingWorker extends SwingWorker<Void, Void> {
        private final Book book;

        public AddBookSwingWorker(Book book) {
            this.book = book;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (book != null) {
                bookManager.createBook(book);
            }
            return null;
        }

        @Override
        protected void done() {
            updateBooks();
            booksTableModel.setBooks(books);
            booksTableModel.fireTableRowsInserted(books.size() - 1, books.size() - 1);
        }
    }

    private class UpdateBookSwingWorker extends SwingWorker<Void, Void> {
        private Book book;
        private int index;

        public UpdateBookSwingWorker(Book book, int index) {
            this.book = book;
            this.index = index;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (book != null) {
                bookManager.updateBook(book);
            }
            return null;
        }

        @Override
        protected void done() {
            updateBooks();
            booksTableModel.setBooks(books);
            booksTableModel.fireTableRowsUpdated(index, index);
        }
    }

    private class DeleteBookSwingWorker extends SwingWorker<Void, Void> {
        private Book book;
        private int row;

        public DeleteBookSwingWorker(Book book, int row) {
            this.book = book;
            this.row = row;
        }

        @Override
        protected void done() {
            updateBooks();
            booksTableModel.setBooks(books);
            booksTableModel.fireTableRowsDeleted(row, row);
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (book != null) {
                try {
                    bookManager.deleteBook(book);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return null;
        }
    }

    private class AddCustomerSwingWorker extends SwingWorker<Void, Void> {
        private final Customer customer;

        public AddCustomerSwingWorker(Customer customer) {
            this.customer = customer;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (customer != null) {
                customerManager.createCustomer(customer);
            }
            return null;
        }

        @Override
        protected void done() {
            updateCustomers();
            customersTableModel.setCustomers(customers);
            customersTableModel.fireTableRowsInserted(customers.size() - 1, customers.size() - 1);
        }
    }

    private class UpdateCustomerSwingWorker extends SwingWorker<Void, Void> {
        private final Customer customer;
        private final int index;

        public UpdateCustomerSwingWorker(Customer customer, int index) {
            this.customer = customer;
            this.index = index;
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (customer != null) {
                customerManager.updateCustomer(customer);
            }
            return null;
        }

        @Override
        protected void done() {
            updateCustomers();
            customersTableModel.setCustomers(customers);
            customersTableModel.fireTableRowsUpdated(index, index);
        }
    }

    private class DeleteCustomerSwingWorker extends SwingWorker<Void, Void> {
        private Customer customer;
        private int row;

        public DeleteCustomerSwingWorker(Customer customer, int row) {
            this.customer = customer;
            this.row = row;
        }

        @Override
        protected void done() {
            updateCustomers();
            customersTableModel.setCustomers(customers);
            customersTableModel.fireTableRowsDeleted(row, row);
        }

        @Override
        protected Void doInBackground() throws Exception {
            if (customer != null) {
                try {
                    customerManager.deleteCustomer(customer);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return null;
        }
    }

    private class GetBooksSwingWorker extends SwingWorker<java.util.List<Book>, Void> {

        @Override
        protected java.util.List<Book> doInBackground() throws Exception {
            return bookManager.findAllBooks();
        }

        @Override
        protected void done() {
            try {
                books = get();
                booksTableModel.setBooks(books);
                booksTableModel.fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetCustomerSwingWorker extends SwingWorker<java.util.List<Customer>, Void> {
        @Override
        protected java.util.List<Customer> doInBackground() throws Exception {
            return customerManager.findAllCustomers();
        }

        @Override
        protected void done() {
            try {
                customers = get();
                customersTableModel.setCustomers(customers);
                customersTableModel.fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetLeasesSwingWorker extends SwingWorker<java.util.List<Lease>, Void> {
        @Override
        protected java.util.List<Lease> doInBackground() throws Exception {
            return leaseManager.findAllLeases();
        }

        @Override
        protected void done() {
            try {
                leases = get();
                leasesTableModel.setLeases(leases);
                leasesTableModel.fireTableStructureChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
