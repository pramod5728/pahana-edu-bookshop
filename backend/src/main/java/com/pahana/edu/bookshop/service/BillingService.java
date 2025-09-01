package com.pahana.edu.bookshop.service;

import com.pahana.edu.bookshop.dto.request.BillRequest;
import com.pahana.edu.bookshop.dto.response.BillResponse;
import com.pahana.edu.bookshop.dto.response.CustomerResponse;
import com.pahana.edu.bookshop.dto.response.ItemResponse;
import com.pahana.edu.bookshop.entity.*;
import com.pahana.edu.bookshop.enums.BillStatus;
import com.pahana.edu.bookshop.exception.BusinessException;
import com.pahana.edu.bookshop.exception.ResourceNotFoundException;
import com.pahana.edu.bookshop.repository.BillRepository;
import com.pahana.edu.bookshop.repository.CustomerRepository;
import com.pahana.edu.bookshop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Bill business logic
 * 
 * Handles billing operations including bill creation, payment processing,
 * invoice generation, and business rule enforcement.
 */
@Service
@Transactional
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Get all bills with pagination
     */
    @Transactional(readOnly = true)
    public Page<BillResponse> getAllBills(Pageable pageable) {
        Page<Bill> bills = billRepository.findAll(pageable);
        return bills.map(this::convertToResponse);
    }

    /**
     * Get bill by ID
     */
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return convertToResponse(bill);
    }

    /**
     * Get bill by bill number
     */
    @Transactional(readOnly = true)
    public BillResponse getBillByBillNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "billNumber", billNumber));
        return convertToResponse(bill);
    }

    /**
     * Get bills by customer ID
     */
    @Transactional(readOnly = true)
    public Page<BillResponse> getBillsByCustomerId(Long customerId, Pageable pageable) {
        Page<Bill> bills = billRepository.findByCustomerId(customerId, pageable);
        return bills.map(this::convertToResponse);
    }

    /**
     * Create new bill
     */
    public BillResponse createBill(BillRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        // Validate bill items and check stock availability
        validateBillItems(request.getItems());

        // Create bill entity
        Bill bill = Bill.builder()
                .billNumber(generateBillNumber())
                .customer(customer)
                .billDate(LocalDateTime.now())
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .status(BillStatus.PENDING)
                .notes(request.getNotes())
                .billItems(new ArrayList<>())
                .build();

        // Create bill items
        for (BillRequest.BillItemRequest itemRequest : request.getItems()) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemRequest.getItemId()));

            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .item(item)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(item.getPrice())
                    .discountPercentage(
                            itemRequest.getDiscountPercentage() != null ? itemRequest.getDiscountPercentage()
                                    : BigDecimal.ZERO)
                    .build();

            billItem.calculateTotalPrice();
            bill.addBillItem(billItem);

            // Reduce item stock
            item.reduceStock(itemRequest.getQuantity());
            itemRepository.save(item);
        }

        // Calculate bill totals
        bill.recalculateAmounts();

        Bill savedBill = billRepository.save(bill);
        return convertToResponse(savedBill);
    }

    /**
     * Update existing bill
     */
    public BillResponse updateBill(Long id, BillRequest request) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));

        if (!bill.canBeModified()) {
            throw new BusinessException("Bill cannot be modified in current status: " + bill.getStatus());
        }

        // Restore stock for existing items
        restoreStockForBillItems(bill.getBillItems());

        // Clear existing bill items
        bill.getBillItems().clear();

        // Validate new bill items
        validateBillItems(request.getItems());

        // Add new bill items
        for (BillRequest.BillItemRequest itemRequest : request.getItems()) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemRequest.getItemId()));

            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .item(item)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(item.getPrice())
                    .discountPercentage(
                            itemRequest.getDiscountPercentage() != null ? itemRequest.getDiscountPercentage()
                                    : BigDecimal.ZERO)
                    .build();

            billItem.calculateTotalPrice();
            bill.addBillItem(billItem);

            // Reduce item stock
            item.reduceStock(itemRequest.getQuantity());
            itemRepository.save(item);
        }

        // Update bill details
        bill.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        bill.setNotes(request.getNotes());
        bill.recalculateAmounts();

        Bill savedBill = billRepository.save(bill);
        return convertToResponse(savedBill);
    }

    /**
     * Mark bill as paid
     */
    public BillResponse markBillAsPaid(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessException("Bill is already paid");
        }

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BusinessException("Cannot mark cancelled bill as paid");
        }

        bill.markAsPaid();
        Bill savedBill = billRepository.save(bill);
        return convertToResponse(savedBill);
    }

    /**
     * Cancel bill
     */
    public BillResponse cancelBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessException("Cannot cancel paid bill");
        }

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BusinessException("Bill is already cancelled");
        }

        // Restore stock for all bill items
        restoreStockForBillItems(bill.getBillItems());

        bill.markAsCancelled();
        Bill savedBill = billRepository.save(bill);
        return convertToResponse(savedBill);
    }

    /**
     * Search bills
     */
    @Transactional(readOnly = true)
    public Page<BillResponse> searchBills(String searchTerm, Pageable pageable) {
        Page<Bill> bills = billRepository.searchBills(searchTerm, pageable);
        return bills.map(this::convertToResponse);
    }

    /**
     * Get bills by status
     */
    @Transactional(readOnly = true)
    public Page<BillResponse> getBillsByStatus(BillStatus status, Pageable pageable) {
        Page<Bill> bills = billRepository.findByStatus(status, pageable);
        return bills.map(this::convertToResponse);
    }

    /**
     * Get bills between dates
     */
    @Transactional(readOnly = true)
    public Page<BillResponse> getBillsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Bill> bills = billRepository.findBillsBetweenDates(startDate, endDate, pageable);
        return bills.map(this::convertToResponse);
    }

    /**
     * Get today's bills
     */
    @Transactional(readOnly = true)
    public List<BillResponse> getTodaysBills() {
        List<Bill> bills = billRepository.findTodaysBills();
        return bills.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get overdue bills
     */
    @Transactional(readOnly = true)
    public List<BillResponse> getOverdueBills(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Bill> bills = billRepository.findOverdueBills(cutoffDate);
        return bills.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get total sales amount for date range
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesAmount(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = billRepository.getTotalSalesAmount(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get average bill amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getAverageBillAmount() {
        BigDecimal average = billRepository.getAverageBillAmount();
        return average != null ? average : BigDecimal.ZERO;
    }

    /**
     * Generate unique bill number
     */
    private String generateBillNumber() {
        Integer lastSequence = billRepository.getLastBillSequence();
        int nextSequence = (lastSequence != null ? lastSequence : 0) + 1;
        return String.format("BILL%06d", nextSequence);
    }

    /**
     * Validate bill items
     */
    private void validateBillItems(List<BillRequest.BillItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("Bill must contain at least one item");
        }

        for (BillRequest.BillItemRequest itemRequest : items) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemRequest.getItemId()));

            if (!item.getActive()) {
                throw new BusinessException("Item is not active: " + item.getName());
            }

            if (!item.hasAvailableQuantity(itemRequest.getQuantity())) {
                throw new BusinessException("Insufficient stock for item: " + item.getName() +
                        ". Available: " + item.getStockQuantity() + ", Requested: " + itemRequest.getQuantity());
            }
        }
    }

    /**
     * Restore stock for bill items
     */
    private void restoreStockForBillItems(List<BillItem> billItems) {
        for (BillItem billItem : billItems) {
            Item item = billItem.getItem();
            item.increaseStock(billItem.getQuantity());
            itemRepository.save(item);
        }
    }

    /**
     * Convert Bill entity to BillResponse DTO
     */
    private BillResponse convertToResponse(Bill bill) {
        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(bill.getCustomer().getId())
                .accountNumber(bill.getCustomer().getAccountNumber())
                .name(bill.getCustomer().getName())
                .displayName(bill.getCustomer().getDisplayName())
                .build();

        List<BillResponse.BillItemResponse> billItemResponses = bill.getBillItems().stream()
                .map(this::convertBillItemToResponse)
                .toList();

        return BillResponse.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .customer(customerResponse)
                .billDate(bill.getBillDate())
                .subtotal(bill.getSubtotal())
                .taxRate(bill.getTaxRate())
                .taxAmount(bill.getTaxAmount())
                .discountAmount(bill.getDiscountAmount())
                .totalAmount(bill.getTotalAmount())
                .status(bill.getStatus())
                .notes(bill.getNotes())
                .createdAt(bill.getCreatedAt())
                .billItems(billItemResponses)
                .displayNumber(bill.getDisplayNumber())
                .totalQuantity(bill.getTotalQuantity())
                .build();
    }

    /**
     * Convert BillItem entity to BillItemResponse DTO
     */
    private BillResponse.BillItemResponse convertBillItemToResponse(BillItem billItem) {
        ItemResponse itemResponse = ItemResponse.builder()
                .id(billItem.getItem().getId())
                .itemCode(billItem.getItem().getItemCode())
                .name(billItem.getItem().getName())
                .displayName(billItem.getItem().getDisplayName())
                .build();

        return BillResponse.BillItemResponse.builder()
                .id(billItem.getId())
                .item(itemResponse)
                .quantity(billItem.getQuantity())
                .unitPrice(billItem.getUnitPrice())
                .discountPercentage(billItem.getDiscountPercentage())
                .discountAmount(billItem.getDiscountAmount())
                .totalPrice(billItem.getTotalPrice())
                .build();
    }
}