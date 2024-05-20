package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.wallet.Wallet;

import java.math.BigDecimal;

public interface TransactionService {
    Transaction createPayment(Wallet wallet, BigDecimal amount);
    void confirmPayment(Wallet wallet, Transaction transaction, String paymentId);
    void cancelPayment(Transaction transaction);
}
