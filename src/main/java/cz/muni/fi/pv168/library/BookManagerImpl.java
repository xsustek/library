package cz.muni.fi.pv168.library;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Milan on 26.02.2016.
 */
public class BookManagerImpl implements BookManager {

    private static final Logger logger = Logger.getLogger(
            BookManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createBook(Book book) {

    }

    public Book getBookById(Long id) {
        return null;
    }

    public List<Book> findAllBooks() {
        return null;
    }

    public void updateBook(Book book) {

    }

    public void deleteBook(Book book) {

    }

    public List<Book> findBookByAuthor() {
        return null;
    }

    public List<Book> findBookByTitle() {
        return null;
    }

}
