package com.hoangloc.homilux.config;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ResLoginDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.service.UserService;
import com.hoangloc.homilux.util.AuthProvider;
import com.hoangloc.homilux.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Transactional
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${homilux.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, SecurityUtil securityUtil, @Lazy UserService userService, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String provider = determineProvider(authentication);
        String password = provider.equals("GOOGLE") ? oauth2User.getAttribute("sub") : oauth2User.getAttribute("id");
        password = passwordEncoder.encode(password);

        User user = userRepository.findByEmail(email);
        if (user != null) {
            // Kiểm tra provider của user
            if (user.getProvider() == null) {
                // Trường hợp user đã đăng ký bằng email/password thông thường
                throw new ResourceAlreadyExistsException(
                        "Tài khoản",
                        "email",
                        email + " (đã đăng ký bằng email/password)"
                );
            } else {
                // Trường hợp user đã đăng ký với provider khác
                throw new ResourceAlreadyExistsException(
                        "Tài khoản",
                        "email",
                        email + " (đã đăng ký với " + user.getProvider() + ")"
                );
            }
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            user.setCreatedBy(provider);
            user.setProvider(AuthProvider.valueOf(provider));
            user = userRepository.save(user);
        }

        // Tạo response data
        ResLoginDto res = new ResLoginDto();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());

        // Tạo tokens
        String accessToken = securityUtil.createAccessToken(email, res);
        String refreshToken = securityUtil.createRefreshToken(email, res);

        // Cập nhật refresh token trong database
        userService.updateUserToken(refreshToken, email);

        // Tạo refresh token cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        // Thêm tokens vào response
        response.setHeader(HttpHeaders.SET_COOKIE, resCookies.toString());
        response.setHeader("Access-Token", accessToken);
        // Redirect về trang chủ hoặc trang success của bạn
//        getRedirectStrategy().sendRedirect(request, response, "/oauth2/success?token=" + accessToken);
    }

    private String determineProvider(Authentication authentication) {
        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        return clientRegistrationId.toUpperCase();
    }
}