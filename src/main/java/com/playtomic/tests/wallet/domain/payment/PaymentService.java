package com.playtomic.tests.wallet.domain.payment;

import java.math.BigDecimal;

public interface PaymentService {
    Payment processPayment(String creditCard, BigDecimal amount);
}
