package com.hoangloc.homilux.config;

import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    public OAuth2SuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("google")) ? "google" : "facebook";
        ResLoginDto resLoginDto = authService.handleOAuth2Login(oAuth2User, provider).getBody();

        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + resLoginDto.getAccessToken() + "\"}");
    }
}