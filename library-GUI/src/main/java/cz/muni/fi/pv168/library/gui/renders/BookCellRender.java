package cz.muni.fi.pv168.library.gui.renders;

import cz.muni.fi.pv168.library.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by robert on 2.5.2016.
 */
public class BookCellRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Book book = (Book) value;
        setText(book.getTitle() + ", " + book.getAuthor());
        return this;
    }
}
