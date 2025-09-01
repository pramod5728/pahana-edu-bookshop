package com.pahana.edu.bookshop.entity;

import com.pahana.edu.bookshop.entity.base.BaseAuditEntity;
import com.pahana.edu.bookshop.enums.BillStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill entity representing customer bills/invoices
 * 
 * This entity stores billing information including customer details,
 * bill items, amounts, and maintains relationships with customers and bill items.
 */
@Entity
@Table(name = "bills", indexes = {
    @Index(name = "idx_bill_number", columnList = "bill_number"),
    @Index(name = "idx_bill_customer", columnList = "customer_id"),
    @Index(name = "idx_bill_date", columnList = "bill_date"),
    @Index(name = "idx_bill_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"customer", "billItems"})
@EqualsAndHashCode(callSuper = false, exclude = {"customer", "billItems"})
public class Bill extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bill_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Bill number is required")
    @Size(max = 20, message = "Bill number must not exceed 20 characters")
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @Column(name = "bill_date", nullable = false)
    @NotNull(message = "Bill date is required")
    private LocalDateTime billDate;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.00", message = "Subtotal cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Subtotal must have at most 10 integer digits and 2 decimal places")
    private BigDecimal subtotal;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 4)
    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.0000", message = "Tax rate cannot be negative")
    @DecimalMax(value = "1.0000", message = "Tax rate cannot exceed 100%")
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("0.1500"); // 15% VAT

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Tax amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    @DecimalMin(value = "0.00", message = "Discount amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Discount amount must have at most 10 integer digits and 2 decimal places")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Bill status is required")
    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<BillItem> billItems = new ArrayList<>();

    /**
     * Helper method to add a bill item
     */
    public void addBillItem(BillItem billItem) {
        billItems.add(billItem);
        billItem.setBill(this);
        recalculateAmounts();
    }

    /**
     * Helper method to remove a bill item
     */
    public void removeBillItem(BillItem billItem) {
        billItems.remove(billItem);
        billItem.setBill(null);
        recalculateAmounts();
    }

    /**
     * Business method to recalculate all amounts based on bill items
     */
    public void recalculateAmounts() {
        // Calculate subtotal from bill items
        this.subtotal = billItems.stream()
                .map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate tax amount
        this.taxAmount = subtotal.multiply(taxRate);

        // Calculate total amount
        this.totalAmount = subtotal.add(taxAmount).subtract(discountAmount);
    }

    /**
     * Business method to mark bill as paid
     */
    public void markAsPaid() {
        this.status = BillStatus.PAID;
    }

    /**
     * Business method to mark bill as cancelled
     */
    public void markAsCancelled() {
        this.status = BillStatus.CANCELLED;
    }

    /**
     * Business method to check if bill can be modified
     */
    public boolean canBeModified() {
        return status == BillStatus.PENDING || status == BillStatus.DRAFT;
    }

    /**
     * Business method to get total quantity of items
     */
    public int getTotalQuantity() {
        return billItems.stream()
                .mapToInt(BillItem::getQuantity)
                .sum();
    }

    /**
     * Business method to get bill display number
     */
    public String getDisplayNumber() {
        return String.format("BILL-%s", billNumber);
    }
}