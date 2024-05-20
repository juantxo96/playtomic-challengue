package com.playtomic.tests.wallet.application.wallet.getWallet;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class WalletDto {
    private UUID id;
    private BigDecimal balance;
    private UUID userId;
    private String currencyCode;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public static WalletDto fromEntity(Wallet wallet) {
        WalletDto dto = new WalletDto();
        dto.setId(wallet.getId());
        dto.setBalance(wallet.getBalance());
        dto.setUserId(wallet.getUserId());
        dto.setCurrencyCode(wallet.getCurrencyCode());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }
}
