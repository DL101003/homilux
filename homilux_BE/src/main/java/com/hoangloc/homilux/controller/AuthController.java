package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ReqLoginDto;
import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.service.UserService;
import com.hoangloc.homilux.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;

    private final UserService userService;

    @Value("${homilux.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDto> login(@Valid @RequestBody ReqLoginDto reqLoginDTO) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDto res = new ResLoginDto();
        User currentUserDB = userService.handleGetUserByUsername(reqLoginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDto.UserLogin userlogin = new ResLoginDto.UserLogin(currentUserDB.getId(), currentUserDB.getName(), currentUserDB.getEmail(), currentUserDB.getRole());
            res.setUser(userlogin);
        }

        String accessToken = securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(accessToken);

        String refreshToken = securityUtil.createRefreshToken(reqLoginDTO.getUsername(), res);
        userService.updateUserToken(refreshToken, reqLoginDTO.getUsername());

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ResLoginDto.UsetGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        User currentUserDB = userService.handleGetUserByUsername(email);
        ResLoginDto.UserLogin userlogin = new ResLoginDto.UserLogin();
        ResLoginDto.UsetGetAccount usetGetAccount = new ResLoginDto.UsetGetAccount();

        if (currentUserDB != null) {
            userlogin.setId(currentUserDB.getId());
            userlogin.setEmail(currentUserDB.getEmail());
            userlogin.setName(currentUserDB.getName());
            userlogin.setRole(currentUserDB.getRole());
            usetGetAccount.setUser(userlogin);
        }
        return ResponseEntity.ok(usetGetAccount);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResLoginDto> getRefreshToken(@CookieValue("refresh_token") String refreshToken) throws Exception {
        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User currentUser = userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new Exception("Refresh Token không hợp lệ");
        }
        ResLoginDto res = new ResLoginDto();
        User currentUserDB = userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDto.UserLogin userlogin = new ResLoginDto.UserLogin(currentUserDB.getId(), currentUserDB.getName(), currentUserDB.getEmail(), currentUserDB.getRole());
            res.setUser(userlogin);
        }

        String accessToken = securityUtil.createAccessToken(email, res);
        res.setAccessToken(accessToken);

        String newRefreshToken = securityUtil.createRefreshToken(email, res);
        userService.updateUserToken(newRefreshToken, email);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws Exception {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email == null) {
            throw new Exception("Access Token không hợp lệ");
        }
        userService.updateUserToken(null, email);

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

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody User postmanUser) {
        UserDto user = userService.createUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
