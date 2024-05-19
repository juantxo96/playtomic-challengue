package com.playtomic.tests.wallet.infrastructure.persistence;

import com.playtomic.tests.wallet.domain.transaction.Transaction;
import com.playtomic.tests.wallet.domain.transaction.TransactionStatus;
import com.playtomic.tests.wallet.domain.transaction.TransactionType;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.UUID;

@DataJpaTest
@Transactional
@Rollback
public class JpaTransactionRepositoryTest {
    @Autowired
    private JpaTransactionRepository transactionRepository;

    @Test
    public void should_save_transaction() {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);
        BigDecimal amount = new BigDecimal("100.00");
        TransactionType transactionType = TransactionType.PAYMENT;
        TransactionStatus transactionStatus = TransactionStatus.PENDING;

        Transaction transaction = new Transaction(wallet, amount, transactionType, transactionStatus);
        Transaction savedTransaction = transactionRepository.save(transaction);


        Assertions.assertNotNull(savedTransaction.getId());
    }
}
