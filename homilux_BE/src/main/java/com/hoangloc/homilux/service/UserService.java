package com.hoangloc.homilux.service;


import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(User user) {
        if (userRepository.existsByUsernameAndDeletedFalse(user.getUsername())) {
            throw new ResourceAlreadyExistsException("Người dùng", "tên người dùng", user.getUsername());
        }
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Người dùng", "email", user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleted(false);
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
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

    public UserDto updateUser(User updatedUser) {
        User user = userRepository.findByIdAndDeletedFalse(updatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedUser.getId()));
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedFalse(updatedUser.getUsername())) {
                throw new ResourceAlreadyExistsException("Người dùng", "tên người dùng", updatedUser.getUsername());
            }
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndDeletedFalse(updatedUser.getEmail())) {
                throw new ResourceAlreadyExistsException("Người dùng", "email", updatedUser.getEmail());
            }
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
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
}