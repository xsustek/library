package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Created by Milan Šůstek on 26.02.2016.
 */
public class CustomerManagerImpl implements CustomerManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
            CustomerManagerImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void setSources(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void checkSources() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createCustomer(Customer customer) {
        checkSources();
        Validator.validateCustomer(customer);

        if (customer.getId() != null) {
            throw new IllegalEntityException("Customer id is already set");
        }

        String sql = "INSERT INTO CUSTOMERS (NAME, ADDRESS, PHONE_NUMBER) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getName());
                statement.setString(2, customer.getAddress());
                statement.setString(3, customer.getPhoneNumber());
                return statement;
            }, keyHolder);

            DBUtils.checkUpdatesCount(count, customer, true);
            customer.setId(keyHolder.getKey().longValue());

            logger.debug("{} was added", customer.toString());
        } catch (DataAccessException e) {
            String msg = "Error when inserting customer into db";
            logger.error(msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    public Customer getCustomerById(Long id) {
        checkSources();

        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        try {
            Customer customer = jdbcTemplate.queryForObject("SELECT * FROM CUSTOMERS WHERE ID = ?", customerMapper, id);

            logger.debug("{} was returned", customer.toString());

            return customer;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ServiceFailureException(
                    "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + findAllCustomers());
        } catch (DataAccessException e) {
            String msg = "Error when getting customer from DB";
            logger.error(msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    public List<Customer> findAllCustomers() {
        checkSources();

        try {
            List<Customer> customers = jdbcTemplate.query("SELECT * FROM CUSTOMERS", customerMapper);

            logger.debug("{} customers were returned", customers.size());

            return customers;
        } catch (DataAccessException ex) {
            String msg = "Error when getting all customers from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Customer> findCustomerByName(String name) {
        checkSources();

        if (Validator.isValidCustomerName(name)) {
            throw new IllegalArgumentException("Invalid customer's name");
        }

        try {
            List<Customer> customers = jdbcTemplate.query("SELECT * FROM CUSTOMERS WHERE NAME = ?", customerMapper, name);

            logger.debug("{} customers were returned", customers.size());

            return customers;
        } catch (DataAccessException ex) {
            String msg = "Error when getting customer with name = " + name + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateCustomer(Customer customer) {
        checkSources();
        Validator.validateCustomer(customer);

        if (customer.getId() == null) {
            throw new IllegalEntityException("Customer id is null");
        }

        String sql = "UPDATE CUSTOMERS SET NAME = ?, ADDRESS = ?, PHONE_NUMBER = ? WHERE ID = ?";
        try {
            int count = jdbcTemplate.update(sql,
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhoneNumber(),
                    customer.getId());

            DBUtils.checkUpdatesCount(count, customer, false);

            logger.debug("{} was updated", customer.toString());
        } catch (DataAccessException ex) {
            String msg = "Error when updating customer in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public void deleteCustomer(Customer customer) {
        checkSources();
        Validator.validateCustomer(customer);

        if (customer.getId() == null) {
            throw new IllegalEntityException("Customer id is null");
        }

        try {
            int count = jdbcTemplate.update("DELETE FROM CUSTOMERS WHERE ID = ?", customer.getId());
            DBUtils.checkUpdatesCount(count, customer, false);

            logger.debug("{} was deleted", customer);
        } catch (DataAccessException ex) {
            String msg = "Error when deleting customer from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    private RowMapper<Customer> customerMapper = (resultSet, i) -> {
        Customer customer = new Customer();
        customer.setId(resultSet.getLong("id"));
        customer.setName(resultSet.getString("name"));
        customer.setAddress(resultSet.getString("address"));
        customer.setPhoneNumber(resultSet.getString("phone_number"));
        return customer;
    };


}
