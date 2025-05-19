package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String username);

    User findByRefreshTokenAndEmail(String refreshToken, String email);

    boolean existsByEmail(String email);

}