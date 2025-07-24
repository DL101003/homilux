package com.hoangloc.homilux.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.entities.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "rental_services")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal price; // GIÁ CỦA DỊCH VỤ

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private ServiceType type; // PACKAGE, EQUIPMENT, PERSONNEL, OTHER

    @Column(name = "is_customizable")
    private boolean isCustomizable;

    @OneToMany(mappedBy = "rentalService")
    @JsonIgnore
    private Set<BookingRentalService> bookingRentalServices;
}