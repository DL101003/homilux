package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.util.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "service_items")
@Getter
@Setter
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Double price;

    @ManyToMany(mappedBy = "serviceItems", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ServicePackage> servicePackages;

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
