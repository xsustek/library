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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


/**
 * Created by Robert Duriancik on 12.3.2016.
 */
public class CustomerManagerImplTest {

    private CustomerManagerImpl manager;
    private DataSource dataSource;

    private Customer customer1;
    private Customer customer2;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, CustomerManager.class.getResource("createTables.sql"));

        manager = new CustomerManagerImpl();
        manager.setSources(dataSource);

        customer1 = Creator.newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        customer2 = Creator.newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, CustomerManager.class.getResource("dropTables.sql"));
    }

    @Test
    public void createCustomer() {
        manager.createCustomer(customer1);

        assertThat(customer1.getId(), is(not(equalTo(null))));

        Customer loadedCustomer = manager.getCustomerById(customer1.getId());

        assertThat(loadedCustomer, is(equalTo(customer1)));
        assertThat(loadedCustomer, is(not(sameInstance(customer1))));
        assertDeepEquals(customer1, loadedCustomer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCustomerWithNull() {
        manager.createCustomer(null);
    }

    @Test
    public void createCustomerWithNonExistingId() {
        customer1.setId(1L);

        expectedException.expect(IllegalEntityException.class);
        manager.createCustomer(customer1);
    }


    @Test
    public void createCustomerWithWrongName() {
        customer1.setName("65982");

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void createCustomerWithNullName() {
        customer1.setName(null);

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void createCustomerWithWrongAddress() {
        customer1.setAddress("Botanická 68a, Brno-Kralovo Pole");

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void createCustomerWithNullAddress() {
        customer1.setAddress(null);

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void createCustomerWithWrongPhoneNumber() {
        customer1.setPhoneNumber("+4209058ab95366");

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void createCustomerWthNullPhoneNUmber() {
        customer1.setPhoneNumber(null);

        expectedException.expect(ValidationException.class);
        manager.createCustomer(customer1);
    }

    @Test
    public void findAllCustomers() {
        assertTrue(manager.findAllCustomers().isEmpty());

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        List<Customer> expected = Arrays.asList(customer1, customer2);
        List<Customer> retrieved = manager.findAllCustomers();

        Collections.sort(expected, idComparator);
        Collections.sort(retrieved, idComparator);

        assertEquals(expected, retrieved);
        assertDeepEquals(expected, retrieved);
    }

    @Test
    public void updateCustomer() {
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        Long customer1Id = customer1.getId();

        // Change name to Jozef Brkva
        customer1.setName("Jozef Brkva");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat(customer1.getName(), is(equalTo("Jozef Brkva")));
        assertThat(customer1.getAddress(), is(equalTo("Botanická 68a, 602 00 Brno-Královo Pole")));
        assertThat(customer1.getPhoneNumber(), is(equalTo("+420905867953")));

        // Change address to Valaska 20, 615 30 Poliacko
        customer1.setAddress("Valaska 20, 615 30 Poliacko");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat(customer1.getAddress(), is(equalTo("Valaska 20, 615 30 Poliacko")));
        assertThat(customer1.getName(), is(equalTo("Jozef Brkva")));
        assertThat(customer1.getPhoneNumber(), is(equalTo("+420905867953")));

        // Change phone number to +420915768359
        customer1.setPhoneNumber("+420915768359");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat(customer1.getPhoneNumber(), is(equalTo("+420915768359")));
        assertThat(customer1.getName(), is(equalTo("Jozef Brkva")));
        assertThat(customer1.getAddress(), is(equalTo("Valaska 20, 615 30 Poliacko")));

        // Check if updates didn't affected other records
        assertDeepEquals(customer2, manager.getCustomerById(customer2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCustomerWithNull() {
        manager.updateCustomer(null);
    }

    @Test
    public void updateCustomerWithNullId() {
        manager.createCustomer(customer1);

        customer1.setId(null);
        expectedException.expect(IllegalEntityException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithNonExistingId() {
        manager.createCustomer(customer1);

        customer1.setId(customer1.getId() + 1);
        expectedException.expect(IllegalEntityException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithWrongName() {
        manager.createCustomer(customer1);

        customer1.setName("554");
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithNullName() {
        manager.createCustomer(customer1);

        customer1.setName(null);
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithWrongAddress() {
        manager.createCustomer(customer1);

        customer1.setAddress(", 943 58 Nicota");
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithNullAddress() {
        manager.createCustomer(customer1);

        customer1.setAddress(null);
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithWrongPhoneNumber() {
        manager.createCustomer(customer1);

        customer1.setPhoneNumber("number");
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void updateCustomerWithNullPhoneNumber() {
        manager.createCustomer(customer1);

        customer1.setPhoneNumber(null);
        expectedException.expect(ValidationException.class);
        manager.updateCustomer(customer1);
    }

    @Test
    public void deleteCustomer() {
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        assertNotNull(manager.getCustomerById(customer1.getId()));
        assertNotNull(manager.getCustomerById(customer2.getId()));

        manager.deleteCustomer(customer1);

        assertNull(manager.getCustomerById(customer1.getId()));
        assertNotNull(manager.getCustomerById(customer2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCustomerWithNull() {
        manager.deleteCustomer(null);
    }

    @Test
    public void deleteCustomerWithNullId() {
        customer1.setId(null);

        expectedException.expect(IllegalEntityException.class);
        manager.deleteCustomer(customer1);
    }

    @Test
    public void deleteCustomerWithNonExistingId() {
        manager.createCustomer(customer1);
        customer1.setId(customer1.getId());

        customer1.setId(customer1.getId() + 1);
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCustomer(customer1);
    }

    private void assertDeepEquals(Customer expected, Customer actual) {
        assertEquals("id value is not equal", expected.getId(), actual.getId());
        assertEquals("name value is not equal", expected.getName(), actual.getName());
        assertEquals("address value is not equal", expected.getAddress(), actual.getAddress());
        assertEquals("phoneNumber value is not equal", expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    private void assertDeepEquals(List<Customer> expected, List<Customer> retrieved) {
        for (int i = 0; i < expected.size(); i++) {
            Customer customer1 = expected.get(i);
            Customer customer2 = retrieved.get(i);

            assertDeepEquals(customer1, customer2);
        }
    }

    private static Comparator<Customer> idComparator = new Comparator<Customer>() {
        public int compare(Customer o1, Customer o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:customermgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
}
