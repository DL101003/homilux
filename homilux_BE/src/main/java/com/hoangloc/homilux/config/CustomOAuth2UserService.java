package com.hoangloc.homilux.config;

import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.entities.enums.AuthProvider;
import com.hoangloc.homilux.repositories.RoleRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Tải thông tin người dùng từ Google bằng service mặc định của Spring
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Lấy các thuộc tính của người dùng từ Google
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 2. Tìm hoặc tạo người dùng mới trong DB
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setAuthProvider(AuthProvider.GOOGLE);
                    Role userRole = roleRepository.findByName("USER")
                            .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
                    newUser.setRole(userRole);
                    newUser.setPassword(null);

                    return userRepository.save(newUser);
                });

        // 3. Tạo một Principal tùy chỉnh để chứa thông tin User entity
        return new CustomOAuth2User(user, attributes);
    }
}
