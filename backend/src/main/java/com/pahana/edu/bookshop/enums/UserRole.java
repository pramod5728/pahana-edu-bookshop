package com.pahana.edu.bookshop.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration for user roles and permissions
 * 
 * Defines the different user roles in the system with their
 * corresponding permission levels and capabilities.
 */
public enum UserRole {
    /**
     * System administrator with full access
     */
    ADMIN("Administrator", 4),
    
    /**
     * Manager with elevated privileges
     */
    MANAGER("Manager", 3),
    
    /**
     * Regular employee with standard access
     */
    EMPLOYEE("Employee", 2),
    
    /**
     * Read-only viewer access
     */
    VIEWER("Viewer", 1);

    private final String displayName;
    private final int level;

    UserRole(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Check if this role has higher or equal privileges than the specified role
     */
    public boolean hasPrivilegeLevel(UserRole requiredRole) {
        return this.level >= requiredRole.level;
    }

    /**
     * Check if this role can manage users of the specified role
     */
    public boolean canManage(UserRole targetRole) {
        return this.level > targetRole.level;
    }

    /**
     * Get all roles that this role can manage
     */
    public List<UserRole> getManageableRoles() {
        return Arrays.stream(UserRole.values())
                .filter(role -> this.canManage(role))
                .toList();
    }

    /**
     * Check if this role has admin privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role has manager or higher privileges
     */
    public boolean isManagerOrHigher() {
        return this.level >= MANAGER.level;
    }

    /**
     * Check if this role can perform write operations
     */
    public boolean canWrite() {
        return this.level >= EMPLOYEE.level;
    }

    /**
     * Check if this role can only read data
     */
    public boolean isReadOnly() {
        return this == VIEWER;
    }

    /**
     * Get role by display name (case-insensitive)
     */
    public static UserRole fromDisplayName(String displayName) {
        return Arrays.stream(UserRole.values())
                .filter(role -> role.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role display name: " + displayName));
    }
}