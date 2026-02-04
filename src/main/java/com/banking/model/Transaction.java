package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a Bank Transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_account_id", columnList = "account_id"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;  // DEPOSIT, WITHDRAWAL, TRANSFER

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 255)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Date timestamp;

    @Column(name = "status", length = 20)
    private String status;  // SUCCESS, PENDING, FAILED

    /**
     * Constructor with parameters
     */
    public Transaction(Account account, String transactionType, BigDecimal amount, String description) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.timestamp = new Date();
        this.status = "SUCCESS";
    }

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
        if (status == null) {
            status = "PENDING";
        }
    }
}
