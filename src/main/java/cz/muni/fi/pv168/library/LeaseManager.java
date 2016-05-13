package cz.muni.fi.pv168.library;

import java.util.List;

/**
 * Created by Milan on 26.02.2016.
 */
public interface LeaseManager {
    void createLease(Lease lease);

    Lease getLeaseById(Long id);

    List<Lease> findAllLeases();

    void updateLease(Lease lease);

    void deleteLease(Lease lease);

    List<Lease> findLeasesForCustomer(Customer customer);

    List<Lease> findExpiredLeases();

    List<Lease> findLeasesForBook(Book book);

    boolean isBookAvailable(Book book);

}
