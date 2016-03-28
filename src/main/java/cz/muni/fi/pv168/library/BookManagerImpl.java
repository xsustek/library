package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createBook(Book book) {
        checkDataSource();
        validate(book);

        if (book.getId() != null) {
            throw new IllegalEntityException("book id is already set");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO BOOKS (TITLE, PAGES, RELEASE_YEAR, AUTHOR) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.getTitle());
            statement.setInt(2, book.getPages());
            statement.setDate(3, new Date(book.getReleaseYear().getTime()));
            statement.setString(4, book.getAuthor());

            int addedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(addedRows, book, true);

            Long id = DBUtils.getId(statement.getGeneratedKeys());
            book.setId(id);
        } catch (SQLException e) {
            String msg = "Error when inserting book into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }
    }

    private void validate(Book book) throws IllegalArgumentException {
        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("author is null");
        }
        if (!book.getAuthor().matches("^[A-Z][a-z]*( ?[A-Z][a-z]*)* [A-Z][a-z]*$")) {
            throw new IllegalArgumentException("bad author");
        }
        if (book.getPages() < 1) {
            throw new IllegalArgumentException("pages is less then 1");
        }
        if (book.getTitle() == null) {
            throw new IllegalArgumentException("title is null");
        }
        if (book.getReleaseYear() == null) {
            throw new IllegalArgumentException("date is null");
        }

    }

    private Book resultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setPages(rs.getInt("pages"));
        book.setReleaseYear(rs.getDate("release_year"));
        book.setAuthor(rs.getString("author"));
        return book;
    }

    public Book getBookById(Long id) {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS WHERE ID = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Book book = resultSetToBook(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + findAllBooks() + " and " + resultSetToBook(rs));
                }

                return book;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            String msg = "Error when getting book with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Book> findAllBooks() {

        checkDataSource();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS")) {

            return getBooks(st);

        } catch (SQLException ex) {
            String msg = "Error when getting all books from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private List<Book> getBooks(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Book> result = new ArrayList<>();

        while (rs.next()) {
            result.add(resultSetToBook(rs));
        }

        return result;
    }

    public void updateBook(Book book) {
        checkDataSource();
        validate(book);

        if (book.getId() == null) {
            throw new IllegalEntityException("book id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE BOOKS SET TITLE = ?, PAGES = ?, RELEASE_YEAR = ?, AUTHOR = ? WHERE ID = ?")) {

            st.setString(1, book.getTitle());
            st.setInt(2, book.getPages());
            st.setDate(3, new Date(book.getReleaseYear().getTime()));
            st.setString(4, book.getAuthor());
            st.setLong(5, book.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
        } catch (SQLException ex) {
            String msg = "Error when updating book in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void deleteBook(Book book) {
        checkDataSource();

        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getId() == null) {
            throw new IllegalEntityException("book id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM BOOKS WHERE ID = ?")) {

            st.setLong(1, book.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
        } catch (SQLException ex) {
            String msg = "Error when deleting book from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Book> findBookByAuthor(String author) {
        checkDataSource();

        if (author == null) {
            throw new IllegalArgumentException("author is null");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS WHERE AUTHOR = ?")) {

            return getResult(author, st);

        } catch (SQLException ex) {
            String msg = "Error when getting book with author = " + author + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Book> findBookByTitle(String title) {

        checkDataSource();

        if (title == null) {
            throw new IllegalArgumentException("title is null");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS WHERE TITLE = ?")) {

            return getResult(title, st);

        } catch (SQLException ex) {
            String msg = "Error when getting book with title = " + title + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private List<Book> getResult(String data, PreparedStatement st) throws SQLException {
        st.setString(1, data);
        return getBooks(st);
    }
}
