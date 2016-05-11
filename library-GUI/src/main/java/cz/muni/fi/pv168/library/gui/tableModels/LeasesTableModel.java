package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;
import cz.muni.fi.pv168.library.LeaseManager;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;

/**
 * Created by robert on 28.4.2016.
 */
public class LeasesTableModel extends AbstractTableModel {

    private LeaseManager leaseManager;

    public LeasesTableModel(LeaseManager leaseManager) {
        this.leaseManager = leaseManager;
    }

    @Override
    public int getRowCount() {
        return leaseManager.findAllLeases().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Lease lease = leaseManager.findAllLeases().get(rowIndex);

        switch (columnIndex) {
            case 0:
                return lease.getId();
            case 1:
                return lease.getBook();
            case 2:
                return lease.getCustomer();
            case 3:
                return lease.getEndTime();
            case 4:
                return lease.getRealEndTime();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return "Book";
            case 2:
                return "Customer";
            case 3:
                return "End Time";
            case 4:
                return "Real End Time";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return Book.class;
            case 2:
                return Customer.class;
            case 3:
                return LocalDate.class;
            case 4:
                return LocalDate.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }


    public void addLease(Lease lease) {
        leaseManager.createLease(lease);
        int lastRow = leaseManager.findAllLeases().size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void deleteLease(int row) {

    }

    public void updateLease(int row) {

    }
}
