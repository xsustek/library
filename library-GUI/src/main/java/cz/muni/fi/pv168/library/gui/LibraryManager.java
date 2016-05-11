package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.*;
import cz.muni.fi.pv168.library.gui.renders.BookCellRender;
import cz.muni.fi.pv168.library.gui.renders.CustomerCellRender;
import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by robert on 21.4.2016.
 */
public class LibraryManager {
    private static LeaseManager leaseManager;
    private static BookManager bookManager;
    private static CustomerManager customerManager;

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

    public LibraryManager() {
        btAddLease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // swingworker
                JDialog jDialog = new JDialog(frame);
                jDialog.setContentPane(new LeaseAdd().getLeaseAddPanel());
                jDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jDialog.pack();
                jDialog.setVisible(true);
                frame.setEnabled(false);
            }
        });
    }

    public static void main(String[] args) {
        initManagers();
        SwingUtilities.invokeLater(() -> {
            initFrame();
        });

    }

    private static void initFrame() {
        frame = new JFrame("LibraryManager");

        frame.setContentPane(new LibraryManager().mainPane);
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        leaseTable = new JTable(new LeasesTableModel(leaseManager));
        leaseTable.setDefaultRenderer(Book.class, new BookCellRender());
        leaseTable.setDefaultRenderer(Customer.class, new CustomerCellRender());
        leaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumnModel columnModel = leaseTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(40);
    }

    private static void initManagers() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                Lease.class.getResource("spring-test-config.xml").toString());

        bookManager = ctx.getBean(BookManager.class);
        customerManager = ctx.getBean(CustomerManager.class);
        leaseManager = ctx.getBean(LeaseManager.class);
    }

}
