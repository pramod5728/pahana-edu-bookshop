package com.pahana.edu.bookshop.repository;

import com.pahana.edu.bookshop.entity.Customer;
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
 * Repository interface for Customer entity operations
 * 
 * Provides CRUD operations and custom query methods for Customer entities
 * with Spring Data JPA automatic implementation.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

       /**
        * Find customer by account number
        */
       Optional<Customer> findByAccountNumber(String accountNumber);

       /**
        * Find customer by email
        */
       Optional<Customer> findByEmail(String email);

       /**
        * Find customer by phone number
        */
       Optional<Customer> findByPhone(String phone);

       /**
        * Check if customer exists by account number
        */
       boolean existsByAccountNumber(String accountNumber);

       /**
        * Check if customer exists by email
        */
       boolean existsByEmail(String email);

       /**
        * Check if customer exists by phone
        */
       boolean existsByPhone(String phone);

       /**
        * Find customers by name containing the search term (case-insensitive)
        */
       Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

       /**
        * Find customers by address containing the search term (case-insensitive)
        */
       Page<Customer> findByAddressContainingIgnoreCase(String address, Pageable pageable);

       /**
        * Search customers by multiple criteria (name, account number, phone, email)
        */
       @Query("SELECT c FROM Customer c WHERE " +
                     "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(c.accountNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);

       /**
        * Find customers with bills count greater than specified number
        */
       @Query("SELECT c FROM Customer c WHERE SIZE(c.bills) > :billCount")
       List<Customer> findCustomersWithMoreThanXBills(@Param("billCount") int billCount);

       /**
        * Find customers who haven't placed any bills
        */
       @Query("SELECT c FROM Customer c WHERE SIZE(c.bills) = 0")
       List<Customer> findCustomersWithoutBills();

       /**
        * Find top customers by total bill amount
        */
       @Query("SELECT c FROM Customer c JOIN c.bills b " +
                     "GROUP BY c.id " +
                     "ORDER BY SUM(b.totalAmount) DESC")
       Page<Customer> findTopCustomersByTotalAmount(Pageable pageable);

       /**
        * Get customer statistics
        */
       @Query("SELECT COUNT(c) FROM Customer c")
       long getTotalCustomerCount();

       /**
        * Get customers created in the last N days
        * Note: Simplified for H2 compatibility
        */
       @Query("SELECT c FROM Customer c WHERE c.createdAt >= :dateThreshold")
       List<Customer> findCustomersCreatedInLastDays(@Param("dateThreshold") LocalDateTime dateThreshold);

       /**
        * Find customers by name starting with prefix (for auto-complete)
        */
       @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY c.name")
       List<Customer> findByNameStartingWithIgnoreCase(@Param("prefix") String prefix);
}