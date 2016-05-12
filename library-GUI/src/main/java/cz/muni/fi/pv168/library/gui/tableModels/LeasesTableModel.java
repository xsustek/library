package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;
import cz.muni.fi.pv168.library.LeaseManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by robert on 28.4.2016.
 */
public class LeasesTableModel extends AbstractTableModel {

    private LeaseManager manager;
    private List<Lease> leases;

    public LeasesTableModel(LeaseManager manager) {
        this.manager = manager;
        updateLeases();
    }

    @Override
    public int getRowCount() {
        return leases != null ? leases.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (leases == null) return null;
        Lease lease = leases.get(rowIndex);

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


    public void addedLease() {
        updateLeases();
        int lastRow = leases.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void deleteLease(int row) {

    }

    public void updateLease(int row) {

    }

    private void updateLeases() {
        GetLeasesSwingWorker sw = new GetLeasesSwingWorker();
        sw.execute();
    }

    private class GetLeasesSwingWorker extends SwingWorker<List<Lease>, Void> {
        @Override
        protected List<Lease> doInBackground() throws Exception {
            return manager.findAllLeases();
        }

        @Override
        protected void done() {
            try {
                leases = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
