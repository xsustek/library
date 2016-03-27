package cz.muni.fi.pv168.library;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
    }

    public Lease getLeaseById(Long id) {
        checkDataSource();
        return null;
    }

    public List<Lease> findAllLeases() {
        return null;
    }

    public void updateLease(Lease lease) {
        checkDataSource();
        validate(lease);

    }

    public void deleteLease(Lease lease) {
        checkDataSource();
    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        checkDataSource();
        return null;
    }

    public List<Lease> findExpiredLeases() {
        checkDataSource();
        return null;
    }

    public List<Lease> findLeasesForBook(Book book) {
        return null;
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

        lease.setEndTime(rs.getDate("end_time"));
        lease.setRealEndTime(rs.getDate("real_end_time"));

        return lease;
    }

    private void validate(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }

        if (lease.getBook() == null) {

        }

        if (lease.getBook().getId() == null) {

        }

        if (lease.getCustomer() == null) {

        }

        if (lease.getCustomer().getId() == null)

        if (lease.getEndTime() == null) {

        }
    }
}
