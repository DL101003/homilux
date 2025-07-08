package com.hoangloc.homilux.config;

import com.hoangloc.homilux.services.SecurityUtil;
import com.hoangloc.homilux.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecurityUtil securityUtil; // Giả sử bạn có class này để tạo token
    private final UserService userService;

//    @Value("${app.oauth2.redirectUri}") // Thêm vào application.yml: app.oauth2.redirectUri=http://localhost:3000/oauth2/redirect
//    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. Lấy ra đối tượng Authentication, nó chứa đủ thông tin cần thiết
        if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 2. Tạo cặp Access Token và Refresh Token
        String accessToken = securityUtil.generateAccessToken(authentication);
        String refreshToken = securityUtil.generateRefreshToken(authentication);

        userService.saveRefreshToken(refreshToken, authentication.getName());

        // 3. Xóa các thuộc tính không cần thiết để dọn dẹp session
        clearAuthenticationAttributes(request);

        // 4. Xây dựng URL chuyển hướng với CẢ HAI token
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        // 5. Thực hiện chuyển hướng
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}