package cz.muni.fi.pv168.library.gui.renders;

import cz.muni.fi.pv168.library.gui.tableModels.BooksTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Milan on 13.05.2016.
 */

public class BooksCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        BooksTableModel model = (BooksTableModel) table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setForeground(Color.black);
        c.setBackground(model.getRowColour(row));
        if (isSelected) {
            if (model.getRowColour(row).equals(Color.RED)) {
                c.setBackground(new Color(228, 97, 108));
            }
            c.setBackground(new Color(184, 207, 229));
        }
        return this;
    }
}
