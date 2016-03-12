package cz.muni.fi.pv168.book_rental;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


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

    @Test
    public void createGraveWithWrongValues() {
        ExpectedException expectedException = ExpectedException.none();

        Customer customer = newCustomer("Jozef Mrkva",
                "Botanická 68a, 602 00 Brno-Královo Pole", "+420905867953");

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
}
