package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a Bank Account
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;  // SAVINGS, CHECKING, etc.

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "currency", length = 3)
    private String currency;     // USD, EUR, etc.

    @Column(name = "status", length = 20)
    private String status;       // ACTIVE, CLOSED, FROZEN

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    /**
     * Constructor with parameters
     */
    public Account(User user, String accountNumber, String accountType, BigDecimal balance) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = "USD";
        this.status = "ACTIVE";
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        updatedDate = new Date();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (currency == null) {
            currency = "USD";
        }
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }
}
