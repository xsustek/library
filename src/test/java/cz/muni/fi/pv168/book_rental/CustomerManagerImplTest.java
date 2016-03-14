package cz.muni.fi.pv168.book_rental;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

    @Before
    public void setUp() {
        manager = new CustomerManagerImpl();
    }

    @Test
    public void createCustomer() {
        Customer customer = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        manager.createCustomer(customer);

        assertThat("saved customer has null id", customer.getId(), is(not(equalTo(null))));

        Customer loadedCustomer = manager.getCustomerById(customer.getId());
        assertThat("loaded customer differs from the saved one", loadedCustomer, is(equalTo(customer)));
        assertThat("loaded customer is the same instance", loadedCustomer, is(sameInstance(customer)));
        assertDeepEquals(customer, loadedCustomer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGraveWithNull() {
        manager.createCustomer(null);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createCustomerWithWrongValues() {
        Customer customer = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+4209058ab95366");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid phone number not detected");
        manager.createCustomer(customer);

        customer = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "420905867953");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid phone number not detected");
        manager.createCustomer(customer);

        customer = newCustomer("65982",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid name not detected");
        manager.createCustomer(customer);

        customer = newCustomer("Jozef Mrkva", "Botanická 68a", "+420905867953");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid address not detected");
        manager.createCustomer(customer);

        customer = newCustomer("Jozef Mrkva",
                "Botanická 68a, Brno-Královo Pole", "+420905867953");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid address not detected");
        manager.createCustomer(customer);

    }

    @Test
    public void findAllCustomers() {
        assertTrue(manager.findAllCustomers().isEmpty());

        Customer customer1 = newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");
        Customer customer2 = newCustomer("František Testovací",
                "Nová 5, 952 46 Novohrad", "+420932456789");

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        List<Customer> expected = Arrays.asList(customer1, customer2);
        List<Customer> retrieved = manager.findAllCustomers();

        Collections.sort(expected, idComparator);
        Collections.sort(retrieved, idComparator);

        assertEquals("expected and retrieved customers differ", expected, retrieved);
        assertDeepEquals(expected, retrieved);
    }

    @Test
    public void updateCustomer() {
        Customer customer1 = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        Customer customer2 = newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        Long customer1Id = customer1.getId();

        // Change name to Jozef Brkva
        customer1.setName("Jozef Brkva");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat("name was not changed", customer1.getName(), is(equalTo("Jozef Brkva")));
        assertThat("address was changed when changing name",
                customer1.getAddress(), is(equalTo("Botanická 68a, 602 00 Brno-Královo Pole")));
        assertThat("phone number was changed when changing name",
                customer1.getPhoneNumber(), is(equalTo("+420905867953")));

        // Change address to Valaska 20, 615 30 Poliacko
        customer1.setAddress("Valaska 20, 615 30 Poliacko");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat("address was not changed",
                customer1.getAddress(), is(equalTo("Valaska 20, 615 30 Poliacko")));
        assertThat("name was changed when changing address",
                customer1.getName(), is(equalTo("Jozef Brkva")));
        assertThat("phone number was changed when changing address",
                customer1.getPhoneNumber(), is(equalTo("+420905867953")));

        // Change phone number to +420915768359
        customer1.setPhoneNumber("+420915768359");
        manager.updateCustomer(customer1);

        customer1 = manager.getCustomerById(customer1Id);

        assertThat("phone number was not changed",
                customer1.getPhoneNumber(), is(equalTo("+420915768359")));
        assertThat("name was changed when changing phone number",
                customer1.getName(), is(equalTo("Jozef Mrkva")));
        assertThat("address was changed when changing phone number",
                customer1.getAddress(), is(equalTo("Botanická 68a, 602 00 Brno-Královo Pole")));

        // Check if updates didn't affected other records
        assertDeepEquals(customer2, manager.getCustomerById(customer2.getId()));
    }

    @Test
    public void updateCustomerWithWrongAttributes() {

    }

    @Test
    public void deleteCustomer() {
        Customer customer1 = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");
        Customer customer2 = newCustomer("Ján Otrok",
                "Obchodná 9, 613 05 Albertov", "+420915687932");

        manager.createCustomer(customer1);
        manager.createCustomer(customer2);

        assertNotNull(manager.getCustomerById(customer1.getId()));
        assertNotNull(manager.getCustomerById(customer2.getId()));

        manager.deleteCustomer(customer1);

        assertNull(manager.getCustomerById(customer1.getId()));
        assertNotNull(manager.getCustomerById(customer2.getId()));
    }

    @Test
    public void deleteGraveWithWrongAttributes() {

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
