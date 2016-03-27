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

/**
 * Created by Milan on 26.02.2016.
 */
public class LeaseManagerImpl implements LeaseManager {

    private static final Logger logger = Logger.getLogger(
            LeaseManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not set");
        }
    }

    public void createLease(Lease lease) {
        checkDataSource();
        validate(lease);

        if (lease.getId() != null) {
            throw new IllegalArgumentException("lease id is already set");
        }

        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);
        if (customerManager.getCustomerById(lease.getCustomer().getId()) == null) {
            throw new IllegalEntityException("Customer is not in db");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO LEASES(CUSTOMER_ID, BOOK_ID, ENDTIME, REAL_END_TIME) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, lease.getCustomer().getId());
            statement.setLong(2, lease.getBook().getId());
            statement.setDate(3, toSqlDate(lease.getEndTime()));
            statement.setDate(4, toSqlDate(lease.getRealEndTime()));

            int count = statement.executeUpdate();
            DBUtils.checkUpdatesCount(count, lease, true);

            Long id = DBUtils.getId(statement.getGeneratedKeys());
            lease.setId(id);
        } catch (SQLException e) {
            String msg = "Error when inserting lease into db";
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceFailureException(msg, e);
        }
    }

    public Lease getLeaseById(Long id) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME FROM LEASES WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Lease lease = resultSetToLease(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + findAllLeases()
                                    + " and " + resultSetToLease(rs));
                }

                return lease;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            String msg = "Error when getting lease with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findAllLeases() {
        checkDataSource();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME FROM LEASES")) {
            ResultSet rs = statement.executeQuery();
            return resultSetToList(rs);
        } catch (SQLException ex) {
            String msg = "Error when getting all leases from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void updateLease(Lease lease) {
        checkDataSource();
        validate(lease);

        if (lease.getId() == null) {
            throw new IllegalEntityException("lease id is null");
        }

        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        if (bookManager.getBookById(lease.getBook().getId()) == null) {
            throw new IllegalEntityException("Book is not in db");
        }

        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);
        if (customerManager.getCustomerById(lease.getCustomer().getId()) == null) {
            throw new IllegalEntityException("Customer is not in db");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "UPDATE LEASES SET BOOK_ID = ?, CUSTOMER_ID = ?, END_TIME = ?, REAL_END_TIME = ?" +
                                " WHERE ID = ?")) {

            statement.setLong(1, lease.getBook().getId());
            statement.setLong(2, lease.getCustomer().getId());
            statement.setDate(3, toSqlDate(lease.getEndTime()));
            statement.setDate(4, toSqlDate(lease.getRealEndTime()));
            statement.setLong(5, lease.getId());

            int count = statement.executeUpdate();
            DBUtils.checkUpdatesCount(count, lease, false);
        } catch (SQLException ex) {
            String msg = "Error when updating lease in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public void deleteLease(Lease lease) {
        checkDataSource();

        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }

        if (lease.getId() == null) {
            throw new IllegalEntityException("lease id is null");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "DELETE FROM LEASES WHERE ID = ?")) {

            statement.setLong(1, lease.getId());

            int count = statement.executeUpdate();
            DBUtils.checkUpdatesCount(count, lease, false);
        } catch (SQLException ex) {
            String msg = "Error when deleting lease from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        checkDataSource();

        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        if (customer.getId() == null) {
            throw new IllegalEntityException("customer id is null");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                                "FROM LEASES WHERE CUSTOMER_ID = ?")) {
            statement.setLong(1, customer.getId());
            ResultSet rs = statement.executeQuery();
            return resultSetToList(rs);
        } catch (SQLException ex) {
            String msg = "Error when getting leases for customer " + customer + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findExpiredLeases() {
        checkDataSource();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                                "FROM LEASES WHERE REAL_END_TIME > END_TIME")) {
            ResultSet rs = statement.executeQuery();
            return resultSetToList(rs);
        } catch (SQLException ex) {
            String msg = "Error when getting expired leases from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    public List<Lease> findLeasesForBook(Book book) {
        checkDataSource();

        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }

        if (book.getId() == null) {
            throw new IllegalEntityException("book's id is null");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT ID, BOOK_ID, CUSTOMER_ID, END_TIME, REAL_END_TIME " +
                                "FROM LEASES WHERE BOOK_ID = ?")) {
            statement.setLong(1, book.getId());
            ResultSet rs = statement.executeQuery();
            return resultSetToList(rs);
        } catch (SQLException ex) {
            String msg = "Error when getting leases for book " + book + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }
    }

    private static Date toSqlDate(java.util.Date date) {
        return date == null ? null : new Date(date.getTime());
    }

    private List<Lease> resultSetToList(ResultSet rs) throws SQLException {
        List<Lease> list = new ArrayList<>();

        while (rs.next()) {
            list.add(resultSetToLease(rs));
        }

        return list;
    }

    private Lease resultSetToLease(ResultSet rs) throws SQLException {
        Lease lease = new Lease();
        BookManager bookManager = new BookManagerImpl();
        CustomerManager customerManager = new CustomerManagerImpl();

        lease.setId(rs.getLong("id"));

        Book book = bookManager.getBookById(rs.getLong("book_id"));
        lease.setBook(book);

        Customer customer = customerManager.getCustomerById(rs.getLong("customer_id"));
        lease.setCustomer(customer);

        lease.setEndTime(new java.util.Date(rs.getDate("end_time").getTime()));
        lease.setRealEndTime(new java.util.Date(rs.getDate("real_end_time").getTime()));

        return lease;
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
}
