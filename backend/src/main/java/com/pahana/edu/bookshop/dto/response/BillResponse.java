package com.pahana.edu.bookshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pahana.edu.bookshop.enums.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for bill response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponse {

    private Long id;
    private String billNumber;
    private CustomerResponse customer;
    private LocalDateTime billDate;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BillStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private List<BillItemResponse> billItems;
    private String displayNumber;
    private Integer totalQuantity;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BillItemResponse {
        private Long id;
        private ItemResponse item;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
        private BigDecimal totalPrice;
    }
}