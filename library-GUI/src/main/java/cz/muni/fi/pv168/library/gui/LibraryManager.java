package cz.muni.fi.pv168.library.gui;

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
    private JButton btFindLease;
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

    public static void main(String[] args) {
        initFrame();
    }

    private static void initFrame() {
        JFrame frame = new JFrame("LibraryManager");

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException ex) {

        } catch (InstantiationException ex) {

        } catch (ClassNotFoundException ex) {

        } catch (IllegalAccessException ex) {

        }

        frame.setContentPane(new LibraryManager().mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
