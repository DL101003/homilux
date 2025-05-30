package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ReqLoginDto;
import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDto> login(@Valid @RequestBody ReqLoginDto reqLoginDto) {
        return authService.login(reqLoginDto);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ResLoginDto> getAccount() {
        return authService.getAccount();
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDto> getRefreshToken(@CookieValue("refresh_token") String refreshToken) throws Exception {
        return authService.getRefreshToken(refreshToken);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws Exception {
        return authService.logout();
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody User postmanUser) {
        return authService.register(postmanUser);
    }
}
