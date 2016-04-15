package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.ValidationException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
        bookManager = new BookManagerImpl();
        bookManager.setSources(dataSource);
        customerManager = new CustomerManagerImpl();
        customerManager.setSources(dataSource);
        leaseManager.setBookManager(bookManager);
        leaseManager.setCustomerManager(customerManager);
        leaseManager.setSources(dataSource);
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
        l1.setBook(bookWithNullId);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullCustomerId() {
        l1.setCustomer(customerWithNullId);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithNullEndTime() {
        l1.setEndTime(null);

        expectedException.expect(ValidationException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithBookNotInDB() {
        l1.setBook(bookNotInDB);

        expectedException.expect(IllegalEntityException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithCustomerNotInDB() {
        l1.setCustomer(customerNotInDB);

        expectedException.expect(IllegalEntityException.class);
        leaseManager.createLease(l1);
    }

    @Test
    public void createLeaseWithLentBook() {
        l1.setRealEndTime(null);
        leaseManager.createLease(l1);

        l2.setBook(l1.getBook());
        expectedException.expect(ServiceFailureException.class);
        leaseManager.createLease(l2);
    }

    @Test
    public void createLeaseWithAvailableBook() {
        l1.setRealEndTime(null);
        leaseManager.createLease(l1);

        l1.setRealEndTime(new GregorianCalendar(2016, 1, 4).getTime());
        leaseManager.updateLease(l1);

        l2.setBook(l1.getBook());
        leaseManager.createLease(l2);

        assertTrue(leaseManager.findAllLeases().size() == 2);
    }

    @Test
    public void findAllLeases() {
        assertTrue(leaseManager.findAllLeases().isEmpty());

        leaseManager.createLease(l1);
        leaseManager.createLease(l2);

        List<Lease> expected = Arrays.asList(l1, l2);
        List<Lease> retrieved = leaseManager.findAllLeases();

        Collections.sort(expected, idComparator);
        Collections.sort(retrieved, idComparator);

        assertEquals(expected, retrieved);
        assertDeepEquals(expected, retrieved);
    }

    @Test
    public void updateLease() {
        leaseManager.createLease(l1);
        leaseManager.createLease(l2);

        Long l1Id = l1.getId();

        l1.setRealEndTime(new GregorianCalendar(2016, 1, 4).getTime());
        leaseManager.updateLease(l1);

        l1 = leaseManager.getLeaseById(l1Id);

        assertThat(l1.getRealEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));
        assertThat(l1.getBook(), is(equalTo(b1)));
        assertThat(l1.getCustomer(), is(equalTo(c1)));
        assertThat(l1.getEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));

        l1.setBook(b2);
        leaseManager.updateLease(l1);

        l1 = leaseManager.getLeaseById(l1Id);

        assertThat(l1.getRealEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));
        assertThat(l1.getBook(), is(equalTo(b2)));
        assertThat(l1.getCustomer(), is(equalTo(c1)));
        assertThat(l1.getEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));

        l1.setCustomer(c2);
        leaseManager.updateLease(l1);

        l1 = leaseManager.getLeaseById(l1Id);

        assertThat(l1.getRealEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));
        assertThat(l1.getBook(), is(equalTo(b2)));
        assertThat(l1.getCustomer(), is(equalTo(c2)));
        assertThat(l1.getEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));

        l1.setEndTime(new GregorianCalendar(2015, 1, 4).getTime());
        leaseManager.updateLease(l1);

        l1 = leaseManager.getLeaseById(l1Id);

        assertThat(l1.getRealEndTime(), is(equalTo(new GregorianCalendar(2016, 1, 4).getTime())));
        assertThat(l1.getBook(), is(equalTo(b2)));
        assertThat(l1.getCustomer(), is(equalTo(c2)));
        assertThat(l1.getEndTime(), is(equalTo(new GregorianCalendar(2015, 1, 4).getTime())));

        assertDeepEquals(l1, leaseManager.getLeaseById(l1Id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateLeaseWithNull() {
        leaseManager.updateLease(null);
    }

    @Test
    public void updateLeaseWithNullBook() {
        leaseManager.createLease(l1);
        l1.setBook(null);

        expectedException.expect(ValidationException.class);
        leaseManager.updateLease(l1);
    }

    @Test
    public void updateLeaseWithNullCustomer() {
        leaseManager.createLease(l1);
        l1.setCustomer(null);

        expectedException.expect(ValidationException.class);
        leaseManager.updateLease(l1);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateLeaseWithNonExistingId() {
        leaseManager.updateLease(leaseNotInDB);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateLeaseWithNullId() {
        leaseManager.updateLease(leaseWithNullId);
    }

    @Test
    public void updateLeaseWithNullBookId() {
        leaseManager.createLease(l1);

        l1.setBook(bookWithNullId);

        expectedException.expect(ValidationException.class);
        leaseManager.updateLease(l1);
    }

    @Test
    public void updateLeaseWithNullCustomerId() {
        leaseManager.createLease(l1);

        l1.setCustomer(customerWithNullId);

        expectedException.expect(ValidationException.class);
        leaseManager.updateLease(l1);
    }

    @Test
    public void updateLeaseWithNullEndTime() {
        leaseManager.createLease(l1);

        l1.setEndTime(null);

        expectedException.expect(ValidationException.class);
        leaseManager.updateLease(l1);
    }

    @Test
    public void updateLeaseWithLentBook() {
        leaseManager.createLease(l1);
        l1.setRealEndTime(null);
        leaseManager.createLease(l2);

        l2.setBook(l1.getBook());
        expectedException.expect(ServiceFailureException.class);
        leaseManager.updateLease(l2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteLeaseWithNull() {
        leaseManager.deleteLease(null);
    }

    @Test
    public void deleteLease() {
        leaseManager.createLease(l1);
        leaseManager.createLease(l2);

        assertNotNull(leaseManager.getLeaseById(l1.getId()));
        assertNotNull(leaseManager.getLeaseById(l2.getId()));

        leaseManager.deleteLease(l1);

        assertNull(leaseManager.getLeaseById(l1.getId()));
        assertNotNull(leaseManager.getLeaseById(l2.getId()));
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteLeaseNotInDB() {
        leaseManager.deleteLease(leaseNotInDB);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteLeaseWithNulId() {
        leaseManager.deleteLease(leaseWithNullId);
    }

    @Test
    public void findLeasesForCustomer() {
        leaseManager.createLease(l1);
        l2.setCustomer(l1.getCustomer());
        leaseManager.createLease(l2);

        List<Lease> loadedLeases = leaseManager.findLeasesForCustomer(l1.getCustomer());
        List<Lease> lease = java.util.Arrays.asList(l1, l2);

        assertTrue(loadedLeases.size() == 2);

        Collections.sort(loadedLeases, idComparator);
        Collections.sort(lease, idComparator);

        assertEquals(loadedLeases, lease);
        assertDeepEquals(loadedLeases, lease);

    }

    @Test
    public void findLeasesForCustomerWithoutLeases() {
        List<Lease> leases = leaseManager.findLeasesForCustomer(c1);

        assertTrue(leases.isEmpty());
    }

    @Test
    public void findExpiredLeases() {
        l1.setEndTime(new GregorianCalendar(2016, 1, 4).getTime());
        l2.setEndTime(new GregorianCalendar(2015, 2, 8).getTime());

        leaseManager.createLease(l1);
        leaseManager.createLease(l2);

        l1.setRealEndTime(new GregorianCalendar(2016, 3, 4).getTime());
        l2.setRealEndTime(new GregorianCalendar(2015, 1, 8).getTime());

        leaseManager.updateLease(l1);
        leaseManager.updateLease(l2);

        List<Lease> expired = leaseManager.findExpiredLeases();

        assertTrue(expired.size() == 1);
        assertThat(expired.get(0), is(equalTo(l1)));
        assertDeepEquals(expired.get(0), l1);
    }

    @Test
    public void findLeaseForBook() {
        leaseManager.createLease(l1);
        leaseManager.createLease(l2);

        List<Lease> loadedLeases = leaseManager.findLeasesForBook(l1.getBook());
        List<Lease> lease = Collections.singletonList(l1);

        assertTrue(loadedLeases.size() == 1);

        assertEquals(loadedLeases, lease);
        assertDeepEquals(loadedLeases, lease);
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

        b1 = Creator.newBook("Jaja a Paja", 80, new GregorianCalendar(1998, 8, 5).getTime(), "Karel Capek");
        b2 = Creator.newBook("Kosek a Bosek", 97, new GregorianCalendar(1968, 8, 5).getTime(), "Karel Capek");
        bookWithNullId = Creator.newBook("Stary otec", 120, new GregorianCalendar(1990, 30, 5).getTime(), "Michal Julius");
        bookWithNullId.setId(null);
        bookNotInDB = Creator.newBook("Milionar", 200, new GregorianCalendar(2000, 24, 3).getTime(), "Jozef Zivotny");
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

    private static Comparator<Lease> idComparator = new Comparator<Lease>() {
        public int compare(Lease o1, Lease o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}
