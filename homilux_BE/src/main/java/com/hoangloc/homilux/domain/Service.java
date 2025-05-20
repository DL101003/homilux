package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "services")
@Getter
@Setter
@SQLDelete(sql = "UPDATE services SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Service extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    @JsonBackReference
    private EventType eventType;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Event> events;

}