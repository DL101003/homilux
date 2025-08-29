package com.hoangloc.homilux.services;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.dtos.authDto.RegisterRequest;
import com.hoangloc.homilux.dtos.userDto.ChangePasswordRequest;
import com.hoangloc.homilux.dtos.userDto.UserResponse;
import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.entities.enums.AuthProvider;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.InvalidRequestException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.RoleRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService extends AbstractPaginationService<User, UserResponse> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User with email '" + request.email() + "' already exists.");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.roleId()));

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password())) // Mã hóa mật khẩu
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Created new user with id: {}", savedUser.getId());
        return toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.roleId()));

        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(role);

        User updatedUser = userRepository.save(user);
        log.info("Updated user with id: {}", updatedUser.getId());
        return toResponse(updatedUser);
    }

    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        // Cần kiểm tra ràng buộc: không cho xóa user nếu họ có booking...
        // Tạm thời bỏ qua để đơn giản, nhưng trong dự án thực tế phải kiểm tra.
        userRepository.delete(user);
        log.info("Deleted user with id: {}", id);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toResponse(user);
    }

    @Transactional
    public void saveRefreshToken(String refreshToken, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.info("Saved refresh token for user: {}", email);
    }

    @Override
    protected UserResponse toResponse(User user) {
        return new UserResponse(
        user.getId(),
        user.getFullName(),
        user.getEmail(),
        user.getAuthProvider(),
        user.getPhoneNumber(),
        user.getRole() != null ? user.getRole().getId() : null,
        user.getRole() != null ? user.getRole().getName() : null
);
    }

    @Transactional
    public void changeCurrentUserPassword(ChangePasswordRequest request) {
        // Lấy user đang đăng nhập từ security context
        String currentUsername = SecurityUtil.getCurrentUser();

        User currentUser = userRepository.findByEmail(currentUsername) // hoặc findByUsername
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())) {
            throw new InvalidRequestException("Incorrect old password.");
        }

        // Mã hóa và cập nhật mật khẩu mới
        currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(currentUser);
    }

    public UserResponse getUserLogin(String email) {
        User currentUserLogin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return toResponse(currentUserLogin);
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return user.getId();
    }

    @Transactional
    public void upsertOAuth2User(String email, String fullName, AuthProvider provider) {

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setFullName(fullName);
                    u.setAuthProvider(provider);
                    u.setPassword(null);
                    return u;
                });

        // Nếu đã tồn tại → hoà giải dữ liệu
        if (user.getId() != null) {
            if (fullName != null && !fullName.isBlank() && !fullName.equals(user.getFullName())) {
                user.setFullName(fullName);
            }
            if (user.getAuthProvider() == null) {
                user.setAuthProvider(provider);
            }
        }

        userRepository.save(user);
    }

}