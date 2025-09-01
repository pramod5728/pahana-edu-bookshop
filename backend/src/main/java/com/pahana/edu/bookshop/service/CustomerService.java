package com.pahana.edu.bookshop.service;

import com.pahana.edu.bookshop.dto.request.CustomerRequest;
import com.pahana.edu.bookshop.dto.response.CustomerResponse;
import com.pahana.edu.bookshop.entity.Customer;
import com.pahana.edu.bookshop.exception.BusinessException;
import com.pahana.edu.bookshop.exception.ResourceNotFoundException;
import com.pahana.edu.bookshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for Customer business logic
 * 
 * Handles customer management operations including CRUD operations,
 * validation, and business rule enforcement.
 */
@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Get all customers with pagination
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(this::convertToResponse);
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return convertToResponse(customer);
    }

    /**
     * Get customer by account number
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByAccountNumber(String accountNumber) {
        Customer customer = customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "accountNumber", accountNumber));
        return convertToResponse(customer);
    }

    /**
     * Create new customer
     */
    public CustomerResponse createCustomer(CustomerRequest request) {
        // Validate unique constraints
        validateUniqueConstraints(request.getPhone(), request.getEmail(), null);
        
        // Create customer entity
        Customer customer = Customer.builder()
                .accountNumber(generateAccountNumber())
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    /**
     * Update existing customer
     */
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Validate unique constraints (exclude current customer)
        validateUniqueConstraints(request.getPhone(), request.getEmail(), id);

        // Update fields
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Check if customer has bills
        if (!customer.getBills().isEmpty()) {
            throw new BusinessException("Cannot delete customer with existing bills. " +
                    "Customer has " + customer.getBills().size() + " bills.");
        }

        customerRepository.delete(customer);
    }

    /**
     * Search customers
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String searchTerm, Pageable pageable) {
        Page<Customer> customers = customerRepository.searchCustomers(searchTerm, pageable);
        return customers.map(this::convertToResponse);
    }

    /**
     * Get customers for auto-complete
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersForAutoComplete(String prefix) {
        List<Customer> customers = customerRepository.findByNameStartingWithIgnoreCase(prefix);
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get customers without bills
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithoutBills() {
        List<Customer> customers = customerRepository.findCustomersWithoutBills();
        return customers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get total customer count
     */
    @Transactional(readOnly = true)
    public long getTotalCustomerCount() {
        return customerRepository.getTotalCustomerCount();
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        String accountNumber;
        do {
            // Generate account number format: CUST-YYYYMMDD-XXXX
            String datePart = java.time.LocalDate.now().toString().replace("-", "");
            String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            accountNumber = "CUST-" + datePart + "-" + randomPart;
        } while (customerRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }

    /**
     * Validate unique constraints
     */
    private void validateUniqueConstraints(String phone, String email, Long excludeId) {
        // Check phone number uniqueness
        if (phone != null && customerRepository.existsByPhone(phone)) {
            if (excludeId == null || !customerRepository.findByPhone(phone)
                    .map(Customer::getId).equals(excludeId)) {
                throw new BusinessException("Phone number already exists: " + phone);
            }
        }

        // Check email uniqueness
        if (email != null && customerRepository.existsByEmail(email)) {
            if (excludeId == null || !customerRepository.findByEmail(email)
                    .map(Customer::getId).equals(excludeId)) {
                throw new BusinessException("Email already exists: " + email);
            }
        }
    }

    /**
     * Convert Customer entity to CustomerResponse DTO
     */
    private CustomerResponse convertToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .accountNumber(customer.getAccountNumber())
                .name(customer.getName())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .totalBills(customer.getBills().size())
                .displayName(customer.getDisplayName())
                .build();
    }
}