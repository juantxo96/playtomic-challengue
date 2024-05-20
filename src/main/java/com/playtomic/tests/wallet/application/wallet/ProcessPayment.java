package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentRequestDto;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentResponseDto;

import java.util.UUID;

public interface ProcessPayment {
    ProcessPaymentResponseDto execute(UUID walletId, ProcessPaymentRequestDto processPaymentRequestDto);
}
