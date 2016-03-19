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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (name != null ? !name.equals(customer.name) : customer.name != null) return false;
        if (address != null ? !address.equals(customer.address) : customer.address != null) return false;
        return phoneNumber != null ? phoneNumber.equals(customer.phoneNumber) : customer.phoneNumber == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + "\'" +
                ", address='" + address + "\'" +
                ", phone number='" + phoneNumber + "\'" +
                "}";
    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[a-zA-Z\\u00c0-\\u017e]+", name);
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
