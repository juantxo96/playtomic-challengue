package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.transaction.exception.TransactionAmountTooSmallException;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionTest {
   @Test
   public void should_generate_transaction() {
      UUID userId = UUID.randomUUID();
      String currency = "EUR";
      Wallet wallet = new Wallet(userId, currency);
      BigDecimal amount = new BigDecimal("100.00");
      TransactionType transactionType = TransactionType.PAYMENT;
      TransactionStatus transactionStatus = TransactionStatus.PENDING;

      Transaction transaction = new Transaction(wallet, amount, transactionType, transactionStatus);

      Assertions.assertEquals(transaction.getWallet(), wallet);
      Assertions.assertEquals(transaction.getAmount(), amount);
      Assertions.assertEquals(transaction.getType(), transactionType);
      Assertions.assertEquals(transaction.getStatus(), transactionStatus);
   }

   @Test
   public void should_return_exception_when_amount_is_lower_than_allowed() {
      UUID userId = UUID.randomUUID();
      String currency = "EUR";
      Wallet wallet = new Wallet(userId, currency);
      BigDecimal amount = new BigDecimal("0");
      TransactionType transactionType = TransactionType.PAYMENT;
      TransactionStatus transactionStatus = TransactionStatus.PENDING;

      Assertions.assertThrows(TransactionAmountTooSmallException.class, () -> new Transaction(wallet, amount, transactionType, transactionStatus));
   }

   @Test
   public void should_confirm_transaction() {
      UUID userId = UUID.randomUUID();
      String currency = "EUR";
      Wallet wallet = new Wallet(userId, currency);
      BigDecimal amount = new BigDecimal("100.00");
      TransactionType transactionType = TransactionType.PAYMENT;
      TransactionStatus transactionStatus = TransactionStatus.PENDING;
      String paymentId = UUID.randomUUID().toString();

      Transaction transaction = new Transaction(wallet, amount, transactionType, transactionStatus);

      transaction.confirm(paymentId);

      Assertions.assertEquals(transaction.getPaymentId(), paymentId);
      Assertions.assertEquals(transaction.getStatus(), TransactionStatus.COMPLETED);
   }

   @Test
   public void should_cancel_transaction() {
      UUID userId = UUID.randomUUID();
      String currency = "EUR";
      Wallet wallet = new Wallet(userId, currency);
      BigDecimal amount = new BigDecimal("100.00");
      TransactionType transactionType = TransactionType.PAYMENT;
      TransactionStatus transactionStatus = TransactionStatus.PENDING;

      Transaction transaction = new Transaction(wallet, amount, transactionType, transactionStatus);

      transaction.cancel();

      Assertions.assertEquals(transaction.getStatus(), TransactionStatus.CANCELLED);
   }
}
