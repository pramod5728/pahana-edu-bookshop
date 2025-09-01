package com.pahana.edu.bookshop.controller;

import com.pahana.edu.bookshop.dto.request.LoginRequest;
import com.pahana.edu.bookshop.dto.request.UserRequest;
import com.pahana.edu.bookshop.dto.response.ApiResponse;
import com.pahana.edu.bookshop.dto.response.AuthResponse;
import com.pahana.edu.bookshop.dto.response.UserResponse;
import com.pahana.edu.bookshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 * 
 * Handles user authentication, registration, and profile management.
 */
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001", "http://localhost:3002" })
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Token refreshed successfully"));
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        UserResponse userResponse = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = authService.updateCurrentUserProfile(userRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Profile updated successfully"));
    }

    /**
     * Change password
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change current user's password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        authService.changePassword(currentPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /**
     * Create new user (admin/manager only)
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create new user account (requires admin/manager privileges)")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = authService.createUser(userRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User registered successfully"));
    }

    /**
     * Logout endpoint (client-side token removal)
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (token should be removed client-side)")
    public ResponseEntity<ApiResponse<String>> logout() {
        // In JWT implementation, logout is typically handled client-side by removing
        // the token
        // Server-side logout would require token blacklisting which is more complex
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}