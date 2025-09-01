package com.pahana.edu.bookshop.controller;

import com.pahana.edu.bookshop.dto.request.BillRequest;
import com.pahana.edu.bookshop.dto.response.ApiResponse;
import com.pahana.edu.bookshop.dto.response.BillResponse;
import com.pahana.edu.bookshop.dto.response.PagedResponse;
import com.pahana.edu.bookshop.enums.BillStatus;
import com.pahana.edu.bookshop.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for billing management endpoints
 * 
 * Handles bill CRUD operations, payment processing, and related functionality.
 */
@RestController
@RequestMapping("/bills")
@Tag(name = "Billing Management", description = "Bill and invoice management endpoints")
public class BillingController {

    @Autowired
    private BillingService billingService;

    /**
     * Get all bills with pagination
     */
    @GetMapping
    @Operation(summary = "Get all bills", description = "Get paginated list of all bills")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponse>>> getAllBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "billDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<BillResponse> bills = billingService.getAllBills(pageable);
        PagedResponse<BillResponse> pagedResponse = PagedResponse.of(bills);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get bill by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get bill by ID", description = "Get bill details by ID")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillById(@PathVariable Long id) {
        BillResponse bill = billingService.getBillById(id);
        return ResponseEntity.ok(ApiResponse.success(bill));
    }

    /**
     * Get bill by bill number
     */
    @GetMapping("/number/{billNumber}")
    @Operation(summary = "Get bill by number", description = "Get bill details by bill number")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillByBillNumber(@PathVariable String billNumber) {
        BillResponse bill = billingService.getBillByBillNumber(billNumber);
        return ResponseEntity.ok(ApiResponse.success(bill));
    }

    /**
     * Get bills by customer ID
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get bills by customer", description = "Get bills for a specific customer")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponse>>> getBillsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "billDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<BillResponse> bills = billingService.getBillsByCustomerId(customerId, pageable);
        PagedResponse<BillResponse> pagedResponse = PagedResponse.of(bills);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Create new bill
     */
    @PostMapping
    @Operation(summary = "Create new bill", description = "Create a new bill/invoice")
    @PreAuthorize("hasAuthority('PERM_BILL_WRITE')")
    public ResponseEntity<ApiResponse<BillResponse>> createBill(@Valid @RequestBody BillRequest billRequest) {
        BillResponse bill = billingService.createBill(billRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bill, "Bill created successfully"));
    }

    /**
     * Update existing bill
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update bill", description = "Update existing bill details")
    @PreAuthorize("hasAuthority('PERM_BILL_WRITE')")
    public ResponseEntity<ApiResponse<BillResponse>> updateBill(
            @PathVariable Long id,
            @Valid @RequestBody BillRequest billRequest) {
        BillResponse bill = billingService.updateBill(id, billRequest);
        return ResponseEntity.ok(ApiResponse.success(bill, "Bill updated successfully"));
    }

    /**
     * Mark bill as paid
     */
    @PostMapping("/{id}/pay")
    @Operation(summary = "Mark bill as paid", description = "Mark bill as paid")
    @PreAuthorize("hasAuthority('PERM_BILL_WRITE')")
    public ResponseEntity<ApiResponse<BillResponse>> markBillAsPaid(@PathVariable Long id) {
        BillResponse bill = billingService.markBillAsPaid(id);
        return ResponseEntity.ok(ApiResponse.success(bill, "Bill marked as paid successfully"));
    }

    /**
     * Cancel bill
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel bill", description = "Cancel bill and restore stock")
    @PreAuthorize("hasAuthority('PERM_BILL_DELETE')")
    public ResponseEntity<ApiResponse<BillResponse>> cancelBill(@PathVariable Long id) {
        BillResponse bill = billingService.cancelBill(id);
        return ResponseEntity.ok(ApiResponse.success(bill, "Bill cancelled successfully"));
    }

    /**
     * Search bills
     */
    @GetMapping("/search")
    @Operation(summary = "Search bills", description = "Search bills by customer name or bill number")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponse>>> searchBills(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "billDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<BillResponse> bills = billingService.searchBills(query, pageable);
        PagedResponse<BillResponse> pagedResponse = PagedResponse.of(bills);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get bills by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get bills by status", description = "Get bills filtered by status")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponse>>> getBillsByStatus(
            @PathVariable BillStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "billDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<BillResponse> bills = billingService.getBillsByStatus(status, pageable);
        PagedResponse<BillResponse> pagedResponse = PagedResponse.of(bills);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get bills between dates
     */
    @GetMapping("/date-range")
    @Operation(summary = "Get bills by date range", description = "Get bills within specified date range")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<BillResponse>>> getBillsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "billDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<BillResponse> bills = billingService.getBillsBetweenDates(startDate, endDate, pageable);
        PagedResponse<BillResponse> pagedResponse = PagedResponse.of(bills);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get today's bills
     */
    @GetMapping("/today")
    @Operation(summary = "Get today's bills", description = "Get bills created today")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getTodaysBills() {
        List<BillResponse> bills = billingService.getTodaysBills();
        return ResponseEntity.ok(ApiResponse.success(bills));
    }

    /**
     * Get overdue bills
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue bills", description = "Get bills that are overdue")
    @PreAuthorize("hasAuthority('PERM_BILL_READ')")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getOverdueBills(
            @RequestParam(defaultValue = "30") int days) {
        List<BillResponse> bills = billingService.getOverdueBills(days);
        return ResponseEntity.ok(ApiResponse.success(bills));
    }

    /**
     * Get total sales amount for date range
     */
    @GetMapping("/sales-total")
    @Operation(summary = "Get sales total", description = "Get total sales amount for date range")
    @PreAuthorize("hasAuthority('PERM_REPORT_VIEW')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalSalesAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = billingService.getTotalSalesAmount(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    /**
     * Get average bill amount
     */
    @GetMapping("/average-amount")
    @Operation(summary = "Get average bill amount", description = "Get average bill amount")
    @PreAuthorize("hasAuthority('PERM_REPORT_VIEW')")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageBillAmount() {
        BigDecimal average = billingService.getAverageBillAmount();
        return ResponseEntity.ok(ApiResponse.success(average));
    }
}