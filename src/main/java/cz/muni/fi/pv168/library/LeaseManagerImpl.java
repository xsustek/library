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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Milan on 26.02.2016.
 */
public class LeaseManagerImpl implements LeaseManager {

    private static final Logger logger = Logger.getLogger(
            LeaseManagerImpl.class.getName());

    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;

    public void setSources(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private void checkSources() {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is null");
        }
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createLease(Lease lease) {
        checkSources();
        validate(lease);

        if (lease.getId() != null) {
            throw new IllegalEntityException("lease id is already set");
        }

        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setSources(dataSource);
        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setSources(dataSource);
        if (customerManager.getCustomerById(lease.getCustomer().getId()) == null) {
            throw new IllegalEntityException("Customer is not in db");
        }

        if (!isBookAvailable(lease.getBook(), false, lease.getId())) {
            throw new ServiceFailureException("Book is already lent");
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

            lease.setId(keyHolder.getKey().longValue());
            DBUtils.checkUpdatesCount(count, lease, true);
        } catch (DataAccessException e) {
            String msg = "Error when inserting lease into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }
    }

    public Lease getLeaseById(Long id) {
        checkSources();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        String sql = "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME FROM LEASES WHERE ID = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Long[]{id}, leaseMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new ServiceFailureException(
                    "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + findAllLeases());
        } catch (DataAccessException ex) {
            String msg = "Error when getting lease with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }

    }

    public List<Lease> findAllLeases() {
        checkSources();

        String sql = "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME FROM LEASES";

        try {
            return jdbcTemplate.query(sql, leaseMapper);
        } catch (DataAccessException ex) {
            String msg = "Error when getting all leases from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateLease(Lease lease) {
        checkSources();
        validate(lease);

        if (lease.getId() == null) {
            throw new IllegalEntityException("lease id is null");
        }

        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setSources(dataSource);
        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        if (!isBookAvailable(lease.getBook(), true, lease.getId())) {
            throw new ServiceFailureException("Book is already lent");
        }

        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setSources(dataSource);
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
        } catch (DataAccessException ex) {
            String msg = "Error when updating lease in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void deleteLease(Lease lease) {
        checkSources();

        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }

        if (lease.getId() == null) {
            throw new IllegalEntityException("lease id is null");
        }

        String sql = "DELETE FROM LEASES WHERE ID = ?";

        try {
            int count = jdbcTemplate.update(sql,
                    lease.getId());
            DBUtils.checkUpdatesCount(count, lease, false);
        } catch (DataAccessException ex) {
            String msg = "Error when deleting lease from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        checkSources();

        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
        }

        String sql = "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                "FROM LEASES WHERE CUSTOMER_ID = ?";

        try {
            return jdbcTemplate.query(sql, leaseMapper, customer.getId());
        } catch (DataAccessException ex) {
            String msg = "Error when getting leases for customer " + customer + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findExpiredLeases() {
        checkSources();

        String sql = "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                "FROM LEASES WHERE REAL_END_TIME > END_TIME";

        try {
            return jdbcTemplate.query(sql, leaseMapper);
        } catch (DataAccessException ex) {
            String msg = "Error when getting expired leases from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForBook(Book book) {
        checkSources();

        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }

        if (book.getId() == null) {
            throw new IllegalEntityException("book's id is null");
        }

        String sql = "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                "FROM LEASES WHERE BOOK_ID = ?";

        try {
            return jdbcTemplate.query(sql, leaseMapper, book.getId());
        } catch (DataAccessException ex) {
            String msg = "Error when getting leases for book " + book + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private static Date toSqlDate(java.util.Date date) {
        return date == null ? null : new Date(date.getTime());
    }

    private boolean isBookAvailable(Book book, boolean update, Long id) {
        List<Lease> list = findLeasesForBook(book);

        if (list.isEmpty()) {
            return true;
        }

        for (Lease l : list) {
            if (l.getRealEndTime() == null) {
                if (update && l.getId() == id) {
                    continue;
                }
                return false;
            }
        }

        return true;
    }

    private void validate(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }

        if (lease.getBook() == null) {
            throw new ValidationException("lease's book is null");
        }

        if (lease.getBook().getId() == null) {
            throw new ValidationException("lease's book's id is null");
        }

        if (lease.getCustomer() == null) {
            throw new ValidationException("lease's customer is null");
        }

        if (lease.getCustomer().getId() == null) {
            throw new ValidationException("lease's customer's id is null");
        }

        if (lease.getEndTime() == null) {
            throw new ValidationException("lease's end time is null");
        }
    }

    private static RowMapper<Lease> leaseMapper = (rs, rowNum) -> {
        Lease lease = new Lease();
        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setSources(dataSource);
        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setSources(dataSource);

        lease.setId(rs.getLong("id"));

        Book book = bookManager.getBookById(rs.getLong("book_id"));
        lease.setBook(book);

        Customer customer = customerManager.getCustomerById(rs.getLong("customer_id"));
        lease.setCustomer(customer);

        lease.setEndTime(new java.util.Date(rs.getDate("end_time").getTime()));

        Date real_end_time = rs.getDate("real_end_time");
        lease.setRealEndTime(real_end_time != null ?
                new java.util.Date(real_end_time.getTime()) : null);

        return lease;
    };

}
