package com.hoangloc.homilux.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_booking", columnList = "booking_id"),
        @Index(name = "idx_review_user", columnList = "user_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE reviews SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte rating;

    @Lob
    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ReviewImage> images;
}