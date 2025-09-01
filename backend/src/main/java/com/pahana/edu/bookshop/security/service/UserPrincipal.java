package com.pahana.edu.bookshop.security.service;

import com.pahana.edu.bookshop.entity.User;
import com.pahana.edu.bookshop.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Custom UserDetails implementation
 * 
 * Wraps the User entity to provide Spring Security with user information
 * including username, password, authorities, and account status.
 */
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean active;
    private boolean accountLocked;

    public UserPrincipal(Long id, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean active, boolean accountLocked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
        this.accountLocked = accountLocked;
    }

    /**
     * Create UserPrincipal from User entity
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = mapRoleToAuthorities(user.getRole());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities,
                user.getActive(),
                user.isAccountLocked());
    }

    /**
     * Map user role to Spring Security authorities
     */
    private static List<GrantedAuthority> mapRoleToAuthorities(UserRole role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add role-based authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // Add permission-based authorities based on role
        // Using if-else to avoid switch fall-through issues with Java 22
        if (role == UserRole.ADMIN) {
            // Admin has all permissions
            authorities.add(new SimpleGrantedAuthority("PERM_USER_MANAGE"));
            authorities.add(new SimpleGrantedAuthority("PERM_SYSTEM_CONFIG"));
            authorities.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_READ"));
        } else if (role == UserRole.MANAGER) {
            // Manager has report view and delete permissions plus lower levels
            authorities.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_DELETE"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_READ"));
        } else if (role == UserRole.EMPLOYEE) {
            // Employee has write permissions plus read permissions
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_WRITE"));
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_READ"));
        } else if (role == UserRole.VIEWER) {
            // Viewer has only read permissions
            authorities.add(new SimpleGrantedAuthority("PERM_CUSTOMER_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_ITEM_READ"));
            authorities.add(new SimpleGrantedAuthority("PERM_BILL_READ"));
        }

        return authorities;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}