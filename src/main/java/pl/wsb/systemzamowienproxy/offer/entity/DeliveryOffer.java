package pl.wsb.systemzamowienproxy.offer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "delivery_offer")
public class DeliveryOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "estimated_delivery", nullable = false)
    private LocalDate estimatedDelivery;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

    @Column(name = "seller_location", nullable = false, length = 100)
    private String sellerLocation;

    @Column(name = "notes", length = 255)
    private String notes;

    protected DeliveryOffer() {
    }

    public DeliveryOffer(BigDecimal deliveryFee, LocalDate estimatedDelivery, Boolean accepted, String sellerLocation, String notes) {
        this.deliveryFee = deliveryFee;
        this.estimatedDelivery = estimatedDelivery;
        this.accepted = accepted;
        this.sellerLocation = sellerLocation;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
    public Boolean getAccepted() { return accepted; }
    public String getSellerLocation() { return sellerLocation; }
    public String getNotes() { return notes; }
}
