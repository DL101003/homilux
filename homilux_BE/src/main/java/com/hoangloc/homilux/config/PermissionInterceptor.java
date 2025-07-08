package com.hoangloc.homilux.config;

import com.hoangloc.homilux.entities.Permission;
import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.exceptions.PermissionException;
import com.hoangloc.homilux.repositories.UserRepository;
import com.hoangloc.homilux.services.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler) {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String email = SecurityUtil.getCurrentUser();

        if (email != null && !email.isEmpty()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new PermissionException("User not found"));

            Role role = user.getRole();
            if (role == null) {
                throw new PermissionException("User role not found");
            }

            List<Permission> permissions = role.getPermissions();
            boolean isAllow = permissions.stream().anyMatch(item ->
                    item.getApiPath().equals(path) &&
                            item.getMethod().name().equals(httpMethod));

            if (!isAllow) {
                throw new PermissionException("You don't have permission to access API: " + path +
                        " with method: " + httpMethod + ". Please contact admin for support.");
            }
        }

        return true;
    }
}