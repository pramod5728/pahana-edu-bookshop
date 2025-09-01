package com.pahana.edu.bookshop.controller;

import com.pahana.edu.bookshop.dto.request.ItemRequest;
import com.pahana.edu.bookshop.dto.response.ApiResponse;
import com.pahana.edu.bookshop.dto.response.ItemResponse;
import com.pahana.edu.bookshop.dto.response.PagedResponse;
import com.pahana.edu.bookshop.service.ItemService;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for inventory/item management endpoints
 * 
 * Handles item CRUD operations, stock management, and related functionality.
 */
@RestController
@RequestMapping("/items")
@Tag(name = "Inventory Management", description = "Item and inventory management endpoints")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * Get all items with pagination
     */
    @GetMapping
    @Operation(summary = "Get all items", description = "Get paginated list of all items")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponse>>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ItemResponse> items = activeOnly ? 
                itemService.getActiveItems(pageable) : 
                itemService.getAllItems(pageable);
        
        PagedResponse<ItemResponse> pagedResponse = PagedResponse.of(items);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get item by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Get item details by ID")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * Get item by item code
     */
    @GetMapping("/code/{itemCode}")
    @Operation(summary = "Get item by code", description = "Get item details by item code")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemByItemCode(@PathVariable String itemCode) {
        ItemResponse item = itemService.getItemByItemCode(itemCode);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * Create new item
     */
    @PostMapping
    @Operation(summary = "Create new item", description = "Create a new inventory item")
    @PreAuthorize("hasAuthority('PERM_ITEM_WRITE')")
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(@Valid @RequestBody ItemRequest itemRequest) {
        ItemResponse item = itemService.createItem(itemRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(item, "Item created successfully"));
    }

    /**
     * Update existing item
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Update existing item details")
    @PreAuthorize("hasAuthority('PERM_ITEM_WRITE')")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest itemRequest) {
        ItemResponse item = itemService.updateItem(id, itemRequest);
        return ResponseEntity.ok(ApiResponse.success(item, "Item updated successfully"));
    }

    /**
     * Delete item (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Delete item (soft delete - sets active to false)")
    @PreAuthorize("hasAuthority('PERM_ITEM_DELETE')")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully"));
    }

    /**
     * Update item stock quantity
     */
    @PutMapping("/{id}/stock")
    @Operation(summary = "Update stock quantity", description = "Update item stock quantity")
    @PreAuthorize("hasAuthority('PERM_ITEM_WRITE')")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItemStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        ItemResponse item = itemService.updateItemStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(item, "Stock quantity updated successfully"));
    }

    /**
     * Increase item stock
     */
    @PostMapping("/{id}/stock/increase")
    @Operation(summary = "Increase stock", description = "Increase item stock quantity")
    @PreAuthorize("hasAuthority('PERM_ITEM_WRITE')")
    public ResponseEntity<ApiResponse<ItemResponse>> increaseItemStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        ItemResponse item = itemService.increaseItemStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(item, "Stock increased successfully"));
    }

    /**
     * Reduce item stock
     */
    @PostMapping("/{id}/stock/reduce")
    @Operation(summary = "Reduce stock", description = "Reduce item stock quantity")
    @PreAuthorize("hasAuthority('PERM_ITEM_WRITE')")
    public ResponseEntity<ApiResponse<ItemResponse>> reduceItemStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        ItemResponse item = itemService.reduceItemStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(item, "Stock reduced successfully"));
    }

    /**
     * Search items
     */
    @GetMapping("/search")
    @Operation(summary = "Search items", description = "Search items by name, code, description, or category")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponse>>> searchItems(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ItemResponse> items = itemService.searchItems(query, pageable);
        PagedResponse<ItemResponse> pagedResponse = PagedResponse.of(items);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get items by category
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get items by category", description = "Get items filtered by category")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponse>>> getItemsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ItemResponse> items = itemService.getItemsByCategory(category, pageable);
        PagedResponse<ItemResponse> pagedResponse = PagedResponse.of(items);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get items in price range
     */
    @GetMapping("/price-range")
    @Operation(summary = "Get items by price range", description = "Get items within specified price range")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponse>>> getItemsInPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ItemResponse> items = itemService.getItemsInPriceRange(minPrice, maxPrice, pageable);
        PagedResponse<ItemResponse> pagedResponse = PagedResponse.of(items);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Get low stock items
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items", description = "Get items with stock below minimum level")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getLowStockItems() {
        List<ItemResponse> items = itemService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * Get out of stock items
     */
    @GetMapping("/out-of-stock")
    @Operation(summary = "Get out of stock items", description = "Get items with zero stock quantity")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getOutOfStockItems() {
        List<ItemResponse> items = itemService.getOutOfStockItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Get list of all item categories")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = itemService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * Get items for auto-complete
     */
    @GetMapping("/autocomplete")
    @Operation(summary = "Item auto-complete", description = "Get items for auto-complete by name prefix")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItemsForAutoComplete(
            @RequestParam String prefix) {
        List<ItemResponse> items = itemService.getItemsForAutoComplete(prefix);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * Get total inventory value
     */
    @GetMapping("/inventory-value")
    @Operation(summary = "Get inventory value", description = "Get total value of all inventory items")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalInventoryValue() {
        BigDecimal value = itemService.getTotalInventoryValue();
        return ResponseEntity.ok(ApiResponse.success(value));
    }

    /**
     * Get items needing restock
     */
    @GetMapping("/restock-needed")
    @Operation(summary = "Get items needing restock", description = "Get items that need restocking")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getItemsNeedingRestock() {
        List<ItemResponse> items = itemService.getItemsNeedingRestock();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * Check item availability
     */
    @GetMapping("/{id}/availability")
    @Operation(summary = "Check item availability", description = "Check if item has sufficient quantity available")
    @PreAuthorize("hasAuthority('PERM_ITEM_READ')")
    public ResponseEntity<ApiResponse<Boolean>> checkItemAvailability(
            @PathVariable Long id,
            @RequestParam int quantity) {
        boolean available = itemService.checkItemAvailability(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(available));
    }
}