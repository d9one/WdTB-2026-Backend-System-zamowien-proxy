package pl.wsb.systemzamowienproxy.tag.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    protected Tag() {}

    public Tag(String name, String description, Boolean active, LocalDate createdOn) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }
}
