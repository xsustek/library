package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;
import cz.muni.fi.pv168.library.LeaseManager;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by robert on 28.4.2016.
 */
public class LeasesTableModel extends AbstractTableModel {

    private LeaseManager manager;
    private ResourceBundle bundle;
    private List<Lease> leases;
    private List<Lease> expiredLeases;

    public LeasesTableModel() {
        bundle = ResourceBundle.getBundle("cz/muni/fi/pv168/library/gui/stringValues");
    }

    public void setLeases(List<Lease> leases) {
        this.leases = leases;
    }

    public void setExpiredLeases(List<Lease> leases) {
        expiredLeases = leases;
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
                return bundle.getString("lbBook");
            case 2:
                return bundle.getString("lbCustomer");
            case 3:
                return bundle.getString("lbEndTime");
            case 4:
                return bundle.getString("lbRealEndTime");
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

    public Color getLeaseColor(int row) {
        Lease lease = leases.get(row);
        if (expiredLeases == null) return Color.WHITE;
        return expiredLeases.contains(lease) ? Color.RED : Color.WHITE;
    }
}
