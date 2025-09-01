package com.pahana.edu.bookshop.repository;

import com.pahana.edu.bookshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entity operations
 * 
 * Provides CRUD operations and custom query methods for Item entities
 * with inventory management specific queries.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Find item by item code
     */
    Optional<Item> findByItemCode(String itemCode);

    /**
     * Check if item exists by item code
     */
    boolean existsByItemCode(String itemCode);

    /**
     * Find items by category
     */
    Page<Item> findByCategory(String category, Pageable pageable);

    /**
     * Find active items only
     */
    Page<Item> findByActiveTrue(Pageable pageable);

    /**
     * Find inactive items
     */
    Page<Item> findByActiveFalse(Pageable pageable);

    /**
     * Find items by name containing search term (case-insensitive)
     */
    Page<Item> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    /**
     * Find items by category and active status
     */
    Page<Item> findByCategoryAndActiveTrue(String category, Pageable pageable);

    /**
     * Find items with low stock (stock quantity <= minimum stock level)
     */
    @Query("SELECT i FROM Item i WHERE i.stockQuantity <= i.minimumStockLevel AND i.active = true")
    List<Item> findLowStockItems();

    /**
     * Find items out of stock
     */
    @Query("SELECT i FROM Item i WHERE i.stockQuantity = 0 AND i.active = true")
    List<Item> findOutOfStockItems();

    /**
     * Find items with stock quantity less than specified amount
     */
    @Query("SELECT i FROM Item i WHERE i.stockQuantity < :quantity AND i.active = true")
    List<Item> findItemsWithStockLessThan(@Param("quantity") int quantity);

    /**
     * Find items within price range
     */
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice AND i.active = true ORDER BY i.price")
    Page<Item> findItemsInPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);

    /**
     * Search items by multiple criteria (name, item code, description, category)
     */
    @Query("SELECT i FROM Item i WHERE i.active = true AND (" +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Item> searchActiveItems(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find all distinct categories
     */
    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL AND i.active = true ORDER BY i.category")
    List<String> findAllCategories();

    /**
     * Find top selling items by total quantity sold
     */
    @Query("SELECT i FROM Item i JOIN i.billItems bi JOIN bi.bill b " +
           "WHERE b.status NOT IN ('CANCELLED') " +
           "GROUP BY i.id " +
           "ORDER BY SUM(bi.quantity) DESC")
    Page<Item> findTopSellingItems(Pageable pageable);

    /**
     * Find items never sold
     */
    @Query("SELECT i FROM Item i WHERE i.active = true AND " +
           "i.id NOT IN (SELECT DISTINCT bi.item.id FROM BillItem bi)")
    List<Item> findNeverSoldItems();

    /**
     * Get total item count by category
     */
    @Query("SELECT i.category, COUNT(i) FROM Item i WHERE i.active = true GROUP BY i.category")
    List<Object[]> getItemCountByCategory();

    /**
     * Get total inventory value
     */
    @Query("SELECT SUM(i.price * i.stockQuantity) FROM Item i WHERE i.active = true")
    BigDecimal getTotalInventoryValue();

    /**
     * Find items by name starting with prefix (for auto-complete)
     */
    @Query("SELECT i FROM Item i WHERE i.active = true AND LOWER(i.name) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY i.name")
    List<Item> findByNameStartingWithIgnoreCase(@Param("prefix") String prefix);

    /**
     * Find items that need restocking (below minimum level)
     */
    @Query("SELECT i FROM Item i WHERE i.active = true AND i.stockQuantity < i.minimumStockLevel ORDER BY i.stockQuantity ASC")
    List<Item> findItemsNeedingRestock();

    /**
     * Update stock quantity for an item
     */
    @Query("UPDATE Item i SET i.stockQuantity = i.stockQuantity + :quantity WHERE i.id = :itemId")
    void updateStockQuantity(@Param("itemId") Long itemId, @Param("quantity") int quantity);
}