package com.pahana.edu.bookshop.entity;

import com.pahana.edu.bookshop.entity.base.BaseAuditEntity;
import com.pahana.edu.bookshop.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User entity representing system users with authentication and authorization
 * 
 * This entity stores user credentials, profile information, and role-based
 * access control information for the bookshop management system.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "passwordHash")
@EqualsAndHashCode(callSuper = false, exclude = "passwordHash")
public class User extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Password hash is required")
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @NotNull(message = "User role is required")
    @Builder.Default
    private UserRole role = UserRole.EMPLOYEE;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "phone", length = 15)
    @Pattern(regexp = "^(\\+94|0)[0-9]{9}$", message = "Invalid Sri Lankan phone number format")
    private String phone;

    /**
     * Business method to check if user account is locked
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Business method to lock user account for specified minutes
     */
    public void lockAccount(int minutes) {
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Business method to unlock user account
     */
    public void unlockAccount() {
        this.accountLockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    /**
     * Business method to increment failed login attempts
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * Business method to reset failed login attempts
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * Business method to update last login time
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        resetFailedLoginAttempts();
    }

    /**
     * Business method to check if user has admin privileges
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Business method to check if user has manager privileges
     */
    public boolean isManager() {
        return role == UserRole.ADMIN || role == UserRole.MANAGER;
    }

    /**
     * Business method to check if password needs to be changed (older than 90 days)
     */
    public boolean isPasswordExpired() {
        if (passwordChangedAt == null) {
            return true; // Force password change if never set
        }
        return passwordChangedAt.isBefore(LocalDateTime.now().minusDays(90));
    }

    /**
     * Business method to mark password as changed
     */
    public void markPasswordChanged() {
        this.passwordChangedAt = LocalDateTime.now();
    }

    /**
     * Business method to get display name
     */
    public String getDisplayName() {
        return String.format("%s (%s)", fullName, username);
    }
}