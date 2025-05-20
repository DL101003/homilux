package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@SQLDelete(sql = "UPDATE reviews SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    @JsonBackReference
    private Dish dish;

    @Column(columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private int rating;

    private String comment;

}