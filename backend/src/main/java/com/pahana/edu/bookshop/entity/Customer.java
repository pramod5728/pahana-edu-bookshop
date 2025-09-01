package com.pahana.edu.bookshop.entity;

import com.pahana.edu.bookshop.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer entity representing bookshop customers
 * 
 * This entity stores customer information including account details,
 * contact information, and maintains relationships with bills.
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_account_number", columnList = "account_number"),
    @Index(name = "idx_customer_name", columnList = "name"),
    @Index(name = "idx_customer_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "bills")
@EqualsAndHashCode(callSuper = false, exclude = "bills")
public class Customer extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must not exceed 20 characters")
    private String accountNumber;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    private String address;

    @Column(name = "phone", nullable = false, length = 15)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)[0-9]{9}$", message = "Invalid Sri Lankan phone number format")
    private String phone;

    @Column(name = "email", length = 100)
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bill> bills = new ArrayList<>();

    /**
     * Helper method to add a bill to the customer
     */
    public void addBill(Bill bill) {
        bills.add(bill);
        bill.setCustomer(this);
    }

    /**
     * Helper method to remove a bill from the customer
     */
    public void removeBill(Bill bill) {
        bills.remove(bill);
        bill.setCustomer(null);
    }

    /**
     * Business method to get full display name with account number
     */
    public String getDisplayName() {
        return String.format("%s (%s)", name, accountNumber);
    }
}