package cz.muni.fi.pv168.library;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by robert on 16.4.2016.
 */
public class mainDemo {
    public static void main(String[] args) throws SQLException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                mainDemo.class.getResource("spring-config.xml").toString());

        BookManager bookManager = ctx.getBean(BookManager.class);
        CustomerManager customerManager = ctx.getBean(CustomerManager.class);
        LeaseManager leaseManager = ctx.getBean(LeaseManager.class);

        Customer customer = new Customer();
        customer.setName("Anton Maly");
        customer.setAddress("Dlha 3, 652 32 Brno");
        customer.setPhoneNumber("+420689532147");
        customerManager.createCustomer(customer);

        Book book = new Book();
        book.setAuthor("Richard Hrdy");
        book.setTitle("Skuska databaz");
        book.setPages(400);
        book.setReleaseYear(new GregorianCalendar(2014, 2, 14).getTime());
        bookManager.createBook(book);

        Lease lease = new Lease();
        lease.setBook(book);
        lease.setCustomer(customer);
        lease.setEndTime(new GregorianCalendar(2016, 17, 4).getTime());
        leaseManager.createLease(lease);

        //deleteTestData(customerManager, bookManager, leaseManager);
    }

    private static void deleteTestData(CustomerManager customerManager, BookManager bookManager, LeaseManager leaseManager) {
        List<Lease> leases = leaseManager.findAllLeases();
        leases.forEach(l -> leaseManager.deleteLease(l));

        List<Book> books = bookManager.findAllBooks();
        books.forEach(b -> bookManager.deleteBook(b));

        List<Customer> customers = customerManager.findAllCustomers();
        customers.forEach(c -> customerManager.deleteCustomer(c));
    }
}
