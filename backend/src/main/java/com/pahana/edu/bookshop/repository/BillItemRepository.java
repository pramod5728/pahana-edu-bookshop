package com.pahana.edu.bookshop.repository;

import com.pahana.edu.bookshop.entity.BillItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for BillItem entity operations
 * 
 * Provides CRUD operations and custom query methods for BillItem entities
 * with sales analysis and reporting specific queries.
 */
@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    /**
     * Find bill items by bill ID
     */
    List<BillItem> findByBillId(Long billId);

    /**
     * Find bill items by item ID
     */
    Page<BillItem> findByItemId(Long itemId, Pageable pageable);

    /**
     * Find bill items by bill ID and item ID
     */
    List<BillItem> findByBillIdAndItemId(Long billId, Long itemId);

    /**
     * Get total quantity sold for an item
     */
    @Query("SELECT SUM(bi.quantity) FROM BillItem bi WHERE bi.item.id = :itemId AND " +
           "bi.bill.status IN ('PAID', 'PARTIAL_PAID')")
    Long getTotalQuantitySoldForItem(@Param("itemId") Long itemId);

    /**
     * Get total revenue for an item
     */
    @Query("SELECT SUM(bi.totalPrice) FROM BillItem bi WHERE bi.item.id = :itemId AND " +
           "bi.bill.status IN ('PAID', 'PARTIAL_PAID')")
    java.math.BigDecimal getTotalRevenueForItem(@Param("itemId") Long itemId);

    /**
     * Find top selling items by quantity
     */
    @Query("SELECT bi.item, SUM(bi.quantity) as totalQuantity FROM BillItem bi " +
           "WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') " +
           "GROUP BY bi.item ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingItemsByQuantity(Pageable pageable);

    /**
     * Find top selling items by revenue
     */
    @Query("SELECT bi.item, SUM(bi.totalPrice) as totalRevenue FROM BillItem bi " +
           "WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') " +
           "GROUP BY bi.item ORDER BY totalRevenue DESC")
    Page<Object[]> findTopSellingItemsByRevenue(Pageable pageable);

    /**
     * Get item sales within date range
     */
    @Query("SELECT bi.item, SUM(bi.quantity) as totalQuantity, SUM(bi.totalPrice) as totalRevenue " +
           "FROM BillItem bi WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') AND " +
           "bi.bill.billDate BETWEEN :startDate AND :endDate " +
           "GROUP BY bi.item ORDER BY totalRevenue DESC")
    Page<Object[]> getItemSalesInDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Get category-wise sales report
     */
    @Query("SELECT bi.item.category, SUM(bi.quantity) as totalQuantity, SUM(bi.totalPrice) as totalRevenue " +
           "FROM BillItem bi WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') AND " +
           "bi.bill.billDate BETWEEN :startDate AND :endDate " +
           "GROUP BY bi.item.category ORDER BY totalRevenue DESC")
    List<Object[]> getCategorySalesReport(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find bill items with discount
     */
    @Query("SELECT bi FROM BillItem bi WHERE bi.discountPercentage > 0 ORDER BY bi.discountPercentage DESC")
    Page<BillItem> findBillItemsWithDiscount(Pageable pageable);

    /**
     * Get daily item sales
     */
    @Query("SELECT DATE(bi.bill.billDate) as saleDate, bi.item, SUM(bi.quantity) as quantity, SUM(bi.totalPrice) as revenue " +
           "FROM BillItem bi WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') AND " +
           "bi.bill.billDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(bi.bill.billDate), bi.item ORDER BY saleDate DESC, revenue DESC")
    List<Object[]> getDailyItemSales(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find items sold to a specific customer
     */
    @Query("SELECT bi.item, SUM(bi.quantity) as totalQuantity, SUM(bi.totalPrice) as totalAmount " +
           "FROM BillItem bi WHERE bi.bill.customer.id = :customerId AND " +
           "bi.bill.status IN ('PAID', 'PARTIAL_PAID') " +
           "GROUP BY bi.item ORDER BY totalQuantity DESC")
    List<Object[]> getItemsSoldToCustomer(@Param("customerId") Long customerId);

    /**
     * Get average selling price for an item
     */
    @Query("SELECT AVG(bi.unitPrice) FROM BillItem bi WHERE bi.item.id = :itemId AND " +
           "bi.bill.status IN ('PAID', 'PARTIAL_PAID')")
    java.math.BigDecimal getAverageSellingPrice(@Param("itemId") Long itemId);

    /**
     * Find bill items by item code
     */
    @Query("SELECT bi FROM BillItem bi WHERE bi.item.itemCode = :itemCode ORDER BY bi.bill.billDate DESC")
    Page<BillItem> findByItemCode(@Param("itemCode") String itemCode, Pageable pageable);

    /**
     * Get slow moving items (items with low sales in last N days)
     */
    @Query("SELECT bi.item, SUM(bi.quantity) as totalQuantity FROM BillItem bi " +
           "WHERE bi.bill.status IN ('PAID', 'PARTIAL_PAID') AND " +
           "bi.bill.billDate >= :sinceDate " +
           "GROUP BY bi.item " +
           "HAVING SUM(bi.quantity) < :threshold " +
           "ORDER BY totalQuantity ASC")
    List<Object[]> getSlowMovingItems(@Param("sinceDate") LocalDateTime sinceDate,
                                    @Param("threshold") Long threshold);

    /**
     * Get items never sold
     */
    @Query("SELECT i FROM Item i WHERE i.active = true AND " +
           "i.id NOT IN (SELECT DISTINCT bi.item.id FROM BillItem bi)")
    List<com.pahana.edu.bookshop.entity.Item> getNeverSoldItems();
}