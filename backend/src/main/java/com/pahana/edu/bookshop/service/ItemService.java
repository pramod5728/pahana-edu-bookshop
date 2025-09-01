package com.pahana.edu.bookshop.service;

import com.pahana.edu.bookshop.dto.request.ItemRequest;
import com.pahana.edu.bookshop.dto.response.ItemResponse;
import com.pahana.edu.bookshop.entity.Item;
import com.pahana.edu.bookshop.exception.BusinessException;
import com.pahana.edu.bookshop.exception.ResourceNotFoundException;
import com.pahana.edu.bookshop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for Item business logic
 * 
 * Handles inventory management operations including CRUD operations,
 * stock management, and business rule enforcement.
 */
@Service
@Transactional
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Get all items with pagination
     */
    @Transactional(readOnly = true)
    public Page<ItemResponse> getAllItems(Pageable pageable) {
        Page<Item> items = itemRepository.findAll(pageable);
        return items.map(this::convertToResponse);
    }

    /**
     * Get active items with pagination
     */
    @Transactional(readOnly = true)
    public Page<ItemResponse> getActiveItems(Pageable pageable) {
        Page<Item> items = itemRepository.findByActiveTrue(pageable);
        return items.map(this::convertToResponse);
    }

    /**
     * Get item by ID
     */
    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        return convertToResponse(item);
    }

    /**
     * Get item by item code
     */
    @Transactional(readOnly = true)
    public ItemResponse getItemByItemCode(String itemCode) {
        Item item = itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "itemCode", itemCode));
        return convertToResponse(item);
    }

    /**
     * Create new item
     */
    public ItemResponse createItem(ItemRequest request) {
        // Validate unique item code
        if (itemRepository.existsByItemCode(request.getItemCode())) {
            throw new BusinessException("Item code already exists: " + request.getItemCode());
        }

        // Set default minimum stock level if not provided
        Integer minimumStockLevel = request.getMinimumStockLevel() != null ? 
                request.getMinimumStockLevel() : 10;

        // Create item entity
        Item item = Item.builder()
                .itemCode(request.getItemCode())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .minimumStockLevel(minimumStockLevel)
                .active(request.getActive())
                .build();

        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    /**
     * Update existing item
     */
    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        // Validate unique item code (exclude current item)
        if (!item.getItemCode().equals(request.getItemCode()) && 
            itemRepository.existsByItemCode(request.getItemCode())) {
            throw new BusinessException("Item code already exists: " + request.getItemCode());
        }

        // Update fields
        item.setItemCode(request.getItemCode());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStockQuantity(request.getStockQuantity());
        item.setCategory(request.getCategory());
        item.setMinimumStockLevel(request.getMinimumStockLevel());
        item.setActive(request.getActive());

        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    /**
     * Delete item (soft delete by setting active to false)
     */
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        // Soft delete - set active to false
        item.setActive(false);
        itemRepository.save(item);
    }

    /**
     * Update item stock quantity
     */
    public ItemResponse updateItemStock(Long id, int newQuantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        if (newQuantity < 0) {
            throw new BusinessException("Stock quantity cannot be negative");
        }

        item.setStockQuantity(newQuantity);
        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    /**
     * Increase item stock
     */
    public ItemResponse increaseItemStock(Long id, int quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        item.increaseStock(quantity);
        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    /**
     * Reduce item stock
     */
    public ItemResponse reduceItemStock(Long id, int quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        item.reduceStock(quantity);
        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    /**
     * Search items
     */
    @Transactional(readOnly = true)
    public Page<ItemResponse> searchItems(String searchTerm, Pageable pageable) {
        Page<Item> items = itemRepository.searchActiveItems(searchTerm, pageable);
        return items.map(this::convertToResponse);
    }

    /**
     * Get items by category
     */
    @Transactional(readOnly = true)
    public Page<ItemResponse> getItemsByCategory(String category, Pageable pageable) {
        Page<Item> items = itemRepository.findByCategoryAndActiveTrue(category, pageable);
        return items.map(this::convertToResponse);
    }

    /**
     * Get items in price range
     */
    @Transactional(readOnly = true)
    public Page<ItemResponse> getItemsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Item> items = itemRepository.findItemsInPriceRange(minPrice, maxPrice, pageable);
        return items.map(this::convertToResponse);
    }

    /**
     * Get low stock items
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> getLowStockItems() {
        List<Item> items = itemRepository.findLowStockItems();
        return items.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get out of stock items
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> getOutOfStockItems() {
        List<Item> items = itemRepository.findOutOfStockItems();
        return items.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return itemRepository.findAllCategories();
    }

    /**
     * Get items for auto-complete
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsForAutoComplete(String prefix) {
        List<Item> items = itemRepository.findByNameStartingWithIgnoreCase(prefix);
        return items.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get total inventory value
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        BigDecimal total = itemRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get items needing restock
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsNeedingRestock() {
        List<Item> items = itemRepository.findItemsNeedingRestock();
        return items.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Check item availability for quantity
     */
    @Transactional(readOnly = true)
    public boolean checkItemAvailability(Long itemId, int requestedQuantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));
        
        return item.isInStock() && item.hasAvailableQuantity(requestedQuantity);
    }

    /**
     * Convert Item entity to ItemResponse DTO
     */
    private ItemResponse convertToResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .itemCode(item.getItemCode())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .category(item.getCategory())
                .minimumStockLevel(item.getMinimumStockLevel())
                .active(item.getActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .displayName(item.getDisplayName())
                .isLowStock(item.isLowStock())
                .isInStock(item.isInStock())
                .build();
    }
}