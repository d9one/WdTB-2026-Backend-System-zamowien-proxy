package pl.wsb.systemzamowienproxy.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "street", length = 80)
    private String street;

    @Column(name = "city", length = 40)
    private String city;

    @Column(name = "country", length = 40)
    private String country;

    protected Address() {
    }

    public Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
}
