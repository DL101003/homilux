package com.hoangloc.homilux.config;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.repository.PermissionRepository;
import com.hoangloc.homilux.repository.RoleRepository;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Bắt đầu tạo dữ liệu...");
        long countPermissions = permissionRepository.count();
        long countRoles = roleRepository.count();
        long countUsers = userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            // DISHES permissions
            arr.add(new Permission("Create a dish", "/api/v1/dishes", "POST", "DISHES"));
            arr.add(new Permission("Get all dishes", "/api/v1/dishes", "GET", "DISHES"));
            arr.add(new Permission("Get dish by id", "/api/v1/dishes/{id}", "GET", "DISHES"));
            arr.add(new Permission("Update dish", "/api/v1/dishes", "PUT", "DISHES"));
            arr.add(new Permission("Delete dish", "/api/v1/dishes/{id}", "DELETE", "DISHES"));

            // EVENTS permissions
            arr.add(new Permission("Create an event", "/api/v1/events", "POST", "EVENTS"));
            arr.add(new Permission("Get all events", "/api/v1/events", "GET", "EVENTS"));
            arr.add(new Permission("Get event by id", "/api/v1/events/{id}", "GET", "EVENTS"));
            arr.add(new Permission("Update event", "/api/v1/events", "PUT", "EVENTS"));
            arr.add(new Permission("Delete event", "/api/v1/events/{id}", "DELETE", "EVENTS"));

            // EVENT_TYPE permissions
            arr.add(new Permission("Create an event-types", "/api/v1/event-types", "POST", "EVENT_TYPES"));
            arr.add(new Permission("Get all event-types", "/api/v1/event-types", "GET", "EVENT_TYPES"));
            arr.add(new Permission("Get event-types by id", "/api/v1/event-types/{id}", "GET", "EVENT_TYPES"));
            arr.add(new Permission("Update event-types", "/api/v1/event-types", "PUT", "EVENT_TYPES"));
            arr.add(new Permission("Delete event-types", "/api/v1/event-types/{id}", "DELETE", "EVENT_TYPES"));

            // MENUS permissions
            arr.add(new Permission("Create a menu", "/api/v1/menus", "POST", "MENUS"));
            arr.add(new Permission("Get all menus", "/api/v1/menus", "GET", "MENUS"));
            arr.add(new Permission("Get menu by id", "/api/v1/menus/{id}", "GET", "MENUS"));
            arr.add(new Permission("Update menu", "/api/v1/menus", "PUT", "MENUS"));
            arr.add(new Permission("Delete menu", "/api/v1/menus/{id}", "DELETE", "MENUS"));

            // PAYMENTS permissions
            arr.add(new Permission("Create a payment", "/api/v1/payments", "POST", "PAYMENTS"));
            arr.add(new Permission("Get all payments", "/api/v1/payments", "GET", "PAYMENTS"));
            arr.add(new Permission("Get payment by id", "/api/v1/payments/{id}", "GET", "PAYMENTS"));
            arr.add(new Permission("Update payment", "/api/v1/payments", "PUT", "PAYMENTS"));
            arr.add(new Permission("Delete payment", "/api/v1/payments/{id}", "DELETE", "PAYMENTS"));

            // REVIEWS permissions
            arr.add(new Permission("Create a review", "/api/v1/reviews", "POST", "REVIEWS"));
            arr.add(new Permission("Get all reviews", "/api/v1/reviews", "GET", "REVIEWS"));
            arr.add(new Permission("Get review by id", "/api/v1/reviews/{id}", "GET", "REVIEWS"));
            arr.add(new Permission("Update review", "/api/v1/reviews", "PUT", "REVIEWS"));
            arr.add(new Permission("Delete review", "/api/v1/reviews/{id}", "DELETE", "REVIEWS"));

            // ROLES permissions
            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Get all roles", "/api/v1/roles", "GET", "ROLES"));
            arr.add(new Permission("Get role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Update role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete role", "/api/v1/roles/{id}", "DELETE", "ROLES"));

            // SERVICES permissions
            arr.add(new Permission("Create a service", "/api/v1/services", "POST", "SERVICES"));
            arr.add(new Permission("Get all services", "/api/v1/services", "GET", "SERVICES"));
            arr.add(new Permission("Get service by id", "/api/v1/services/{id}", "GET", "SERVICES"));
            arr.add(new Permission("Update service", "/api/v1/services", "PUT", "SERVICES"));
            arr.add(new Permission("Delete service", "/api/v1/services/{id}", "DELETE", "SERVICES"));

            // USERS permissions
            arr.add(new Permission("Create an user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Get all users", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Get user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Update user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete user", "/api/v1/users/{id}", "DELETE", "USERS"));

            // FILES permissions
            arr.add(new Permission("Upload files", "/api/v1/files", "POST", "FILES"));

            permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = permissionRepository.findAll();
            Role roleAdmin = new Role();
            roleAdmin.setName("ADMIN");
            roleAdmin.setPermissions(allPermissions);
            roleAdmin.setCreatedBy("SYSTEM");
            roleAdmin.setCreatedAt(Instant.now());
            roleRepository.save(roleAdmin);
        }

        if (countUsers == 0) {
            User adminUser = new User();

            adminUser.setEmail("hani101003@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setName("Admin");
            adminUser.setCreatedBy("SYSTEM");
            adminUser.setCreatedAt(Instant.now());

            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println("Tạo dữ liệu thành công");
        } else {
            System.out.println("Dữ liệu đã tồn tại");
        }

    }
}
