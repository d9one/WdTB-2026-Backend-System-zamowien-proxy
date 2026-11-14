package pl.wsb.systemzamowienproxy.customer.entity;

import jakarta.persistence.*;
import pl.wsb.systemzamowienproxy.account.domain.UserAccount;
import pl.wsb.systemzamowienproxy.offer.entity.DeliveryOffer;
import pl.wsb.systemzamowienproxy.order.entity.OrderRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 80)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Embedded
    private Address address;

    protected Customer() {
    }

    public Customer(String fullName, String email, LocalDate registeredOn, Boolean active, Address address) {
        this.fullName = fullName;
        this.email = email;
        this.registeredOn = registeredOn;
        this.active = active;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public Boolean getActive() { return active; }
    public Address getAddress() { return address; }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @OneToMany(mappedBy = "customer")
    private List<OrderRequest> orderRequests = new ArrayList<>();

    @OneToMany(mappedBy = "courier")
    private List<DeliveryOffer> deliveryOffers = new ArrayList<>();
}
