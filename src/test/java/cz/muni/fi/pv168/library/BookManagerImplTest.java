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

    private DataSource dataSource;

    private Book rur, valkaSMloky, boureMecu;

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("createTables.sql"));


        manager = new BookManagerImpl();
        manager.setDataSource(dataSource);
        rur = Creator.newBook("R.U.R", 80, new GregorianCalendar(1920, 2, 5).getTime(), "Karel Capek");
        valkaSMloky = Creator.newBook("Valka s mloky", 97, new GregorianCalendar(1936, 9, 2).getTime(), "Karel Capek");
        boureMecu = Creator.newBook("Boure mecu", 97, new GregorianCalendar(2011, 9, 2).getTime(), "George Raymond Richard Martin");
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
        manager.createBook(rur);

        assertThat("Book has null id", rur.getId(), is(not(equalTo(null))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullBook() {
        manager.createBook(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongTitle() {
        rur.setTitle(null);
        manager.createBook(rur);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithZeroPages() {
        rur.setPages(0);
        manager.createBook(rur);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithNegativePages() {
        rur.setPages(-2);
        manager.createBook(rur);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongDate() {
        rur.setReleaseYear(null);
        manager.createBook(rur);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithNullAuthor() {
        rur.setAuthor(null);
        manager.createBook(rur);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookWithWrongAuthor() {
        rur.setAuthor("1234567");
        manager.createBook(rur);
    }

    @Test
    public void loadBook() {
        manager.createBook(rur);
        Book loaded = manager.getBookById(rur.getId());

        assertThat("Loaded book differ from the saved one", rur, is(equalTo(loaded)));
        assertThat("Loaded book is same instance", rur, is(not(sameInstance(loaded))));
        assertDeepEquals(rur, loaded);
    }

    @Test
    public void loadAllBook() {


        manager.createBook(boureMecu);
        manager.createBook(rur);
        manager.createBook(valkaSMloky);

        List<Book> retrieved = manager.findAllBooks();
        List<Book> expected = Arrays.asList(boureMecu, rur, valkaSMloky);

        Collections.sort(retrieved, idComparator);
        Collections.sort(expected, idComparator);

        assertDeepEquals(expected, retrieved);

    }

    @Test
    public void updateBookTitle() {
        manager.createBook(rur);

        rur.setTitle("Jaja a Paja");
        manager.updateBook(rur);

        Book retrieved = manager.getBookById(rur.getId());

        assertThat("Title is not changed", rur.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(rur.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(rur.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(rur.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookAuthor() {
        manager.createBook(rur);

        rur.setAuthor("Petr Zelenka");
        manager.updateBook(rur);

        Book retrieved = manager.getBookById(rur.getId());

        assertThat(rur.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(rur.getPages(), is(equalTo(retrieved.getPages())));
        assertThat("Author is not changed", rur.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(rur.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookPages() {
        manager.createBook(rur);

        rur.setPages(100);
        manager.updateBook(rur);

        Book retrieved = manager.getBookById(rur.getId());

        assertThat(rur.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat("Pages is not changed", rur.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(rur.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat(rur.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void updateBookDate() {
        manager.createBook(rur);

        rur.setReleaseYear(new GregorianCalendar(1999, 9, 9).getTime());
        manager.updateBook(rur);

        Book retrieved = manager.getBookById(rur.getId());

        assertThat(rur.getTitle(), is(equalTo(retrieved.getTitle())));
        assertThat(rur.getPages(), is(equalTo(retrieved.getPages())));
        assertThat(rur.getAuthor(), is(equalTo(retrieved.getAuthor())));
        assertThat("Date is not changed", rur.getReleaseYear(), is(equalTo(retrieved.getReleaseYear())));
    }

    @Test
    public void deleteBook() {
        manager.createBook(rur);
        manager.deleteBook(rur);

        List<Book> retrieved = manager.findAllBooks();
        assertThat(retrieved.size(), is(equalTo(0)));
    }

    @Test
    public void findByAuthor() {
        manager.createBook(rur);
        manager.createBook(valkaSMloky);
        manager.createBook(boureMecu);

        List<Book> retrieved = manager.findBookByAuthor("Karel Capek");

        assertDeepEquals(Arrays.asList(rur, valkaSMloky), retrieved);


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

    private static Comparator<Book> idComparator = (o1, o2) -> o1.getId().compareTo(o2.getId());

}
