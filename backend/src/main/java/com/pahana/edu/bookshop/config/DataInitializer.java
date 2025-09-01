package com.pahana.edu.bookshop.config;

import com.pahana.edu.bookshop.entity.User;
import com.pahana.edu.bookshop.enums.UserRole;
import com.pahana.edu.bookshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Profile({ "dev", "mysql" })
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 DataInitializer running...");

        // Check if admin user already exists
        var adminUser = userRepository.findByUsername("admin");
        System.out.println("🔍 Admin user exists: " + adminUser.isPresent());

        if (adminUser.isEmpty()) {
            System.out.println("✨ Creating default users...");
            createDefaultUsers();
        } else {
            System.out.println("⚠️ Admin user already exists, recreating users with correct passwords...");
            System.out.println(
                    "📝 Existing admin: " + adminUser.get().getUsername() + " - " + adminUser.get().getFullName());

            // For debugging - let's recreate users to ensure correct password encoding
            recreateDefaultUsers();
        }
    }

    private void createDefaultUsers() {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setFullName("System Administrator");
        admin.setEmail("admin@pahanaedu.com");
        admin.setRole(UserRole.ADMIN);
        admin.setPhone("+94771234567");
        admin.setActive(true);
        admin.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(admin);

        // Create manager user
        User manager = new User();
        manager.setUsername("manager");
        manager.setPasswordHash(passwordEncoder.encode("manager123"));
        manager.setFullName("Store Manager");
        manager.setEmail("manager@pahanaedu.com");
        manager.setRole(UserRole.MANAGER);
        manager.setPhone("+94771234568");
        manager.setActive(true);
        manager.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(manager);

        // Create employee user
        User employee = new User();
        employee.setUsername("employee");
        employee.setPasswordHash(passwordEncoder.encode("employee123"));
        employee.setFullName("Sales Employee");
        employee.setEmail("employee@pahanaedu.com");
        employee.setRole(UserRole.EMPLOYEE);
        employee.setPhone("+94771234569");
        employee.setActive(true);
        employee.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(employee);

        System.out.println("✅ Default users created successfully!");
        System.out.println("👤 Admin: admin / admin123");
        System.out.println("👤 Manager: manager / manager123");
        System.out.println("👤 Employee: employee / employee123");
    }

    private void recreateDefaultUsers() {
        System.out.println("🔄 Recreating default users with correct passwords...");

        // Delete existing users
        userRepository.deleteAll();

        // Create fresh users
        createDefaultUsers();
    }
}