package com.hoangloc.homilux.config;

import com.hoangloc.homilux.services.SecurityUtil;
import com.hoangloc.homilux.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
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
        // 1. Lấy ra đối tượng Authentication, nó chứa đủ thông tin cần thiết
        if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 2. Tạo Refresh Token
//        String accessToken = securityUtil.generateAccessToken(authentication);
        String refreshToken = securityUtil.generateRefreshToken(authentication);

        userService.saveRefreshToken(refreshToken, authentication.getName());

        // 3. Xóa các thuộc tính không cần thiết để dọn dẹp session
        clearAuthenticationAttributes(request);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, resCookies.toString());

        // 4. Xây dựng URL chuyển hướng với access token
        String targetUrl = "http://localhost:3000/oauth2/redirect";

        // 5. Thực hiện chuyển hướng
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}