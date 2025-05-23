package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@SQLDelete(sql = "UPDATE permissions SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
@NoArgsConstructor
public class Permission extends BaseEntity {

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

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
    private List<Role> roles;

}
