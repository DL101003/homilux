package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoangloc.homilux.annotation.CustomLocationValidation;
import com.hoangloc.homilux.util.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@CustomLocationValidation
@SoftDelete
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private EventType eventType; // TIEC_CUOI, DAM_HOI, TAN_GIA, DAY_THANG, SINH_NHAT, KHAC

    @Enumerated(EnumType.STRING)
    private LocationType locationType; // NHA_HANG, TUY_CHINH

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
    private PaymentStatus paymentStatus; // CHUA_THANH_TOAN, DA_THANH_TOAN, THAT_BAI

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // CHO_XAC_NHAN, DA_XAC_NHAN, HOAN_THANH, DA_HUY

    @OneToOne(mappedBy = "booking")
    @JsonIgnore
    private Payment payment;

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