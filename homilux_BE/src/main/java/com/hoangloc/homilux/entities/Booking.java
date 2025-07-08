package com.hoangloc.homilux.entities;

import com.hoangloc.homilux.entities.enums.BookingStatus;
import com.hoangloc.homilux.entities.enums.LocationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE bookings SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(name = "event_datetime")
    private Instant eventDatetime;

    @Column(name = "expected_guests")
    private Integer expectedGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type")
    private LocationType locationType; // RESTAURANT, EXTERNAL

    @Column(length = 500)
    private String address;

    @Column(name = "contract_signing_date")
    private LocalDate contractSigningDate;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Lob
    @Column(name = "customer_notes")
    private String customerNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private Set<BookingRentalService> bookedRentalServices;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private Set<Payment> payments;

    @OneToOne(mappedBy = "booking")
    private Review review;
}