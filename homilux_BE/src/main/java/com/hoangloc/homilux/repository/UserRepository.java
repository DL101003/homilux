package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedFalse(Long id);

    List<User> findAllByDeletedFalse();

    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByUsernameAndDeletedFalse(String username);

    boolean existsByEmailAndDeletedFalse(String email);

    User findByEmail(String username);

    User findByRefreshTokenAndEmail(String refreshToken, String email);
}