package com.hoangloc.homilux.entities;

import com.hoangloc.homilux.entities.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String email;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(columnDefinition = "VARCHAR(500)")
    private String refreshToken;

    private String phoneNumber;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Review> reviews;
}