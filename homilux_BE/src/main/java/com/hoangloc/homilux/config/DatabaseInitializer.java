package com.hoangloc.homilux.config;

import com.hoangloc.homilux.entities.Permission;
import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.PermissionRepository;
import com.hoangloc.homilux.repositories.RoleRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Bắt đầu tạo dữ liệu...");
        long countPermissions = permissionRepository.count();
        long countRoles = roleRepository.count();
        long countUsers = userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            // EVENTS permissions
            arr.add(new Permission("Create an booking", "/api/v1/bookings", "POST", "BOOKINGS"));
            arr.add(new Permission("Get all bookings", "/api/v1/bookings", "GET", "BOOKINGS"));
            arr.add(new Permission("Get booking by id", "/api/v1/bookings/{id}", "GET", "BOOKINGS"));
            arr.add(new Permission("Update booking status", "/api/v1/bookings/{id}/status", "PATCH", "BOOKINGS"));
            arr.add(new Permission("Delete booking", "/api/v1/bookings/{id}", "DELETE", "BOOKINGS"));

            // EVENT_TYPE permissions
            arr.add(new Permission("Create an event-types", "/api/v1/event-types", "POST", "EVENT_TYPES"));
            arr.add(new Permission("Get all event-types", "/api/v1/event-types", "GET", "EVENT_TYPES"));
            arr.add(new Permission("Get event-types by id", "/api/v1/event-types/{id}", "GET", "EVENT_TYPES"));
            arr.add(new Permission("Update event-types", "/api/v1/event-types/{id}", "PUT", "EVENT_TYPES"));
            arr.add(new Permission("Delete event-types", "/api/v1/event-types/{id}", "DELETE", "EVENT_TYPES"));

            // PAYMENTS permissions
            arr.add(new Permission("Get payments for booking", "/api/v1/bookings/{bookingId}/payments", "GET", "PAYMENTS"));
            arr.add(new Permission("Get all payments", "/api/v1/payments", "GET", "PAYMENTS"));
            arr.add(new Permission("Create a payment", "/api/v1/bookings/{bookingId}/payments", "POST", "PAYMENTS"));
            arr.add(new Permission("Get payment by id", "/api/v1/payments/{paymentId}", "GET", "PAYMENTS"));
            arr.add(new Permission("Delete payment", "/api/v1/payments/{id}", "DELETE", "PAYMENTS"));
            arr.add(new Permission("VNPay payment", "/api/v1/payments/callback", "GET", "PAYMENTS"));
            arr.add(new Permission("Create VNPay payment", "/api/v1/payments/create-vnpay", "POST", "PAYMENTS"));

            // REVIEWS permissions
            arr.add(new Permission("Create a review", "/api/v1/bookings/{bookingId}/reviews", "POST", "REVIEWS"));
            arr.add(new Permission("Get review for booking", "/api/v1/bookings/{bookingId}/reviews", "GET", "REVIEWS"));
            arr.add(new Permission("Get all reviews", "/api/v1/reviews", "GET", "REVIEWS"));

            // ROLES permissions
            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Get all roles", "/api/v1/roles", "GET", "ROLES"));
            arr.add(new Permission("Get role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Update role", "/api/v1/roles/{id}", "PUT", "ROLES"));
            arr.add(new Permission("Delete role", "/api/v1/roles/{id}", "DELETE", "ROLES"));

            // RENTALSERVICES permissions
            arr.add(new Permission("Create a service", "/api/v1/services", "POST", "SERVICES"));
            arr.add(new Permission("Get all services", "/api/v1/services", "GET", "SERVICES"));
            arr.add(new Permission("Get service by id", "/api/v1/services/{id}", "GET", "SERVICES"));
            arr.add(new Permission("Update service", "/api/v1/services/{id}", "PUT", "SERVICES"));
            arr.add(new Permission("Delete service", "/api/v1/services/{id}", "DELETE", "SERVICES"));

            // USERS permissions
            arr.add(new Permission("Create an user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Get all users", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Get user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Update user", "/api/v1/users/{id}", "PUT", "USERS"));
            arr.add(new Permission("Delete user", "/api/v1/users/{id}", "DELETE", "USERS"));

            // FILES permissions
            arr.add(new Permission("Upload files", "/api/v1/files", "POST", "FILES"));

            // PERMISSIONS permissions
            arr.add(new Permission("Get all permissions", "/api/v1/permissions", "GET", "PERMISSIONS"));

            permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = permissionRepository.findAll();
            Role roleAdmin = new Role();
            roleAdmin.setName("ADMIN");
            roleAdmin.setPermissions(allPermissions);
            roleRepository.save(roleAdmin);
        }

        if (countUsers == 0) {
            User adminUser = new User();

            adminUser.setEmail("hani101003@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setFullName("Admin");
            adminUser.setCreatedBy("SYSTEM");
            adminUser.setCreatedAt(Instant.now());

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "ADMIN"));
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println("Data initialization successful");
        } else {
            System.out.println("Data already exists");
        }

    }
}
