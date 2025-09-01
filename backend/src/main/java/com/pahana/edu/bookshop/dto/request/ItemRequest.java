package com.pahana.edu.bookshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for item creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "Item code is required")
    @Size(max = 30, message = "Item code must not exceed 30 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Item code must contain only uppercase letters, numbers, and hyphens")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minimumStockLevel;

    @Builder.Default
    private Boolean active = true;
}