package cz.muni.fi.pv168.book_rental;

/**
 * Created by Milan on 26.02.2016.
 */
public class Customer {
    private Long id;
    private String name;
    private String address;
    private String number;

    public Customer() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public void setAddress(String address) {
        this.address = address;
    }

}
