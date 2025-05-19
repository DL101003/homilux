package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.util.MenuItemType;
import com.hoangloc.homilux.util.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private MenuItemType type;

    @PositiveOrZero
    private BigDecimal price;

    @ManyToMany(mappedBy = "menuItems", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    private boolean active = true;

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
