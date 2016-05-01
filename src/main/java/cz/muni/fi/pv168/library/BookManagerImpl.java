package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Milan on 26.02.2016.
 */
public class BookManagerImpl implements BookManager {

    private static final Logger logger = Logger.getLogger(
            BookManagerImpl.class.getName());

    private JdbcTemplate jdbcTemplate;

    public void setSources(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private void checkSources() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createBook(Book book) {
        checkSources();
        validate(book);

        if (book.getId() != null) {
            throw new IllegalEntityException("Book id is already set");
        }

        String sql = "INSERT INTO BOOKS (TITLE, PAGES, RELEASE_YEAR, AUTHOR) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, book.getTitle());
                statement.setInt(2, book.getPages());
                statement.setInt(3, book.getReleaseYear());
                statement.setString(4, book.getAuthor());
                return statement;
            }, keyHolder);

            DBUtils.checkUpdatesCount(count, book, true);
            book.setId(keyHolder.getKey().longValue());
        } catch (DataAccessException e) {
            String msg = "Error when inserting book into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    private void validate(Book book) throws IllegalArgumentException {
        if (book == null) {
            throw new IllegalArgumentException("Book is null");
        }
        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            throw new IllegalArgumentException("Author is null or empty");
        }
        if (!book.getAuthor().matches("^[A-Z][a-z]*( ?[A-Z][a-z]*)* [A-Z][a-z]*$")) {
            throw new ValidationException("Invalid author format");
        }
        if (book.getPages() < 1) {
            throw new ValidationException("Pages is less then 1");
        }
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is null or empty");
        }
        if (book.getReleaseYear() < 0) {
            throw new ValidationException("Date is less than zero");
        }

    }

    private RowMapper<Book> bookMapper = (resultSet, i) -> {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setPages(resultSet.getInt("pages"));
        book.setReleaseYear(resultSet.getInt("release_year"));
        book.setAuthor(resultSet.getString("author"));
        return book;
    };


    public Book getBookById(Long id) {
        checkSources();

        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        try {
            return jdbcTemplate.queryForObject("SELECT * FROM BOOKS WHERE ID = ?", bookMapper, id);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ServiceFailureException(
                    "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + findAllBooks());
        } catch (DataAccessException ex) {
            String msg = "Error when getting book with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Book> findAllBooks() {
        checkSources();

        try {
            return jdbcTemplate.query("SELECT * FROM BOOKS", bookMapper);
        } catch (DataAccessException ex) {
            String msg = "Error when getting all books from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateBook(Book book) {
        checkSources();
        validate(book);

        if (book.getId() == null) {
            throw new IllegalEntityException("Book id is null");
        }

        String sql = "UPDATE BOOKS SET TITLE = ?, PAGES = ?, RELEASE_YEAR = ?, AUTHOR = ? WHERE ID = ?";
        try {
            int count = jdbcTemplate.update(sql,
                    book.getTitle(),
                    book.getPages(),
                    book.getReleaseYear(),
                    book.getAuthor(),
                    book.getId());

            DBUtils.checkUpdatesCount(count, book, false);
        } catch (DataAccessException ex) {
            String msg = "Error when updating book in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public void deleteBook(Book book) {
        checkSources();
        validate(book);

        if (book.getId() == null) {
            throw new IllegalEntityException("Book id is null");
        }

        try {
            int count = jdbcTemplate.update("DELETE FROM BOOKS WHERE ID = ?", book.getId());

            DBUtils.checkUpdatesCount(count, book, false);
        } catch (DataAccessException ex) {
            String msg = "Error when deleting book from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Book> findBookByAuthor(String author) {
        checkSources();

        if (author == null) {
            throw new IllegalArgumentException("Author is null");
        }

        try {
            return getBooks(author, "AUTHOR");
        } catch (DataAccessException ex) {
            String msg = "Error when getting book with author = " + author + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Book> findBookByTitle(String title) {
        checkSources();

        if (title == null) {
            throw new IllegalArgumentException("Title is null");
        }

        try {
            return getBooks(title, "TITLE");
        } catch (DataAccessException ex) {
            String msg = "Error when getting book with title = " + title + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private List<Book> getBooks(String string, String findBy) {
        String sql = "SELECT * FROM BOOKS WHERE " + findBy + " = ?";

        return jdbcTemplate.query(sql, bookMapper, string);
    }

}
