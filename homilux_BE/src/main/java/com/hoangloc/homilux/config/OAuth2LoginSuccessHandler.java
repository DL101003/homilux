package com.hoangloc.homilux.config;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Value("${homilux.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User localUser = userRepository.findByEmail(email);

        if (localUser != null) {
            throw new ResourceAlreadyExistsException(
                    "Tài khoản",
                    "email",
                    email + " (đã đăng ký trực tiếp trong hệ thống)"
            );
        }

//        ResLoginDto res = new ResLoginDto();
//        res.setEmail(user.getEmail());
//        res.setName(user.getName());

//        String accessToken = securityUtil.createAccessToken(email, res);
//        String refreshToken = securityUtil.createRefreshToken(email, res);
//
//        ResponseCookie resCookies = ResponseCookie
//                .from("refresh_token", refreshToken)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(refreshTokenExpiration)
//                .build();
//
//        response.setHeader(HttpHeaders.SET_COOKIE, resCookies.toString());
//        response.setHeader("Access-Token", accessToken);
//        getRedirectStrategy().sendRedirect(request, response, "/oauth2/success?token=" + accessToken);
    }

}