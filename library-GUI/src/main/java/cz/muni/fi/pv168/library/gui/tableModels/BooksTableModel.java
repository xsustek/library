package cz.muni.fi.pv168.library.gui.tableModels;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.BookManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by robert on 28.4.2016.
 */
public class BooksTableModel extends AbstractTableModel {
    private BookManager manager;
    private List<Book> books;

    public BooksTableModel(BookManager bookManager) {
        manager = bookManager;
        updateBooks();
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
                return "Title";
            case 2:
                return "Author";
            case 3:
                return "Pages";
            case 4:
                return "ReleaseYear";
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

    public void addedBook() {
        updateBooks();
        int index = books.size() - 1;
        fireTableRowsInserted(index, index);
    }

    private void updateBooks() {
        GetBooksSwingWorker sw = new GetBooksSwingWorker();
        sw.execute();
    }

    private class GetBooksSwingWorker extends SwingWorker<List<Book>, Void> {

        @Override
        protected List<Book> doInBackground() throws Exception {
            return manager.findAllBooks();
        }

        @Override
        protected void done() {
            try {
                books = get();
                fireTableStructureChanged();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
