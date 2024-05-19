package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String paymentId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @ManyToOne
    private Wallet wallet;

    public Transaction(Wallet wallet, BigDecimal amount, TransactionType type, TransactionStatus status) {
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.wallet = wallet;
    }

    public void confirm(String paymentId) {
        this.paymentId = paymentId;
        status = TransactionStatus.COMPLETED;
    }

    public void cancel() {
        status = TransactionStatus.CANCELLED;
    }

    @PrePersist
    protected void onCreate() {createdAt = ZonedDateTime.now(ZoneOffset.UTC);}
    @PreUpdate
    protected void preUpdate() {updatedAt = ZonedDateTime.now(ZoneOffset.UTC);}
}
