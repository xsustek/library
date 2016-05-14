package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Book;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by robert on 28.4.2016.
 */
public class BooksTableModel extends AbstractTableModel {
    private ResourceBundle bundle;
    private List<Book> books;
    private List<Book> availableBooks;

    private List<Color> rowColours = new ArrayList<>();



    public BooksTableModel() {
        bundle = ResourceBundle.getBundle("cz/muni/fi/pv168/library/gui/stringValues");
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        books.forEach(book -> rowColours.add(Color.BLACK));
    }

    public void setAvailableBooks(List<Book> books) {
        availableBooks = books;
    }
    @Override
    public int getRowCount() {

        return books != null ? books.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (books == null) return null;
        Book book = books.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return book.getId();
            case 1:
                return book.getTitle();
            case 2:
                return book.getAuthor();
            case 3:
                return book.getPages();
            case 4:
                return book.getReleaseYear();
            default:
                throw new IndexOutOfBoundsException("columnIndex greater than 4");
        }
    }


    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return bundle.getString("lbTitle");
            case 2:
                return bundle.getString("lbAuthor");
            case 3:
                return bundle.getString("lbPages");
            case 4:
                return bundle.getString("lbReleaseYear");
            default:
                throw new IndexOutOfBoundsException("columnIndex greater than 4");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return Integer.class;
            default:
                throw new IndexOutOfBoundsException("columnIndex greater than 4");
        }
    }

    public Color getRowColor(int row) {
        Book book = books.get(row);

        return availableBooks.contains(book) ? Color.WHITE : Color.RED;
    }

}
