package cz.muni.fi.pv168.library.gui.renders.LeaseRenderers;

import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by robert on 2.5.2016.
 */
public class CustomerCellRender extends LeaseRender {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        LeasesTableModel model = (LeasesTableModel) table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Color.black);
        c.setBackground(model.getLeaseColor(row));

        setColor(isSelected, row, model, c);

        Customer customer = (Customer) value;
        setText(customer.getName());
        return this;
    }
}
