package cz.muni.fi.pv168.common;

import cz.muni.fi.pv168.library.Book;
import cz.muni.fi.pv168.library.Customer;
import cz.muni.fi.pv168.library.Lease;

import java.util.regex.Pattern;

/**
 * Created by robert on 13.5.2016.
 */
public class Validator {

    private Validator() {
    }

    public static void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book is null");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("Author is null");
        }

        if (book.getAuthor().isEmpty()) {
            throw new ValidationException("Author is empty");
        }

        if (!book.getAuthor().matches("^[A-Z][a-z]*( ?[A-Z][a-z]*)* [A-Z][a-z]*$")) {
            throw new ValidationException("Invalid author format");
        }

        if (book.getPages() < 1) {
            throw new ValidationException("Pages is less then 1");
        }

        if (book.getTitle() == null) {
            throw new IllegalArgumentException("Title is null");
        }

        if (book.getTitle().isEmpty()) {
            throw new ValidationException("Title is empty");
        }
        if (book.getReleaseYear() < 0) {
            throw new ValidationException("Date is less than zero");
        }
    }

    public static void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }

        if (!isValidCustomerName(customer.getName())) {
            throw new ValidationException("Invalid customer's name");
        }

        if (!isValidCustomerAddress(customer.getAddress())) {
            throw new ValidationException("Invalid customer's address. Valid address " +
                    "format \"streetName buildingNumber, postalCode city\'");
        }

        if (!isValidCustomerPhoneNumber(customer.getPhoneNumber())) {
            throw new ValidationException("Invalid customer's phone number. Valid phone" +
                    " number format \"+xxxxxxxxxxxx\"");
        }
    }

    public static void validateLease(Lease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("Lease is null");
        }

        if (lease.getBook() == null) {
            throw new ValidationException("Lease's book is null");
        }

        if (lease.getBook().getId() == null) {
            throw new ValidationException("Lease's book's id is null");
        }

        if (lease.getCustomer() == null) {
            throw new ValidationException("Lease's customer is null");
        }

        if (lease.getCustomer().getId() == null) {
            throw new ValidationException("Lease's customer's id is null");
        }

        if (lease.getEndTime() == null) {
            throw new ValidationException("Lease's end time is null");
        }
    }

    public static boolean isValidCustomerName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z\\u00c0-\\u017e ]+", name);
    }

    public static boolean isValidCustomerPhoneNumber(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }

        return Pattern.matches("[+]\\d{12}+", number);
    }

    public static boolean isValidCustomerAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z0-9\\u00c0-\\u017e -]+[,][ ]" +
                "[0-9]{3}+[ ][0-9]{2}+[ ][a-zA-Z0-9\\u00c0-\\u017e., -]+", address);
    }
}
