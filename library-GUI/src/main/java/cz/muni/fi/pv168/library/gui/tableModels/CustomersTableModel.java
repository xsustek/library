package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.CustomerManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by robert on 28.4.2016.
 */
public class CustomersTableModel extends AbstractTableModel {
    private CustomerManager manager;
    private List<Customer> customers;

    public CustomersTableModel(CustomerManager customerManager) {
        manager = customerManager;
        updateCustomers();
    }

    private void updateCustomers() {
        GetCustomerSwingWorker sw = new GetCustomerSwingWorker();
        sw.execute();
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
                return "Name";
            case 2:
                return "Address";
            case 3:
                return "Phone";
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
        updateCustomers();
        int index = customers.size() - 1;
        fireTableRowsInserted(index, index);
    }

    private class GetCustomerSwingWorker extends SwingWorker<List<Customer>, Void> {
        @Override
        protected List<Customer> doInBackground() throws Exception {
            return manager.findAllCustomers();
        }

        @Override
        protected void done() {
            try {
                customers = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
