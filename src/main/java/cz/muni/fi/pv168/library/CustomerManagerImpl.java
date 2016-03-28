package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.IllegalEntityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import cz.muni.fi.pv168.common.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    public void setSources(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("CUSTOMERS")
                .usingGeneratedKeyColumns("ID");
    }

    public void checkSources() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createCustomer(Customer customer) {
        checkSources();
        validate(customer);

        if (customer.getId() != null) {
            throw new IllegalEntityException("customer id is already set");
        }

        try {
            KeyHolder keyHolder = jdbcInsert.executeAndReturnKeyHolder(new HashMap<String, Object>() {
                {
                    put("NAME", customer.getName());
                    put("ADDRESS", customer.getAddress());
                    put("PHONE_NUMBER", customer.getPhoneNumber());
                }
            });

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
            throw new IllegalArgumentException("id is null");
        }

        String sql = "SELECT ID,NAME,ADDRESS,PHONE_NUMBER FROM CUSTOMERS WHERE ID = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Long[]{id}, new customerMapper());

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

        String sql = "SELECT ID,NAME,ADDRESS,PHONE_NUMBER FROM CUSTOMERS";
        try {
            return jdbcTemplate.query(sql, new customerMapper());
        } catch (DataAccessException ex) {
            String msg = "Error when getting all customers from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public void updateCustomer(Customer customer) {
        checkSources();
        validate(customer);

        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
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

        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
        }

        String sql = "DELETE FROM CUSTOMERS WHERE ID = ?";
        try {
            int count = jdbcTemplate.update(sql, customer.getId());
            DBUtils.checkUpdatesCount(count, customer, false);
        } catch (DataAccessException ex) {
            String msg = "Error when deleting customer from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    private static final class customerMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
            Customer customer = new Customer();
            customer.setId(resultSet.getLong("id"));
            customer.setName(resultSet.getString("name"));
            customer.setAddress(resultSet.getString("address"));
            customer.setPhoneNumber(resultSet.getString("phone_number"));
            return customer;
        }
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
