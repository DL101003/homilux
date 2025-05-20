package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hoangloc.homilux.util.LocationType;
import com.hoangloc.homilux.util.PaymentMethod;
import com.hoangloc.homilux.util.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@SQLDelete(sql = "UPDATE events SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    @JsonBackReference
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    private String customLocation;
    private int estimatedAttendees;
    private Instant eventDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDate contractDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_menus",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    @JsonManagedReference
    private List<Menu> menus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_services",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @JsonManagedReference
    private List<Service> services;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Payment> payments;

}