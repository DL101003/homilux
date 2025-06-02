package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ReqLoginDto;
import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.domain.dto.UserDto;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${homilux.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthService(AuthenticationManagerBuilder authenticationManagerBuilder,
                       SecurityUtil securityUtil,
                       UserService userService,
                       UserRepository userRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResLoginDto> login(ReqLoginDto reqLoginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(reqLoginDto.getUsername(), reqLoginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDto res = new ResLoginDto();
        User currentUserDB = userRepository.findByEmail(reqLoginDto.getUsername());
        if (currentUserDB != null) {
            res.setId(currentUserDB.getId());
            res.setEmail(currentUserDB.getEmail());
            res.setName(currentUserDB.getName());
        }

        String accessToken = securityUtil.createAccessToken(reqLoginDto.getUsername(), res);
        res.setAccessToken(accessToken);

        String refreshToken = securityUtil.createRefreshToken(reqLoginDto.getUsername(), res);
        userService.updateUserToken(refreshToken, reqLoginDto.getUsername());

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

    public ResponseEntity<ResLoginDto> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        User currentUserDB = userRepository.findByEmail(email);
        ResLoginDto userLogin = new ResLoginDto();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
        }
        return ResponseEntity.ok(userLogin);
    }

    public ResponseEntity<ResLoginDto> getRefreshToken(String refreshToken) throws Exception {
        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User currentUser = userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new Exception("Refresh Token không hợp lệ");
        }
        ResLoginDto res = new ResLoginDto();
        User currentUserDB = userRepository.findByEmail(email);
        if (currentUserDB != null) {
            res.setId(currentUserDB.getId());
            res.setEmail(currentUserDB.getEmail());
            res.setName(currentUserDB.getName());
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

    public ResponseEntity<UserDto> register(User postmanUser) {
        UserDto user = userService.createUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}