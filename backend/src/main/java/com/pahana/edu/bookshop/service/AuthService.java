package com.pahana.edu.bookshop.service;

import com.pahana.edu.bookshop.dto.request.LoginRequest;
import com.pahana.edu.bookshop.dto.request.UserRequest;
import com.pahana.edu.bookshop.dto.response.AuthResponse;
import com.pahana.edu.bookshop.dto.response.UserResponse;
import com.pahana.edu.bookshop.entity.User;
import com.pahana.edu.bookshop.enums.UserRole;
import com.pahana.edu.bookshop.exception.BusinessException;
import com.pahana.edu.bookshop.exception.ResourceNotFoundException;
import com.pahana.edu.bookshop.repository.UserRepository;
import com.pahana.edu.bookshop.security.jwt.JwtUtils;
import com.pahana.edu.bookshop.security.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Authentication and User management
 * 
 * Handles user authentication, authorization, and user management
 * operations including login, logout, and user CRUD operations.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    /**
     * Authenticate user and generate JWT token
     */
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Check if user exists and is not locked
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

            // Check if account is locked
            if (user.isAccountLocked()) {
                throw new BusinessException("Account is locked. Please try again later or contact administrator.");
            }

            // Check if account is active
            if (!user.getActive()) {
                throw new BusinessException("Account is deactivated. Please contact administrator.");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

            // Update user last login and reset failed attempts
            user.updateLastLogin();
            userRepository.save(user);

            // Get user permissions
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<String> permissions = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Create user info
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .active(user.getActive())
                    .lastLogin(user.getLastLogin())
                    .displayName(user.getDisplayName())
                    .build();

            return AuthResponse.builder()
                    .accessToken(jwt)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn((long) jwtUtils.getJwtExpirationMs())
                    .user(userInfo)
                    .permissions(permissions)
                    .build();

        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            handleFailedLoginAttempt(loginRequest.getUsername());
            throw new BusinessException("Invalid username or password");
        }
    }

    /**
     * Refresh JWT token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!user.getActive()) {
            throw new BusinessException("Account is deactivated");
        }

        String newAccessToken = jwtUtils.generateTokenFromUsername(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .lastLogin(user.getLastLogin())
                .displayName(user.getDisplayName())
                .build();

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn((long) jwtUtils.getJwtExpirationMs())
                .user(userInfo)
                .build();
    }

    /**
     * Get current user profile
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return convertToResponse(user);
    }

    /**
     * Update current user profile
     */
    public UserResponse updateCurrentUserProfile(UserRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Validate unique constraints (exclude current user)
        validateUniqueConstraints(request.getUsername(), request.getEmail(),
                request.getPhone(), user.getId());

        // Update fields (user cannot change their own role)
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.markPasswordChanged();
        }

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    /**
     * Change user password
     */
    public void changePassword(String currentPassword, String newPassword) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        // Update to new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.markPasswordChanged();
        userRepository.save(user);
    }

    /**
     * Create new user (admin/manager only)
     */
    public UserResponse createUser(UserRequest request) {
        // Validate unique constraints
        validateUniqueConstraints(request.getUsername(), request.getEmail(),
                request.getPhone(), null);

        // Validate role permissions
        validateRolePermissions(request.getRole());

        // Create user entity
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(request.getRole())
                .phone(request.getPhone())
                .active(request.getActive())
                .build();

        user.markPasswordChanged();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    /**
     * Handle failed login attempt
     */
    private void handleFailedLoginAttempt(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.incrementFailedLoginAttempts();

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.lockAccount(LOCKOUT_DURATION_MINUTES);
            }

            userRepository.save(user);
        });
    }

    /**
     * Get current username from security context
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }
        return authentication.getName();
    }

    /**
     * Validate unique constraints
     */
    private void validateUniqueConstraints(String username, String email, String phone, Long excludeId) {
        // Check username uniqueness
        if (userRepository.existsByUsername(username)) {
            if (excludeId == null || !userRepository.findByUsername(username)
                    .map(User::getId).equals(excludeId)) {
                throw new BusinessException("Username already exists: " + username);
            }
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(email)) {
            if (excludeId == null || !userRepository.findByEmail(email)
                    .map(User::getId).equals(excludeId)) {
                throw new BusinessException("Email already exists: " + email);
            }
        }

        // Check phone uniqueness
        if (phone != null && userRepository.existsByPhone(phone)) {
            if (excludeId == null || !userRepository.findByPhone(phone)
                    .map(User::getId).equals(excludeId)) {
                throw new BusinessException("Phone number already exists: " + phone);
            }
        }
    }

    /**
     * Validate role permissions
     */
    private void validateRolePermissions(UserRole roleToAssign) {
        String currentUsername = getCurrentUsername();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        if (!currentUser.getRole().canManage(roleToAssign)) {
            throw new BusinessException("Insufficient privileges to assign role: " + roleToAssign.getDisplayName());
        }
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .active(user.getActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .displayName(user.getDisplayName())
                .isAccountLocked(user.isAccountLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .build();
    }
}