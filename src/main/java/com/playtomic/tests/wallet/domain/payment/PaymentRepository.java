package com.playtomic.tests.wallet.domain.payment;

import java.math.BigDecimal;

public interface PaymentRepository {
    Payment charge(String creditCardNumber, BigDecimal amount);
}
