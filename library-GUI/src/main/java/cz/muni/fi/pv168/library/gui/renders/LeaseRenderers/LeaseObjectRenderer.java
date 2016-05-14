package cz.muni.fi.pv168.library.gui.renders.LeaseRenderers;

import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Milan on 14.05.2016.
 */
public class LeaseObjectRenderer extends LeaseRender {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        LeasesTableModel model = (LeasesTableModel) table.getModel();

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        c.setForeground(Color.black);
        c.setBackground(model.getLeaseColor(row));

        setColor(isSelected, row, model, c);

        return this;
    }
}
