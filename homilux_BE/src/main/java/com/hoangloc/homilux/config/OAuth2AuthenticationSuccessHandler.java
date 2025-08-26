package com.hoangloc.homilux.config;

import com.hoangloc.homilux.entities.enums.AuthProvider;
import com.hoangloc.homilux.services.SecurityUtil;
import com.hoangloc.homilux.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${homilux.jwt.refresh-token-expiration-days}")
    private long refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        userService.upsertOAuth2User(email, name, AuthProvider.GOOGLE);

        UsernamePasswordAuthenticationToken authenticationGoogle =
                new UsernamePasswordAuthenticationToken(email, "");

        String accessToken = securityUtil.generateAccessToken(authenticationGoogle);
        String refreshToken = securityUtil.generateRefreshToken(authenticationGoogle);
        userService.saveRefreshToken(refreshToken, email);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, resCookies.toString());

        String target = String.format(
                "%s/oauth2/redirect#access_token=%s",
                "http://localhost:3000", accessToken
        );
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}