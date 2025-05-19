package com.hoangloc.homilux.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoangloc.homilux.util.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    private String customLocationAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_menu_items",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    @JsonIgnoreProperties("bookings")
    private List<MenuItem> menuItems;

    private Instant eventDate;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @PositiveOrZero
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToOne(mappedBy = "booking")
    @JsonIgnore
    private Payment payment;

    private boolean deleted = false;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.updatedAt = Instant.now();
    }
}