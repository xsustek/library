package cz.muni.fi.pv168.library;

import cz.muni.fi.pv168.common.*;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Milan on 26.02.2016.
 */
public class LeaseManagerImpl implements LeaseManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
            LeaseManagerImpl.class);

    private JdbcTemplate jdbcTemplate;
    private BookManager bookManager;
    private CustomerManager customerManager;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setBookManager(BookManager bookManager) {
        this.bookManager = bookManager;
    }

    public void setSources(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private void checkSources() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is null");
        }

        if (customerManager == null) {
            throw new IllegalStateException("customerManager is null");
        }

        if (bookManager == null) {
            throw new IllegalStateException("bookManager is null");
        }
    }

    public void createLease(Lease lease) {
        checkSources();
        Validator.validateLease(lease);

        if (lease.getId() != null) {
            throw new IllegalEntityException("Lease id is already set");
        }

        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        if (customerManager.getCustomerById(lease.getCustomer().getId()) == null) {
            throw new IllegalEntityException("Customer is not in db");
        }

        if (!isBookAvailable(lease.getBook(), false, lease.getId())) {
            throw new ValidationException("Book is already lent");
        }

        String sql = "INSERT INTO LEASES(CUSTOMER_ID, BOOK_ID, END_TIME, REAL_END_TIME) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            int count = jdbcTemplate.update(connection -> {
                PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                st.setLong(1, lease.getCustomer().getId());
                st.setLong(2, lease.getBook().getId());
                st.setDate(3, toSqlDate(lease.getEndTime()));
                st.setDate(4, toSqlDate(lease.getRealEndTime()));
                return st;
            }, keyHolder);

            DBUtils.checkUpdatesCount(count, lease, true);
            lease.setId(keyHolder.getKey().longValue());

            logger.debug("{} was added", lease.toString());
        } catch (DataAccessException e) {
            String msg = "Error when inserting lease into db";
            logger.error(msg, e);
            throw new ServiceFailureException(msg, e);
        }
    }

    public Lease getLeaseById(Long id) {
        checkSources();

        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        try {
            Lease lease = jdbcTemplate.queryForObject("SELECT * FROM LEASES WHERE ID = ?", leaseMapper, id);

            logger.debug("{} was returned", lease);

            return lease;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ServiceFailureException(
                    "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + findAllLeases());
        } catch (DataAccessException ex) {
            String msg = "Error when getting lease with id = " + id + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Lease> findAllLeases() {
        checkSources();

        try {

            List<Lease> leases = jdbcTemplate.query("SELECT * FROM LEASES", leaseMapper);

            logger.debug("{} leases were returned", leases.size());

            return leases;
        } catch (DataAccessException ex) {
            String msg = "Error when getting all leases from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateLease(Lease lease) {
        checkSources();
        Validator.validateLease(lease);

        if (lease.getId() == null) {
            throw new IllegalEntityException("Lease id is null");
        }

        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        if (!isBookAvailable(lease.getBook(), true, lease.getId())) {
            throw new ValidationException("Book is already lent");
        }

        if (customerManager.getCustomerById(lease.getCustomer().getId()) == null) {
            throw new IllegalEntityException("Customer is not in db");
        }

        String sql = "UPDATE LEASES SET BOOK_ID = ?, CUSTOMER_ID = ?, END_TIME = ?, REAL_END_TIME = ?" +
                " WHERE ID = ?";

        try {
            int count = jdbcTemplate.update(sql,
                    lease.getBook().getId(),
                    lease.getCustomer().getId(),
                    toSqlDate(lease.getEndTime()),
                    toSqlDate(lease.getRealEndTime()),
                    lease.getId());

            DBUtils.checkUpdatesCount(count, lease, false);

            logger.debug("{} was updated", lease.toString());
        } catch (DataAccessException ex) {
            String msg = "Error when updating lease in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void deleteLease(Lease lease) {
        checkSources();
        Validator.validateLease(lease);

        if (lease.getId() == null) {
            throw new IllegalEntityException("Lease id is null");
        }

        try {
            int count = jdbcTemplate.update("DELETE FROM LEASES WHERE ID = ?",
                    lease.getId());
            DBUtils.checkUpdatesCount(count, lease, false);

            logger.debug("{} was deleted", lease.toString());
        } catch (DataAccessException ex) {
            String msg = "Error when deleting lease from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        checkSources();

        if (customer == null) {
            throw new IllegalArgumentException("Customer is null");
        }

        if (customer.getId() == null) {
            throw new IllegalEntityException("Customer id is null");
        }

        try {
            List<Lease> leases = jdbcTemplate.query("SELECT * FROM LEASES WHERE CUSTOMER_ID = ?",
                    leaseMapper, customer.getId());

            logger.debug("{} leases were returned", leases.size());

            return leases;
        } catch (DataAccessException ex) {
            String msg = "Error when getting leases for customer " + customer + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findExpiredLeases() {
        checkSources();

        try {
            Date now = Date.valueOf(LocalDate.now());
            List<Lease> leases = jdbcTemplate.query("SELECT * FROM LEASES WHERE '" + now + "' > END_TIME AND REAL_END_TIME IS NULL",
                    leaseMapper);

            logger.debug("{} expired leases were returned", leases.size());

            return leases;
        } catch (DataAccessException ex) {
            String msg = "Error when getting expired leases from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForBook(Book book) {
        checkSources();

        if (book == null) {
            throw new IllegalArgumentException("Book is null");
        }

        if (book.getId() == null) {
            throw new IllegalEntityException("Book's id is null");
        }

        try {
            List<Lease> leases = jdbcTemplate.query("SELECT * FROM LEASES WHERE BOOK_ID = ?",
                    leaseMapper, book.getId());

            logger.debug("{} leases were returned", leases.size());

            return leases;
        } catch (DataAccessException ex) {
            String msg = "Error when getting leases for book " + book + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public boolean isBookAvailable(Book book) {
        checkSources();

        try {
            String query = "SELECT * FROM LEASES WHERE BOOK_ID = 101 AND (END_TIME IS NULL OR REAL_END_TIME IS NOT NULL)";
            List<Lease> leases = jdbcTemplate.query(query, leaseMapper);

            logger.debug("{} leases were returned", leases.size());

            return leases.isEmpty();
        } catch (DataAccessException ex) {
            String msg = "Error when getting leases from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private Date toSqlDate(java.time.LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }

    private java.time.LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private boolean isBookAvailable(Book book, boolean update, Long id) {
        List<Lease> list = findLeasesForBook(book);

        if (list.isEmpty()) {
            return true;
        }

        for (Lease l : list) {
            if (l.getRealEndTime() == null) {
                return update && l.getId().equals(id);

            }
        }

        return true;
    }

    private final RowMapper<Lease> leaseMapper = (rs, rowNum) -> {
        Lease lease = new Lease();

        lease.setId(rs.getLong("id"));

        lease.setBook(bookManager.getBookById(rs.getLong("book_id")));

        lease.setCustomer(customerManager.getCustomerById(rs.getLong("customer_id")));

        lease.setEndTime(toLocalDate(rs.getDate("end_time")));

        lease.setRealEndTime(toLocalDate(rs.getDate("real_end_time")));

        return lease;
    };

}
