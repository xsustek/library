package cz.muni.fi.pv168.book_rental;

import java.util.regex.Pattern;

/**
 * Created by Robert Duriancik on 26.02.2016.
 */
public class Customer {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;

    public Customer() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // format "+xxxxxxxxxxxx"
    public void setPhoneNumber(String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new IllegalArgumentException("Invalid phone number");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Invalid name");
        }
    }

    public String getAddress() {
        return address;
    }

    // pattern "street streetNumber, zipCode(xxx xx) city"
    public void setAddress(String address) {
        if (isValidAddress(address)) {
            this.address = address;
        } else {
            throw new IllegalArgumentException("Invalid address");
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z\\u00c0-\\u017e]", name);
    }

    private boolean isValidPhoneNumber(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }

        return Pattern.matches("[+]\\d{12}+", number);
    }

    private boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z0-9\\u00c0-\\u017e -]+[,][ ]" +
                "[0-9]{3}+[ ][0-9]{2}+[ ][a-zA-Z0-9\\u00c0-\\u017e., -]+", address);
    }

}
