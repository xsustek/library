package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Created by Milan on 15.03.2016.
 */
public class BookManagerImplTest {
    private BookManagerImpl manager;
    private Book book;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("createTables.sql"));

        manager = new BookManagerImpl();
        manager.setDataSource(dataSource);
        book = newBook("Pejsek a kocicka", 87, new GregorianCalendar(1978, 8, 5).getTime(), "Karel Capek");
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:bookmgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void createBook() {
        manager.createBook(book);

        assertThat("Book has null id", book.getId(), is(not(equalTo(null))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullBook() {
        manager.createBook(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongTitle() {
        book.setTitle(null);
        manager.createBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithZeroPages() {
        book.setPages(0);
        manager.createBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithNegativePages() {
        book.setPages(-2);
        manager.createBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongDate() {
        book.setReleaseYear(null);
        manager.createBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithNullAuthor() {
        book.setAuthor(null);
        manager.createBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongAuthor() {
        book.setAuthor("1234567");
        manager.createBook(book);
    }

    @Test
    public void loadBook() {
        manager.createBook(book);
        Book loaded = manager.getBookById(book.getId());

        assertThat("Loaded book differ from the saved one", book, is(equalTo(loaded)));
        assertThat("Loaded book is same instance", book, is(not(sameInstance(loaded))));
        assertDeepEquals(book, loaded);
    }

    @Test
    public void loadAllBook() {
        Book book1 = newBook("Jaja a Paja", 80, new GregorianCalendar(1998, 8, 5).getTime(), "Karel Capek");
        Book book2 = newBook("Kosek a Bosek", 97, new GregorianCalendar(1968, 8, 5).getTime(), "Karel Capek");

        manager.createBook(book);
        manager.createBook(book1);
        manager.createBook(book2);

        List<Book> retrieved = manager.findAllBooks();
        List<Book> expected = Arrays.asList(book, book1, book2);

        Collections.sort(retrieved, idComparator);
        Collections.sort(expected, idComparator);

        assertDeepEquals(expected, retrieved);

    }

    @Test
    public void updateBookTitle() {
        manager.createBook(book);

        book.setTitle("Jaja a Paja");
        manager.updateBook(book);

        Book retrieved = manager.getBookById(book.getId());

        assertThat("Title is not changed", book.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(book.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(book.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(book.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookAuthor() {
        manager.createBook(book);

        book.setAuthor("Petr Zelenka");
        manager.updateBook(book);

        Book retrieved = manager.getBookById(book.getId());

        assertThat(book.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(book.getPages(), is(equalTo(retrieved.getPages())));
        assertThat("Author is not changed", book.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(book.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookPages() {
        manager.createBook(book);

        book.setPages(100);
        manager.updateBook(book);

        Book retrieved = manager.getBookById(book.getId());

        assertThat(book.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat("Pages is not changed", book.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(book.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(book.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookDate() {
        manager.createBook(book);

        book.setReleaseYear(new GregorianCalendar(1999, 9, 9).getTime());
        manager.updateBook(book);

        Book retrieved = manager.getBookById(book.getId());

        assertThat(book.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(book.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(book.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat("Date is not changed", book.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void deleteBook() {
        manager.createBook(book);
        manager.deleteBook(book);

        List<Book> retrieved = manager.findAllBooks();
        assertThat(retrieved.size(), is(equalTo(0)));
    }


    public Book newBook(String title, int pages, Date releaseYear, String author) {
        Book b = new Book();
        b.setTitle(title);
        b.setPages(pages);
        b.setReleaseYear(releaseYear);
        b.setAuthor(author);
        return b;
    }

    private void assertDeepEquals(List<Book> expectedList, List<Book> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Book expected = expectedList.get(i);
            Book actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Book expected, Book actual) {
        assertEquals("id value is not equal", expected.getId(), actual.getId());
        assertEquals("title value is not equal", expected.getTitle(), actual.getTitle());
        assertEquals("release year value is not equal", expected.getReleaseYear(), actual.getReleaseYear());
        assertEquals("author value is not equal", expected.getAuthor(), actual.getAuthor());
        assertEquals("pages value is not equal", expected.getPages(), actual.getPages());
    }

    private static Comparator<Book> idComparator = new Comparator<Book>() {
        public int compare(Book o1, Book o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

}
