package com.pahana.edu.bookshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * BillItem entity representing individual items within a bill
 * 
 * This is a junction entity that represents the many-to-many relationship
 * between Bill and Item entities with additional attributes like quantity and pricing.
 */
@Entity
@Table(name = "bill_items", indexes = {
    @Index(name = "idx_bill_item_bill", columnList = "bill_id"),
    @Index(name = "idx_bill_item_item", columnList = "item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"bill", "item"})
@EqualsAndHashCode(exclude = {"bill", "item"})
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @NotNull(message = "Bill is required")
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull(message = "Item is required")
    private Item item;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 9999, message = "Quantity cannot exceed 9999")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal totalPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    /**
     * Business method to calculate total price based on quantity and unit price
     */
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            BigDecimal baseTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            
            if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = baseTotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
                this.totalPrice = baseTotal.subtract(discountAmount);
            } else {
                this.totalPrice = baseTotal;
            }
        }
    }

    /**
     * Business method to set unit price from item's current price
     */
    public void setUnitPriceFromItem() {
        if (item != null && item.getPrice() != null) {
            this.unitPrice = item.getPrice();
            calculateTotalPrice();
        }
    }

    /**
     * Business method to get discount amount
     */
    public BigDecimal getDiscountAmount() {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal baseTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            return baseTotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Business method to get item display name
     */
    public String getItemDisplayName() {
        return item != null ? item.getDisplayName() : "Unknown Item";
    }

    /**
     * Lifecycle callback to calculate total price before persisting
     */
    @PrePersist
    @PreUpdate
    private void calculateTotalPriceBeforeSave() {
        calculateTotalPrice();
    }
}