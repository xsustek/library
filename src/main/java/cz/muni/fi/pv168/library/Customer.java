package cz.muni.fi.pv168.library;

import java.util.Objects;

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
        this.phoneNumber = phoneNumber;
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
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    // pattern "street streetNumber, zipCode(xxx xx) city"
    public void setAddress(String address) {
        this.address = address;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (this.id == null && this != o) {
            return false;
        }

        final Customer customer = (Customer) o;
        return Objects.equals(this.id, customer.id);


    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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

}
