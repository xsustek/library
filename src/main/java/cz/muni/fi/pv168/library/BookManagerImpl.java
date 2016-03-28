package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
    private SimpleJdbcInsert insertBook;


    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertBook = new SimpleJdbcInsert(dataSource)
                .withTableName("BOOKS")
                .usingGeneratedKeyColumns("ID");
    }

    private void checkDataSource() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createBook(Book book) {
        checkDataSource();
        validate(book);

        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is null");
        }

        if (book.getId() != null) {
            throw new IllegalEntityException("book id is already set");
        }
        try {
            KeyHolder id = insertBook.executeAndReturnKeyHolder(new HashMap<String, Object>() {
                {
                    put("TITLE", book.getTitle());
                    put("PAGES", book.getPages());
                    put("RELEASE_YEAR", book.getReleaseYear());
                    put("AUTHOR", book.getAuthor());
                }
            });

            book.setId(id.getKey().longValue());

        } catch (DataAccessException e) {
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

    private static final class bookMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            Book book = new Book();
            book.setId(resultSet.getLong("id"));
            book.setTitle(resultSet.getString("title"));
            book.setPages(resultSet.getInt("pages"));
            book.setReleaseYear(resultSet.getDate("release_year"));
            book.setAuthor(resultSet.getString("author"));
            return book;
        }
    }


    public Book getBookById(Long id) {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        String sql = "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Long[]{id}, new bookMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (DataAccessException ex) {
            String msg = "Error when getting book with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Book> findAllBooks() {

        checkDataSource();

        String sql = "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS";
        try {
            return jdbcTemplate.query(sql, new bookMapper());

        } catch (DataAccessException ex) {
            String msg = "Error when getting all books from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateBook(Book book) {
        checkDataSource();
        validate(book);

        if (book.getId() == null) {
            throw new IllegalEntityException("book id is null");
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
        checkDataSource();

        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getId() == null) {
            throw new IllegalEntityException("book id is null");
        }

        String sql = "DELETE FROM BOOKS WHERE ID = ?";
        try {
            int count = jdbcTemplate.update(sql, book.getId());

            DBUtils.checkUpdatesCount(count, book, false);
        } catch (DataAccessException ex) {
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
        try {
            return getBooks(author, "AUTHOR");
        } catch (DataAccessException ex) {
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
        try {
            return getBooks(title, "TITLE");

        } catch (DataAccessException ex) {
            String msg = "Error when getting book with title = " + title + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private List<Book> getBooks(String string, String findBy) {
        String sql = "SELECT ID,TITLE,PAGES,RELEASE_YEAR, AUTHOR FROM BOOKS WHERE " + findBy + " = ?";

        return jdbcTemplate.query(sql, new String[]{string}, new bookMapper());
    }

}
