package com.pahana.edu.bookshop.controller;

import com.pahana.edu.bookshop.dto.request.CustomerRequest;
import com.pahana.edu.bookshop.dto.response.ApiResponse;
import com.pahana.edu.bookshop.dto.response.CustomerResponse;
import com.pahana.edu.bookshop.dto.response.PagedResponse;
import com.pahana.edu.bookshop.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for customer management endpoints
 * 
 * Handles customer CRUD operations, search, and related functionality.
 */
@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Management", description = "Customer management endpoints")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Get all customers with pagination (ADMIN ONLY)
     */
    @GetMapping
    @Operation(summary = "Get all customers", description = "Get paginated list of all customers (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CustomerResponse> customers = customerService.getAllCustomers(pageable);
        PagedResponse<CustomerResponse> pagedResponse = PagedResponse.of(customers);

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get customer by ID (ADMIN ONLY)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Get customer details by ID (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    /**
     * Get customer by account number (ADMIN ONLY)
     */
    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get customer by account number", description = "Get customer details by account number (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByAccountNumber(
            @PathVariable String accountNumber) {
        CustomerResponse customer = customerService.getCustomerByAccountNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    /**
     * Create new customer (ADMIN ONLY)
     */
    @PostMapping
    @Operation(summary = "Create new customer", description = "Create a new customer account (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse customer = customerService.createCustomer(customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(customer, "Customer created successfully"));
    }

    /**
     * Update existing customer (ADMIN ONLY)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update existing customer details (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse customer = customerService.updateCustomer(id, customerRequest);
        return ResponseEntity.ok(ApiResponse.success(customer, "Customer updated successfully"));
    }

    /**
     * Delete customer (ADMIN ONLY)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete customer (only if no bills exist) (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }

    /**
     * Search customers (ADMIN ONLY)
     */
    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by name, account number, phone, or email (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerResponse>>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CustomerResponse> customers = customerService.searchCustomers(query, pageable);
        PagedResponse<CustomerResponse> pagedResponse = PagedResponse.of(customers);

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get customers for auto-complete (ADMIN ONLY)
     */
    @GetMapping("/autocomplete")
    @Operation(summary = "Customer auto-complete", description = "Get customers for auto-complete by name prefix (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getCustomersForAutoComplete(
            @RequestParam String prefix) {
        List<CustomerResponse> customers = customerService.getCustomersForAutoComplete(prefix);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get customers without bills (ADMIN ONLY)
     */
    @GetMapping("/without-bills")
    @Operation(summary = "Get customers without bills", description = "Get customers who haven't placed any bills (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getCustomersWithoutBills() {
        List<CustomerResponse> customers = customerService.getCustomersWithoutBills();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get total customer count (ADMIN ONLY)
     */
    @GetMapping("/count")
    @Operation(summary = "Get customer count", description = "Get total number of customers (ADMIN ONLY)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalCustomerCount() {
        long count = customerService.getTotalCustomerCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}