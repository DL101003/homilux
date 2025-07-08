package com.hoangloc.homilux.entities;

import com.hoangloc.homilux.entities.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = HttpMethod.valueOf(method);
        this.module = module;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "api_path")
    private String apiPath;

    @Enumerated(EnumType.STRING)
    private HttpMethod method; // GET, POST, PUT, DELETE, PATCH

    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private List<Role> roles;
}