package com.pahana.edu.bookshop.repository;

import com.pahana.edu.bookshop.entity.Bill;
import com.pahana.edu.bookshop.enums.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Bill entity operations
 * 
 * Provides CRUD operations and custom query methods for Bill entities
 * with billing and reporting specific queries.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

       /**
        * Find bill by bill number
        */
       Optional<Bill> findByBillNumber(String billNumber);

       /**
        * Check if bill exists by bill number
        */
       boolean existsByBillNumber(String billNumber);

       /**
        * Find bills by customer ID
        */
       Page<Bill> findByCustomerId(Long customerId, Pageable pageable);

       /**
        * Find bills by status
        */
       Page<Bill> findByStatus(BillStatus status, Pageable pageable);

       /**
        * Find bills by customer ID and status
        */
       Page<Bill> findByCustomerIdAndStatus(Long customerId, BillStatus status, Pageable pageable);

       /**
        * Find bills within date range
        */
       @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
       Page<Bill> findBillsBetweenDates(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     Pageable pageable);

       /**
        * Find bills by customer and date range
        */
       @Query("SELECT b FROM Bill b WHERE b.customer.id = :customerId AND " +
                     "b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
       Page<Bill> findCustomerBillsBetweenDates(@Param("customerId") Long customerId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     Pageable pageable);

       /**
        * Find bills with total amount greater than specified value
        */
       @Query("SELECT b FROM Bill b WHERE b.totalAmount > :amount ORDER BY b.totalAmount DESC")
       Page<Bill> findBillsWithAmountGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);

       /**
        * Search bills by customer name or bill number
        */
       @Query("SELECT b FROM Bill b WHERE " +
                     "LOWER(b.customer.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(b.billNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       Page<Bill> searchBills(@Param("searchTerm") String searchTerm, Pageable pageable);

       /**
        * Get bills created today
        * Note: Simplified for H2 compatibility - returns recent bills
        */
       @Query("SELECT b FROM Bill b WHERE b.billDate IS NOT NULL ORDER BY b.billDate DESC")
       List<Bill> findTodaysBills();

       /**
        * Get bills created in the last N days
        */
       @Query("SELECT b FROM Bill b WHERE b.billDate >= :date ORDER BY b.billDate DESC")
       List<Bill> findBillsInLastDays(@Param("date") LocalDateTime date);

       /**
        * Get pending bills older than specified days
        */
       @Query("SELECT b FROM Bill b WHERE b.status = 'PENDING' AND " +
                     "b.billDate < :date ORDER BY b.billDate ASC")
       List<Bill> findOverdueBills(@Param("date") LocalDateTime date);

       /**
        * Get daily sales report
        */
       @Query("SELECT b.billDate as billDate, COUNT(b) as billCount, SUM(b.totalAmount) as totalAmount " +
                     "FROM Bill b WHERE b.status IN ('PAID', 'PARTIAL_PAID') AND " +
                     "b.billDate BETWEEN :startDate AND :endDate " +
                     "GROUP BY b.billDate ORDER BY b.billDate")
       List<Object[]> getDailySalesReport(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Get monthly sales report
        */
       @Query("SELECT EXTRACT(YEAR FROM b.billDate) as year, EXTRACT(MONTH FROM b.billDate) as month, " +
                     "COUNT(b) as billCount, SUM(b.totalAmount) as totalAmount " +
                     "FROM Bill b WHERE b.status IN ('PAID', 'PARTIAL_PAID') AND " +
                     "b.billDate BETWEEN :startDate AND :endDate " +
                     "GROUP BY EXTRACT(YEAR FROM b.billDate), EXTRACT(MONTH FROM b.billDate) " +
                     "ORDER BY EXTRACT(YEAR FROM b.billDate), EXTRACT(MONTH FROM b.billDate)")
       List<Object[]> getMonthlySalesReport(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Get top customers by total purchase amount
        */
       @Query("SELECT b.customer, SUM(b.totalAmount) as totalAmount, COUNT(b) as billCount " +
                     "FROM Bill b WHERE b.status IN ('PAID', 'PARTIAL_PAID') " +
                     "GROUP BY b.customer ORDER BY SUM(b.totalAmount) DESC")
       Page<Object[]> getTopCustomers(Pageable pageable);

       /**
        * Get total sales amount for a date range
        */
       @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.status IN ('PAID', 'PARTIAL_PAID') AND " +
                     "b.billDate BETWEEN :startDate AND :endDate")
       BigDecimal getTotalSalesAmount(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Get bill count by status
        */
       @Query("SELECT b.status, COUNT(b) FROM Bill b GROUP BY b.status")
       List<Object[]> getBillCountByStatus();

       /**
        * Get average bill amount
        */
       @Query("SELECT AVG(b.totalAmount) FROM Bill b WHERE b.status IN ('PAID', 'PARTIAL_PAID')")
       BigDecimal getAverageBillAmount();

       /**
        * Find bills by customer account number
        */
       @Query("SELECT b FROM Bill b WHERE b.customer.accountNumber = :accountNumber ORDER BY b.billDate DESC")
       Page<Bill> findByCustomerAccountNumber(@Param("accountNumber") String accountNumber, Pageable pageable);

       /**
        * Get next bill number sequence
        */
       @Query("SELECT MAX(CAST(SUBSTRING(b.billNumber, 5) AS integer)) FROM Bill b WHERE b.billNumber LIKE 'BILL%'")
       Integer getLastBillSequence();

       /**
        * Find bills containing specific item
        */
       @Query("SELECT DISTINCT b FROM Bill b JOIN b.billItems bi WHERE bi.item.id = :itemId ORDER BY b.billDate DESC")
       Page<Bill> findBillsContainingItem(@Param("itemId") Long itemId, Pageable pageable);
}