package com.hoangloc.homilux.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private int rating;

    private String comment;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private List<ReviewImage> images;

}