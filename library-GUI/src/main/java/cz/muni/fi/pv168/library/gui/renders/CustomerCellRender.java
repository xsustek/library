package cz.muni.fi.pv168.library.gui.renders;

import cz.muni.fi.pv168.library.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by robert on 2.5.2016.
 */
public class CustomerCellRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Customer customer = (Customer) value;
        setText(customer.getName());
        return this;
    }
}
