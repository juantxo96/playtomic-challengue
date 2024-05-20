package com.playtomic.tests.wallet.application.wallet.processPayment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProcessPaymentRequestDto {
    @JsonProperty("credit_card_number")
    private String creditCardNumber;
    @JsonProperty("amount")
    private BigDecimal amount;
}
