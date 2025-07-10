package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.authDto.LoginRequest;
import com.hoangloc.homilux.dtos.authDto.LoginResponse;
import com.hoangloc.homilux.dtos.authDto.RefreshTokenResponse;
import com.hoangloc.homilux.dtos.authDto.RegisterRequest;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;

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
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = securityUtil.generateAccessToken(authentication);
        String refreshToken = securityUtil.generateRefreshToken(authentication);

        userService.saveRefreshToken(refreshToken, request.email());

        User currentUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                currentUser.getEmail(),
                currentUser.getFullName(),
                currentUser.getRole().getId(),
                currentUser.getRole().getName()
        );

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(response);
    }

    public ResponseEntity<RefreshTokenResponse> refreshToken(String refreshToken) {
        Jwt decodedJwt = jwtDecoder.decode(refreshToken);
        String username = decodedJwt.getSubject();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, user.getRole().getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList())
        );

        String newAccessToken = securityUtil.generateAccessToken(authentication);
        String newRefreshToken = securityUtil.generateRefreshToken(authentication);

        userService.saveRefreshToken(newRefreshToken, username);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(new RefreshTokenResponse(newAccessToken));
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

}