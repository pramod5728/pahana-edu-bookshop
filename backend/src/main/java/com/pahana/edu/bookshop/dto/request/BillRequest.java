package com.pahana.edu.bookshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for bill creation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Bill must contain at least one item")
    @Valid
    private List<BillItemRequest> items;

    @DecimalMin(value = "0.00", message = "Discount amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Discount amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal discountAmount;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillItemRequest {

        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 9999, message = "Quantity cannot exceed 9999")
        private Integer quantity;

        @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
        @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
        private BigDecimal discountPercentage;
    }
}