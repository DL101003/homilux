package com.hoangloc.homilux.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}