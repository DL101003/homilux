package com.hoangloc.homilux.config;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.exception.PermissionException;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.util.SecurityUtil;
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

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        if (email != null && !email.isEmpty()) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item ->
                            item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (!isAllow) {
                        throw new PermissionException("Bạn không có quyền truy cập vào api: " + path + " với phương thức: " + httpMethod + " . Vui lòng liên hệ admin để được hỗ trợ.");
                    }
                } else {
                    throw new PermissionException("Vai trò người dùng không hợp lệ");
                }
            }
        }

        return true;
    }
}