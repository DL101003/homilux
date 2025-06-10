package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.RoleRepository;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.util.AuthProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractPaginationService<User, UserDto> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public UserDto createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Người dùng", "email", user.getEmail());
        }
        if (user.getRole() != null) {
            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", user.getRole().getId()));
            user.setRole(role);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    public UserDto saveOrUpdateOAuth2User(OAuth2User oAuth2User, String provider) {
        String providerId = provider.equals("google") ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        AuthProvider authProvider = provider.equals("google") ? AuthProvider.GOOGLE : AuthProvider.FACEBOOK;

        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setProviderId(providerId);
            user.setAuthProvider(authProvider);
            user.setUpdatedBy(provider.toUpperCase());
        } else {
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(null);
            user.setPassword(null);
            user.setProviderId(providerId);
            user.setAuthProvider(authProvider);
            user.setCreatedBy(provider.toUpperCase());
            user.setUpdatedBy(provider.toUpperCase());
        }

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", id));
        return toDto(user);
    }

    public UserDto updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new IllegalArgumentException("ID người dùng không được để trống!");
        }
        User user = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedUser.getId()));

        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());

        if (updatedUser.getRole() != null) {
            Role role = roleRepository.findById(updatedUser.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", updatedUser.getRole().getId()));
            user.setRole(role);
        }
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", id));
        userRepository.deleteById(id);
    }

    @Override
    protected UserDto toDto(User user) {
        UserDto dto = new UserDto();
        UserDto.RoleUser role = new UserDto.RoleUser();

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            dto.setRole(role);
        }

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public void updateUserToken(String refreshToken, String username) {
        User currentUser = userRepository.findByEmail(username);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

}