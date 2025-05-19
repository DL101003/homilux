package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.util.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Api path cannot be blank")
    private String apiPath;
    @NotBlank(message = "Method cannot be blank")
    private String method;
    @NotBlank(message = "Module cannot be blank")
    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Role> roles;

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
