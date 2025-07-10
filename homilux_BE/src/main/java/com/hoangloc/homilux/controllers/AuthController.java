package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.authDto.LoginRequest;
import com.hoangloc.homilux.dtos.authDto.LoginResponse;
import com.hoangloc.homilux.dtos.authDto.RefreshTokenResponse;
import com.hoangloc.homilux.dtos.authDto.RegisterRequest;
import com.hoangloc.homilux.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@CookieValue(name = "refresh_token") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return authService.logout();
    }
}