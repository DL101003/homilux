package com.hoangloc.homilux.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "review_images")
@Getter
@Setter
@SQLDelete(sql = "UPDATE review_images SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class ReviewImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String imagePath;
}