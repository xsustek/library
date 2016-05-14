package cz.muni.fi.pv168.library.gui.renders.LeaseRenderers;

import cz.muni.fi.pv168.library.gui.tableModels.LeasesTableModel;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Milan on 14.05.2016.
 */
public abstract class LeaseRender extends DefaultTableCellRenderer {


    public void setColor(boolean isSelected, int row, LeasesTableModel model, Component c) {
        if (isSelected) {
            if (model.getLeaseColor(row).equals(Color.RED)) {
                c.setBackground(new Color(228, 97, 108));
            } else {
                c.setBackground(new Color(184, 207, 229));
            }
        }
    }

}
