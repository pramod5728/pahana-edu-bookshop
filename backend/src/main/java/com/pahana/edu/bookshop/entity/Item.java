package com.pahana.edu.bookshop.entity;

import com.pahana.edu.bookshop.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Item entity representing bookshop inventory items
 * 
 * This entity stores item/product information including pricing,
 * stock quantities, and maintains relationships with bill items.
 */
@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_item_code", columnList = "item_code"),
    @Index(name = "idx_item_name", columnList = "name"),
    @Index(name = "idx_item_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "billItems")
@EqualsAndHashCode(callSuper = false, exclude = "billItems")
public class Item extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_code", unique = true, nullable = false, length = 30)
    @NotBlank(message = "Item code is required")
    @Size(max = 30, message = "Item code must not exceed 30 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Item code must contain only uppercase letters, numbers, and hyphens")
    private String itemCode;

    @Column(name = "name", nullable = false, length = 150)
    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Column(name = "category", length = 50)
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Column(name = "minimum_stock_level")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Builder.Default
    private Integer minimumStockLevel = 10;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BillItem> billItems = new ArrayList<>();

    /**
     * Business method to check if item is low on stock
     */
    public boolean isLowStock() {
        return stockQuantity <= minimumStockLevel;
    }

    /**
     * Business method to check if item is in stock
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Business method to check if sufficient quantity is available
     */
    public boolean hasAvailableQuantity(int requestedQuantity) {
        return stockQuantity >= requestedQuantity;
    }

    /**
     * Business method to reduce stock quantity
     */
    public void reduceStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to reduce cannot be negative");
        }
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock available");
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Business method to increase stock quantity
     */
    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to add cannot be negative");
        }
        this.stockQuantity += quantity;
    }

    /**
     * Business method to get display name with code
     */
    public String getDisplayName() {
        return String.format("%s (%s)", name, itemCode);
    }
}