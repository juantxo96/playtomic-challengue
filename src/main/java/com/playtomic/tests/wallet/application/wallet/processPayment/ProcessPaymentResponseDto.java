package com.playtomic.tests.wallet.application.wallet.processPayment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playtomic.tests.wallet.domain.transaction.Transaction;
import lombok.Data;

import java.util.UUID;

@Data
public class ProcessPaymentResponseDto {
    @JsonProperty("wallet_id")
    private UUID walletId;
    @JsonProperty("transaction_id")
    private UUID transactionId;
    @JsonProperty("transaction_status")
    private String transactionStatus;

    public static ProcessPaymentResponseDto fromEntity(Transaction transaction) {
        ProcessPaymentResponseDto dto = new ProcessPaymentResponseDto();
        dto.walletId = transaction.getWallet().getId();
        dto.transactionId = transaction.getId();
        dto.transactionStatus = transaction.getStatus().name();
        return dto;
    }
}
