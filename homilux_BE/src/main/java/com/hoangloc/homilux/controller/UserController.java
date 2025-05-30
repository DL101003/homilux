package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.service.UserService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody User user) {
        var createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(spec, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users")
    public ResponseEntity<UserDto> updateUser(@RequestBody User user) {
        var updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}