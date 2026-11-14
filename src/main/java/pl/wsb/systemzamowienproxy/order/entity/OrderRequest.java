package pl.wsb.systemzamowienproxy.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "order_request")
public class OrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "origin_country", nullable = false, length = 50)
    private String originCountry;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    protected OrderRequest() {
    }

    public OrderRequest(String itemName, BigDecimal budget, OrderStatus status, String originCountry, LocalDate requestDate) {
        this.itemName = itemName;
        this.budget = budget;
        this.status = status;
        this.originCountry = originCountry;
        this.requestDate = requestDate;
    }

    public Long getId() { return id; }
    public String getItemName() { return itemName; }
    public BigDecimal getBudget() { return budget; }
    public OrderStatus getStatus() { return status; }
    public String getOriginCountry() { return originCountry; }
    public LocalDate getRequestDate() { return requestDate; }
}
