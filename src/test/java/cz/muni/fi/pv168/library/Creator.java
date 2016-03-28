package cz.muni.fi.pv168.library;

import java.util.Date;

/**
 * Created by robert on 27.3.2016.
 */
public class Creator {
    protected static Lease newLease(Book book, Customer customer, Date endTime, Date realEndTime) {
        Lease lease = new Lease();
        lease.setBook(book);
        lease.setCustomer(customer);
        lease.setEndTime(endTime);
        lease.setRealEndTime(realEndTime);

        return lease;
    }

    protected static Customer newCustomer(String name, String address, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);

        return customer;
    }

    protected static Book newBook(String title, int pages, Date releaseYear, String author) {
        Book b = new Book();
        b.setTitle(title);
        b.setPages(pages);
        b.setReleaseYear(releaseYear);
        b.setAuthor(author);

        return b;
    }
}
