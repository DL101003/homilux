package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "event_types")
@Getter
@Setter
@SQLDelete(sql = "UPDATE event_types SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class EventType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "eventType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Event> events;

    @OneToMany(mappedBy = "eventType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Service> services;

}