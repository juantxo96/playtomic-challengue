package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentRequestDto;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentResponseDto;
import com.playtomic.tests.wallet.domain.payment.Payment;
import com.playtomic.tests.wallet.domain.payment.PaymentService;
import com.playtomic.tests.wallet.domain.transaction.Transaction;
import com.playtomic.tests.wallet.domain.transaction.TransactionService;
import com.playtomic.tests.wallet.domain.transaction.TransactionStatus;
import com.playtomic.tests.wallet.domain.transaction.TransactionType;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletService;
import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.infrastructure.external.stripe.StripeServiceException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ProcessPaymentTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ProcessPaymentImp processPayment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_process_payment() {
        UUID walletId = UUID.randomUUID();
        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("4242", new BigDecimal("100.00"));
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);
        Transaction transaction = new Transaction(wallet, new BigDecimal("100.00"), TransactionType.PAYMENT, TransactionStatus.PENDING);
        Payment payment = new Payment(UUID.randomUUID().toString());

        when(walletService.getWallet(walletId)).thenReturn(wallet);
        when(transactionService.createPayment(wallet, new BigDecimal("100.00"))).thenReturn(transaction);
        when(paymentService.processPayment(processPaymentRequestDto.getCreditCardNumber(), transaction.getAmount())).thenReturn(payment);
        doAnswer(invocation -> {
            transaction.confirm(payment.getId());
            return null;
        }).when(transactionService).confirmPayment(wallet, transaction, payment.getId());

        ProcessPaymentResponseDto response = processPayment.execute(walletId, processPaymentRequestDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getTransactionId(), transaction.getId());
        Assertions.assertEquals(response.getTransactionStatus(), TransactionStatus.COMPLETED.name());
        Assertions.assertEquals(response.getWalletId(), wallet.getId());
    }

    @Test
    public void should_throw_exception_when_wallet_not_found() {
        UUID walletId = UUID.randomUUID();
        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("4242", new BigDecimal("100.00"));

        when(walletService.getWallet(walletId)).thenThrow(new WalletNotFoundException());

        Assertions.assertThrows(WalletNotFoundException.class, () -> processPayment.execute(walletId, processPaymentRequestDto));
    }

    @Test
    public void should_cancel_transaction_and_throw_exception_when_payment_fails() {
        UUID walletId = UUID.randomUUID();
        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("4242", new BigDecimal("100.00"));
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);
        Transaction transaction = new Transaction(wallet, new BigDecimal("100.00"), TransactionType.PAYMENT, TransactionStatus.PENDING);

        when(walletService.getWallet(walletId)).thenReturn(wallet);
        when(transactionService.createPayment(wallet, new BigDecimal("100.00"))).thenReturn(transaction);
        when(paymentService.processPayment(processPaymentRequestDto.getCreditCardNumber(), transaction.getAmount())).thenThrow(new StripeServiceException());

        Assertions.assertThrows(StripeServiceException.class, () -> processPayment.execute(walletId, processPaymentRequestDto));
    }

    @Test
    public void should_process_transaction_when_first_iteration_throws_concurrency_exception() {
        UUID walletId = UUID.randomUUID();
        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("4242", new BigDecimal("100.00"));
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);
        Transaction transaction = new Transaction(wallet, new BigDecimal("100.00"), TransactionType.PAYMENT, TransactionStatus.PENDING);
        Payment payment = new Payment(UUID.randomUUID().toString());

        when(walletService.getWallet(any())).thenReturn(wallet);
        when(transactionService.createPayment(wallet, new BigDecimal("100.00"))).thenReturn(transaction);
        when(paymentService.processPayment(processPaymentRequestDto.getCreditCardNumber(), transaction.getAmount())).thenReturn(payment);
        doThrow(new OptimisticLockException()).doAnswer(invocation -> {
            transaction.confirm(payment.getId());
            return null;
        }).when(transactionService).confirmPayment(wallet, transaction, payment.getId());

        ProcessPaymentResponseDto response = processPayment.execute(walletId, processPaymentRequestDto);

        verify(transactionService, times(2)).confirmPayment(any(Wallet.class), any(Transaction.class), any(String.class));
        verify(walletService, times(2)).getWallet(any());
    }
}
