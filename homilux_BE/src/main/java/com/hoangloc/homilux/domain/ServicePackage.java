package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoangloc.homilux.util.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "service_packages")
@Getter
@Setter
public class ServicePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "service_package_items",
            joinColumns = @JoinColumn(name = "service_package_id"),
            inverseJoinColumns = @JoinColumn(name = "service_item_id")
    )
    @JsonIgnoreProperties("servicePackages")
    private List<ServiceItem> serviceItems;

    private Double price;

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
