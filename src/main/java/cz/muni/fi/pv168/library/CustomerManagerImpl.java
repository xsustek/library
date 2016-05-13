package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.Validator;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Milan Šůstek on 26.02.2016.
 */
public class CustomerManagerImpl implements CustomerManager {

    private static final Logger logger = Logger.getLogger(
            CustomerManagerImpl.class.getName());

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
        } catch (DataAccessException e) {
            String msg = "Error when inserting customer into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    public Customer getCustomerById(Long id) {
        checkSources();

        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        try {
            return jdbcTemplate.queryForObject("SELECT * FROM CUSTOMERS WHERE ID = ?", customerMapper, id);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ServiceFailureException(
                    "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + findAllCustomers());
        } catch (DataAccessException e) {
            String msg = "Error when getting customer from DB";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }

    }

    public List<Customer> findAllCustomers() {
        checkSources();

        try {
            return jdbcTemplate.query("SELECT * FROM CUSTOMERS", customerMapper);
        } catch (DataAccessException ex) {
            String msg = "Error when getting all customers from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Customer> findCustomerByName(String name) {
        checkSources();

        if (Validator.isValidCustomerName(name)) {
            throw new IllegalArgumentException("Invalid customer's name");
        }

        try {
            return jdbcTemplate.query("SELECT * FROM CUSTOMERS WHERE NAME = ?", customerMapper, name);
        } catch (DataAccessException ex) {
            String msg = "Error when getting customer with name = " + name + " from DB";
            logger.log(Level.SEVERE, msg, ex);
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
        } catch (DataAccessException ex) {
            String msg = "Error when updating customer in the db";
            logger.log(Level.SEVERE, msg, ex);
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
        } catch (DataAccessException ex) {
            String msg = "Error when deleting customer from the db";
            logger.log(Level.SEVERE, msg, ex);
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
