package com.pahana.edu.bookshop.enums;

/**
 * Enumeration for bill status values
 * 
 * Represents the different states a bill can be in throughout its lifecycle.
 */
public enum BillStatus {
    /**
     * Bill has been created but not yet finalized
     */
    DRAFT("Draft"),
    
    /**
     * Bill is pending payment
     */
    PENDING("Pending"),
    
    /**
     * Bill has been paid
     */
    PAID("Paid"),
    
    /**
     * Bill has been cancelled
     */
    CANCELLED("Cancelled"),
    
    /**
     * Bill is partially paid
     */
    PARTIAL_PAID("Partially Paid"),
    
    /**
     * Bill is overdue
     */
    OVERDUE("Overdue");

    private final String displayName;

    BillStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the bill status represents a finalized state
     */
    public boolean isFinal() {
        return this == PAID || this == CANCELLED;
    }

    /**
     * Check if the bill can be modified
     */
    public boolean isModifiable() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * Check if the bill requires payment
     */
    public boolean requiresPayment() {
        return this == PENDING || this == PARTIAL_PAID || this == OVERDUE;
    }
}