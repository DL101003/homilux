package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@SQLDelete(sql = "UPDATE dishes SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Dish extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String category;

    @ManyToMany(mappedBy = "dishes", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Menu> menus;

}