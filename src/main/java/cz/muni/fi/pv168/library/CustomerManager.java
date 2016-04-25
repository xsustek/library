package cz.muni.fi.pv168.library;

import java.util.List;

/**
 * Created by Milan on 26.02.2016.
 */
public interface CustomerManager {
    void createCustomer(Customer customer);

    Customer getCustomerById(Long id);

    List<Customer> findAllCustomers();

    List<Customer> findCustomerByName(String name);

    void updateCustomer(Customer customer);

    void deleteCustomer(Customer customer);
}
