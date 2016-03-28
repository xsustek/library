package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by Milan Šůstek on 26.02.2016.
 */
public class CustomerManagerImpl implements CustomerManager {

    private static final Logger logger = Logger.getLogger(
            CustomerManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createCustomer(Customer customer) {
        checkDataSource();
        validate(customer);

        if (customer.getId() != null) {
            throw new IllegalEntityException("customer id is already set");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO CUSTOMERS (NAME, ADDRESS, PHONE_NUMBER) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhoneNumber());

            int addedRows = statement.executeUpdate();
            DBUtils.checkUpdatesCount(addedRows, customer, true);

            Long id = DBUtils.getId(statement.getGeneratedKeys());
            customer.setId(id);
        } catch (SQLException e) {
            String msg = "Error when inserting customer into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    public Customer getCustomerById(Long id) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,NAME,ADDRESS,PHONE_NUMBER FROM CUSTOMERS WHERE ID = ?")) {

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
            String msg = "Error when getting customer with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Customer> findAllCustomers() {
        checkDataSource();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT ID,NAME,ADDRESS,PHONE_NUMBER FROM CUSTOMERS")) {

            ResultSet rs = st.executeQuery();
            List<Customer> result = new ArrayList<>();

            while (rs.next()) {
                result.add(resultSetToCustomer(rs));
            }

            return result;

        } catch (SQLException ex) {
            String msg = "Error when getting all customers from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public void updateCustomer(Customer customer) {
        checkDataSource();
        validate(customer);

        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE CUSTOMERS SET NAME = ?, ADDRESS = ?, PHONE_NUMBER = ? WHERE ID = ?")) {

            st.setString(1, customer.getName());
            st.setString(2, customer.getAddress());
            st.setString(3, customer.getPhoneNumber());
            st.setLong(4, customer.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
        } catch (SQLException ex) {
            String msg = "Error when updating customer in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public void deleteCustomer(Customer customer) {
        checkDataSource();

        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM CUSTOMERS WHERE ID = ?")) {

            st.setLong(1, customer.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
        } catch (SQLException ex) {
            String msg = "Error when deleting customer from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        return customer;
    }

    private void validate(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        if (!isValidName(customer.getName())) {
            throw new ValidationException("Invalid customer's name");
        }

        if (!isValidAddress(customer.getAddress())) {
            throw new ValidationException("Invalid customer's address");
        }

        if (!isValidPhoneNumber(customer.getPhoneNumber())) {
            throw new ValidationException("Invalid customer's phone number");
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z\\u00c0-\\u017e ]+", name);
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
