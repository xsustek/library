package cz.muni.fi.pv168.library;

import javax.activation.DataSource;
import java.util.List;

/**
 * Created by Milan on 26.02.2016.
 */
public class LeaseManagerImpl implements LeaseManager {

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
        return null;
    }

    public List<Lease> findAllLeases() {
        return null;
    }

    public void updateLease(Lease lease) {

    }

    public void deleteLease(Lease lease) {

    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        return null;
    }

    public List<Lease> findExpiredLeases() {
        return null;
    }

    public List<Lease> findLeasesForBook(Book book) {
        return null;
    }

    private void validate(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }

        if (lease.getBook() == null) {

        }

        if (lease.getCustomer() == null) {

        }

        if (lease.getEndTime() == null) {

        }
    }
}
