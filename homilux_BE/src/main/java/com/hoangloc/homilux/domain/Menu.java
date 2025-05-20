package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "menus")
@Getter
@Setter
@SQLDelete(sql = "UPDATE menus SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuName;
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_dishes",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    @JsonManagedReference
    private List<Dish> dishes;

    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Event> events;

}