package com.hoangloc.homilux.service;


import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.UserCreateDto;
import com.hoangloc.homilux.domain.dto.UserUpdateDto;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.RoleRepository;
import com.hoangloc.homilux.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public UserCreateDto createUser(User user) {
        if (userRepository.existsByUsernameAndDeletedFalse(user.getUsername())) {
            throw new ResourceAlreadyExistsException("Người dùng", "tên người dùng", user.getUsername());
        }
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Người dùng", "email", user.getEmail());
        }
        if (user.getRole() != null) {
            Role role = roleRepository.findByIdAndDeletedFalse(user.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", user.getRole().getId()));
            user.setRole(role);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return toCreateDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", id));
        return toDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserUpdateDto updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new IllegalArgumentException("ID người dùng không được để trống!");
        }
        User user = userRepository.findByIdAndDeletedFalse(updatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedUser.getId()));
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsernameAndDeletedFalse(updatedUser.getUsername())) {
            throw new ResourceAlreadyExistsException("Người dùng", "tên đăng nhập", updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmailAndDeletedFalse(updatedUser.getEmail())) {
            throw new ResourceAlreadyExistsException("Người dùng", "email", updatedUser.getEmail());
        }
        if (updatedUser.getUsername() != null) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getRole() != null && updatedUser.getRole().getId() != null) {
            Role role = roleRepository.findByIdAndDeletedFalse(updatedUser.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", updatedUser.getRole().getId()));
            user.setRole(role);
        }
        User savedUser = userRepository.save(user);
        return toUpdateDto(savedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", id));
        user.setDeleted(true);
        userRepository.save(user);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private UserCreateDto toCreateDto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private UserUpdateDto toUpdateDto(User user) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public User handleGetUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public void updateUserToken(String refreshToken, String username) {
        User currentUser = handleGetUserByUsername(username);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }
}