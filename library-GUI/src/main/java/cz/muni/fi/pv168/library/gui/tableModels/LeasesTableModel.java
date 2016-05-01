package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Lease;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 28.4.2016.
 */
public class LeasesTableModel extends AbstractTableModel {

    private List<Lease> leases = new ArrayList<>();

    @Override
    public int getRowCount() {
        return leases.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
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
}
