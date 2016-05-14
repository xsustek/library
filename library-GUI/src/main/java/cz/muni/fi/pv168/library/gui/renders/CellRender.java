package cz.muni.fi.pv168.library.gui.renders;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by Milan on 14.05.2016.
 */
public abstract class CellRender extends DefaultTableCellRenderer {


    public void setColor(boolean isSelected, int row, Color rowColor, Component c) {
        if (isSelected) {
            c.setForeground(Color.white);
            if (rowColor.equals(Color.RED)) {
                c.setBackground(new Color(228, 137, 166));
            } else {
                c.setBackground(new Color(115, 152, 229));
            }
        } else {
            c.setForeground(Color.black);
            c.setBackground(rowColor);
        }
    }

}
