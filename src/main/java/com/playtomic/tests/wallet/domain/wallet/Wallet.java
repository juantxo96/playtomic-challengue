package com.playtomic.tests.wallet.domain.wallet;

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
public class Wallet {
    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal balance;
    private UUID userId;
    private String currencyCode;

    @Version
    private Long version;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Wallet(UUID userId, String currencyCode) {
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.balance = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

}
