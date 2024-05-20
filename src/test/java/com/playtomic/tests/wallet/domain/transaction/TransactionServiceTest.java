package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@Transactional
@Rollback
public class TransactionServiceTest {
    @Autowired
    private TransactionServiceImpl transactionService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        wallet = new Wallet(userId, currency);

        walletRepository.save(wallet);
    }

    @Test
    public void should_create_payment_transaction() {
        Transaction transaction = transactionService.createPayment(wallet, BigDecimal.TEN);
        Assertions.assertNotNull(transaction);
        Assertions.assertEquals(transaction.getWallet(), wallet);
        Assertions.assertEquals(transaction.getAmount(), BigDecimal.TEN);
        Assertions.assertEquals(transaction.getType(), TransactionType.PAYMENT);
        Assertions.assertEquals(transaction.getStatus(), TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).get();
        Assertions.assertNotNull(savedTransaction);
        Assertions.assertEquals(savedTransaction, transaction);
    }

    @Test
    public void should_confirm_payment_transaction() {
        Transaction transaction = transactionService.createPayment(wallet, BigDecimal.TEN);

        String paymentId = UUID.randomUUID().toString();
        transactionService.confirmPayment(transaction.getWallet(), transaction, paymentId);

        Assertions.assertEquals(transaction.getPaymentId(), paymentId);
        Assertions.assertEquals(transaction.getStatus(), TransactionStatus.COMPLETED);

        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).get();
        Assertions.assertNotNull(savedTransaction);
        Assertions.assertEquals(savedTransaction, transaction);
    }

    @Test
    public void should_cancel_payment_transaction() {
        Transaction transaction = transactionService.createPayment(wallet, BigDecimal.TEN);

        transactionService.cancelPayment(transaction);

        Assertions.assertEquals(transaction.getStatus(), TransactionStatus.CANCELLED);

        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).get();

        Assertions.assertNotNull(savedTransaction);
        Assertions.assertEquals(savedTransaction, transaction);
    }
}
