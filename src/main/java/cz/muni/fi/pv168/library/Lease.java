package cz.muni.fi.pv168.library;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Milan on 26.02.2016.
 */
public class Lease {

    private Long id;
    private LocalDate endTime;
    private Customer customer;
    private Book book;
    private LocalDate realEndTime;

    public Lease() {
    }

    public LocalDate getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(LocalDate realEndTime) {
        this.realEndTime = realEndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "Lease{"
                + "id=" + id
                + ", customer=" + customer
                + ", book=" + book
                + ", endTime=" + endTime
                + ", realEndTime=" + realEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this != o && this.id == null) {
            return false;
        }

        final Lease lease = (Lease) o;

        return Objects.equals(this.id, lease.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
