package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by robert on 28.4.2016.
 */
public class CustomersTableModel extends AbstractTableModel {

    private ResourceBundle bundle;
    private List<Customer> customers;

    public CustomersTableModel() {
        bundle = ResourceBundle.getBundle("stringValues");
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public int getRowCount() {
        return customers != null ? customers.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (customers == null) return null;
        Customer customer = customers.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return customer.getId();
            case 1:
                return customer.getName();
            case 2:
                return customer.getAddress();
            case 3:
                return customer.getPhoneNumber();
            default:
                throw new IndexOutOfBoundsException("ColumnIndex greater than 3");
        }
    }


    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return bundle.getString("lbName");
            case 2:
                return bundle.getString("lbAddress");
            case 3:
                return bundle.getString("lbPhoneNumb");
            default:
                throw new IndexOutOfBoundsException("columnIndex greater than 3");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            default:
                throw new IndexOutOfBoundsException("columnIndex greater than 3");
        }
    }

    public void addedCustomer() {
        int index = customers.size() - 1;
        fireTableRowsInserted(index, index);
    }


}
