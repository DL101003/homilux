package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.util.MenuItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String description;

    @Enumerated(EnumType.STRING)
    private MenuItemType type;

    private Double price;

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
    public void prePersist() {}

    @PreUpdate
    public void preUpdate() {}
}
