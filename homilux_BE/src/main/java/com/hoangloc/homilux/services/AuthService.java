package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.authDto.*;
import com.hoangloc.homilux.dtos.permissionDto.PermissionResponse;
import com.hoangloc.homilux.dtos.roleDto.RoleResponse;
import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.RoleRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;

    @Value("${homilux.jwt.refresh-token-expiration-days}")
    private long refreshTokenExpiration;

    public ResponseEntity<Void> logout() {

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = securityUtil.generateAccessToken(authentication);
        String refreshToken = securityUtil.generateRefreshToken(authentication);

        userService.saveRefreshToken(refreshToken, request.email());

        User currentUser = userRepository.findByEmail(request.email()).get();

        List<PermissionResponse> permissionResponses = currentUser.getRole().getPermissions().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName(), p.getApiPath(), p.getMethod().toString(), p.getModule()))
                .toList();
        RoleResponse roleResponse = new RoleResponse(currentUser.getRole().getId(), currentUser.getRole().getName(), permissionResponses);
        LoginResponse loginResponse = new LoginResponse(accessToken, currentUser.getId(), currentUser.getEmail(), currentUser.getFullName(), roleResponse);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(loginResponse);
    }

    public ResponseEntity<RefreshTokenResponse> refreshToken(String refreshToken) {
        Jwt decodedJwt = jwtDecoder.decode(refreshToken);
        String username = decodedJwt.getSubject();

        userRepository.findByRefreshTokenAndEmail(refreshToken, username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "refresh token and email"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String newAccessToken = securityUtil.generateAccessToken(authentication);

        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));

        User newUser = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(userRole)
                .build();
        userRepository.save(newUser);
    }

    public ResponseEntity<FetchAccount> getAccount() {
        String email = SecurityUtil.getCurrentUser();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        List<PermissionResponse> permissionResponses = user.getRole().getPermissions().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName(), p.getApiPath(), p.getMethod().toString(), p.getModule()))
                .toList();
        RoleResponse roleResponse = new RoleResponse(user.getRole().getId(), user.getRole().getName(), permissionResponses);
        return ResponseEntity.ok(new FetchAccount(user.getId(), user.getEmail(), user.getFullName(), roleResponse));
    }
}