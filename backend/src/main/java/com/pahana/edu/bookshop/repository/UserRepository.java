package com.pahana.edu.bookshop.repository;

import com.pahana.edu.bookshop.entity.User;
import com.pahana.edu.bookshop.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * 
 * Provides CRUD operations and custom query methods for User entities
 * with authentication and user management specific queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

       /**
        * Find user by username
        */
       Optional<User> findByUsername(String username);

       /**
        * Find user by email
        */
       Optional<User> findByEmail(String email);

       /**
        * Check if user exists by username
        */
       boolean existsByUsername(String username);

       /**
        * Check if user exists by email
        */
       boolean existsByEmail(String email);

       /**
        * Find active users only
        */
       Page<User> findByActiveTrue(Pageable pageable);

       /**
        * Find inactive users
        */
       Page<User> findByActiveFalse(Pageable pageable);

       /**
        * Find users by role
        */
       Page<User> findByRole(UserRole role, Pageable pageable);

       /**
        * Find active users by role
        */
       Page<User> findByRoleAndActiveTrue(UserRole role, Pageable pageable);

       /**
        * Search users by full name or username (case-insensitive)
        */
       @Query("SELECT u FROM User u WHERE u.active = true AND (" +
                     "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
       Page<User> searchActiveUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

       /**
        * Find users with failed login attempts greater than threshold
        */
       @Query("SELECT u FROM User u WHERE u.failedLoginAttempts > :threshold")
       List<User> findUsersWithFailedLoginAttempts(@Param("threshold") int threshold);

       /**
        * Find locked user accounts
        */
       @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > CURRENT_TIMESTAMP")
       List<User> findLockedUsers();

       /**
        * Find users who haven't logged in for specified days
        */
       @Query("SELECT u FROM User u WHERE u.active = true AND " +
                     "(u.lastLogin IS NULL OR u.lastLogin < :date)")
       List<User> findInactiveUsers(@Param("date") LocalDateTime date);

       /**
        * Find users with expired passwords
        */
       @Query("SELECT u FROM User u WHERE u.active = true AND " +
                     "(u.passwordChangedAt IS NULL OR u.passwordChangedAt < :date)")
       List<User> findUsersWithExpiredPasswords(@Param("date") LocalDateTime date);

       /**
        * Find recently created users
        */
       @Query("SELECT u FROM User u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
       List<User> findRecentlyCreatedUsers(@Param("date") LocalDateTime date);

       /**
        * Find users by role with higher or equal privilege level
        */
       @Query("SELECT u FROM User u WHERE u.active = true AND " +
                     "(u.role = 'ADMIN' OR " +
                     "(u.role = 'MANAGER' AND :role IN ('MANAGER', 'EMPLOYEE', 'VIEWER')) OR " +
                     "(u.role = 'EMPLOYEE' AND :role IN ('EMPLOYEE', 'VIEWER')) OR " +
                     "(u.role = 'VIEWER' AND :role = 'VIEWER'))")
       List<User> findUsersWithRoleOrLower(@Param("role") UserRole role);

       /**
        * Get user count by role
        */
       @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.active = true GROUP BY u.role")
       List<Object[]> getUserCountByRole();

       /**
        * Find admin users
        */
       @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.active = true")
       List<User> findAdminUsers();

       /**
        * Find users who logged in today
        * Note: Simplified for H2 compatibility - returns all users with recent login
        */
       @Query("SELECT u FROM User u WHERE u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
       List<User> findUsersLoggedInToday();

       /**
        * Find users by phone number
        */
       Optional<User> findByPhone(String phone);

       /**
        * Check if phone number exists
        */
       boolean existsByPhone(String phone);

       /**
        * Find users by full name containing search term
        */
       Page<User> findByFullNameContainingIgnoreCaseAndActiveTrue(String fullName, Pageable pageable);

       /**
        * Get total active user count
        */
       @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
       long getActiveUserCount();

       /**
        * Find users created between dates
        */
       @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
       List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Find users by username starting with prefix (for auto-complete)
        */
       @Query("SELECT u FROM User u WHERE u.active = true AND LOWER(u.username) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY u.username")
       List<User> findByUsernameStartingWithIgnoreCase(@Param("prefix") String prefix);
}