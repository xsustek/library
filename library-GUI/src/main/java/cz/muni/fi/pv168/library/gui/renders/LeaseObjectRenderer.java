package cz.muni.fi.pv168.library.gui.renders;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Milan on 14.05.2016.
 */
public class LeaseObjectRenderer extends CellRender {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        LeasesTableModel model = (LeasesTableModel) table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setColor(isSelected, row, model.getRowColor(row), c);

        if (value instanceof Book) {
            Book book = (Book) value;
            setText(book.getTitle() + ", " + book.getAuthor());
        }

        if (value instanceof Customer) {
            Customer customer = (Customer) value;
            setText(customer.getName());
        }

        return this;
    }
}
