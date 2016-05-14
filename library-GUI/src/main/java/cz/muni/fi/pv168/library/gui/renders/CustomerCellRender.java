package cz.muni.fi.pv168.library.gui.renders;

import javax.swing.*;
import java.awt.*;

/**
 * Created by robert on 2.5.2016.
 */
public class CustomerCellRender extends CellRender {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setColor(isSelected, row, Color.WHITE, c);

        return this;
    }
}
