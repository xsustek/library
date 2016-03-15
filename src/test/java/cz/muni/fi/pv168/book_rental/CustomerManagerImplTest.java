package cz.muni.fi.pv168.book_rental;

import org.junit.Before;
import org.junit.Test;

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
    private Customer customer1;
    private Customer customer2;


    @Before
    public void setUp() {
        manager = new CustomerManagerImpl();
        customer1 = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        customer2 = newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");
    }

    @Test
    public void createCustomer() {
        manager.createCustomer(customer1);

        assertThat(customer1.getId(), is(not(equalTo(null))));

        Customer loadedCustomer = manager.getCustomerById(customer1.getId());

        assertThat(loadedCustomer, is(equalTo(customer1)));
        assertThat(loadedCustomer, is(sameInstance(customer1)));
        assertDeepEquals(customer1, loadedCustomer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCustomerWithNull() {
        manager.createCustomer(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void createCustomerWithWrongName() {
        customer1.setName("65982");

        manager.createCustomer(customer1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCustomerWithWrongAddress() {
        customer1.setAddress("Botanická 68a, Brno-Kralovo Pole");

        manager.createCustomer(customer1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCustomerWithWrongPhoneNumber() {
        customer1.setPhoneNumber("+4209058ab95366");

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

    @Test(expected = IllegalArgumentException.class)
    public void updateCustomerWithWrongName() {
        manager.createCustomer(customer1);

        customer1.setName("554");
        manager.updateCustomer(customer1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCustomerWithWrongAddress() {
        manager.createCustomer(customer1);

        customer1.setAddress(", 943 58 Nicota");
        manager.updateCustomer(customer1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCustomerWithWrongPhoneNumber() {
        manager.createCustomer(customer1);

        customer1.setPhoneNumber("number");
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

    private static Customer newCustomer(String name, String address, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);

        return customer;
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
}
