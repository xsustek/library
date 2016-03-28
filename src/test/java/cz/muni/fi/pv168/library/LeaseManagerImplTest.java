package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ValidationException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Robert Duriancik, 445363
 */
public class LeaseManagerImplTest {

    private LeaseManagerImpl leaseManager;
    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    private DataSource dataSource;

    private Customer c1, c2, customerWithNullId, customerNotInDB;
    private Book b1, b2, bookWithNullId, bookNotInDB;
    private Lease l1, l2, leaseWithNullId, leaseNotInDB;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResource("createTables.sql"));

        leaseManager = new LeaseManagerImpl();
        leaseManager.setDataSource(dataSource);
        bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);

        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, LeaseManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void createLease() {
        leaseManager.createLease(l1);

        assertThat(l1.getId(), is(not(equalTo(null))));

        Lease loadedLease = leaseManager.getLeaseById(l1.getId());

        assertThat(loadedLease, is(equalTo(l1)));
        assertThat(loadedLease, is(not(sameInstance(l1))));
        assertDeepEquals(l1, loadedLease);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLeaseWithNull() {
        leaseManager.createLease(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createLeaseWithNonExistingId() {
        leaseManager.createLease(leaseNotInDB);
    }

    @Test
    public void createLeaseWithNullBook() {
        l1.setBook(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullCustomer() {
        l1.setCustomer(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullBookId() {
        l1.getBook().setId(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullCustomerId() {
        l1.getCustomer().setId(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullEndTime() {
        l1.setEndTime(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLeaseWithNull() {
        leaseManager.updateLease(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteLeaseWithNull() {
        leaseManager.deleteLease(null);
    }

    private void prepareTestData() {

        c1 = Creator.newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        c2 = Creator.newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");
        customerWithNullId = Creator.newCustomer("Patrik Stary",
                "Mestska 5, 621 58 Domov", "+420976325876");
        customerWithNullId.setId(null);
        customerNotInDB = Creator.newCustomer("John Travolt",
                "Slavna 10, 985 26 Celebritno", "+420987635214");
        customerNotInDB.setId(5L);

        customerManager.createCustomer(c1);
        customerManager.createCustomer(c2);

        b1 = Creator.newBook("Jaja a Paja", 80, new GregorianCalendar(1998, 8, 5).getTime(), "Capek");
        b2 = Creator.newBook("Kosek a Bosek", 97, new GregorianCalendar(1968, 8, 5).getTime(), "Capek");
        bookWithNullId = Creator.newBook("Stary otec", 120, new GregorianCalendar(1990, 30, 5).getTime(), "Julius");
        bookWithNullId.setId(null);
        bookNotInDB = Creator.newBook("Milionar", 200, new GregorianCalendar(2000, 24, 3).getTime(), "Zivotny");
        bookNotInDB.setId(5L);

        bookManager.createBook(b1);
        bookManager.createBook(b2);

        l1 = Creator.newLease(b1, c1, new GregorianCalendar(2016, 1, 4).getTime(), null);
        l2 = Creator.newLease(b2, c2, new GregorianCalendar(2016, 1, 1).getTime(),
                new GregorianCalendar(2016, 1, 1).getTime());
        leaseWithNullId = Creator.newLease(b1, c2, new GregorianCalendar(2016, 12, 7).getTime(), null);
        leaseWithNullId.setId(null);
        leaseNotInDB = Creator.newLease(b2, c1, new GregorianCalendar(2016, 3, 3).getTime(), null);
        leaseNotInDB.setId(5L);

    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:leasemgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    private void assertDeepEquals(Lease expected, Lease actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBook(), actual.getBook());
        assertEquals(expected.getCustomer(), actual.getCustomer());
        assertEquals(expected.getEndTime(), actual.getEndTime());
        assertEquals(expected.getRealEndTime(), actual.getRealEndTime());
    }

    private void assertDeepEquals(List<Lease> expected, List<Lease> retrieved) {
        for (int i = 0; i < expected.size(); i++) {
            Lease lease1 = expected.get(i);
            Lease lease2 = retrieved.get(i);

            assertDeepEquals(lease1, lease2);
        }
    }
}
