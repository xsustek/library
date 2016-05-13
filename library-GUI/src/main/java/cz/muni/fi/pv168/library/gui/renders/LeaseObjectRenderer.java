package cz.muni.fi.pv168.library.gui.renders;

import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Milan on 14.05.2016.
 */
public class LeaseObjectRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        LeasesTableModel model = (LeasesTableModel) table.getModel();

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        c.setBackground(model.getLeaseColor(row));

        return this;
    }
}
