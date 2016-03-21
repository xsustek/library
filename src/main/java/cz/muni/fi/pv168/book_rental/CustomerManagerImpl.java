package cz.muni.fi.pv168.book_rental;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Milan on 26.02.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private final DataSource dataSource;

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createCustomer(Customer customer) {
        validate(customer);

        if (customer.getId() != null) {
            throw new IllegalArgumentException("customer id is already set");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO CUSTOMER (name, address, number) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhoneNumber());

            int addedRows = statement.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal error: More rows (" +
                        addedRows + ") inserted when trying to insert customer " + customer);
            }

            ResultSet keys = statement.getGeneratedKeys();
            customer.setId(getKey(keys, customer));
        } catch (SQLException e) {
            throw new ServiceFailureException("Error when inserting customer" + customer, e);
        }

    }

    private void validate(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        if (!isValidName(customer.getName())) {
            throw new IllegalArgumentException("Invalid customer's name");
        }

        if (!isValidAddress(customer.getAddress())) {
            throw new IllegalArgumentException("Invalid customer's address");
        }

        if (!isValidPhoneNumber(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid customer's phone number");
        }
    }

    private Long getKey(ResultSet keyRS, Customer customer) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert customer " + customer
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert customer " + customer
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert customer " + customer
                    + " - no key found");
        }
    }

    public Customer getCustomerById(Long id) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,address,number FROM customer WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Customer customer = resultSetToCustomer(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + findAllCustomers() + " and " + resultSetToCustomer(rs));
                }

                return customer;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving customer with id " + id, ex);
        }

    }

    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setPhoneNumber(rs.getString("number"));
        return customer;
    }

    public List<Customer> findAllCustomers() {

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,address,number FROM customer")) {

            ResultSet rs = st.executeQuery();

            List<Customer> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToCustomer(rs));
            }
            return result;

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all customers", ex);
        }

    }

    public void updateCustomer(Customer customer) {
        validate(customer);
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE customer SET name = ?, address = ?, number = ? WHERE id = ?")) {

            st.setString(1, customer.getName());
            st.setString(2, customer.getAddress());
            st.setString(3, customer.getPhoneNumber());
            st.setLong(4, customer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Customer " + customer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating customer " + customer, ex);
        }

    }

    public void deleteCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM customer WHERE id = ?")) {

            st.setLong(1, customer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Customer " + customer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating customer " + customer, ex);
        }

    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z\\u00c0-\\u017e]+", name);
    }

    private boolean isValidPhoneNumber(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }

        return Pattern.matches("[+]\\d{12}+", number);
    }

    private boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z0-9\\u00c0-\\u017e -]+[,][ ]" +
                "[0-9]{3}+[ ][0-9]{2}+[ ][a-zA-Z0-9\\u00c0-\\u017e., -]+", address);
    }
}
