package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentRequestDto;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentResponseDto;
import com.playtomic.tests.wallet.domain.payment.Payment;
import com.playtomic.tests.wallet.domain.payment.PaymentService;
import com.playtomic.tests.wallet.domain.transaction.Transaction;
import com.playtomic.tests.wallet.domain.transaction.TransactionService;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProcessPaymentImp implements ProcessPayment {
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final RetryTemplate retryTemplate;

    public ProcessPaymentImp(TransactionService transactionService,
                             WalletService walletService,
                             PaymentService paymentService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.paymentService = paymentService;
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(100);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        this.retryTemplate = retryTemplate;
    }

    public ProcessPaymentResponseDto execute(UUID walletId, ProcessPaymentRequestDto processPaymentRequestDto) {
        Wallet wallet = walletService.getWallet(walletId);
        Transaction transaction = transactionService.createPayment(wallet, processPaymentRequestDto.getAmount());

        Payment payment = performCharge(processPaymentRequestDto.getCreditCardNumber(), transaction);

        /*
         * This last step should be done asynchronously with a message queue
         * The idea behind it is that once we have charged the user, we should always save those changes in our database
         * For optimistic concurrency issues we can use a retry logic as implemented.
         * But for other kind of database issues like having it in a downtime or something like that, that mechanism
         * will help us retry them after the downtime automatically.
         */

        confirmPayment(transaction, payment);

        return ProcessPaymentResponseDto.fromEntity(transaction);
    }

    private Payment performCharge(String creditCard, Transaction transaction) {
        try {
            return paymentService.processPayment(creditCard, transaction.getAmount());
        }
        catch (Exception e) {
            /*
             * A metric should be added here(with something like prometheus) to metricate payment issues and trigger an alert in case of X issues
             * Also a log should be added to be able to identify the issue
             */
            transactionService.cancelPayment(transaction);
            throw e;
        }
    }

    private void confirmPayment(Transaction transaction, Payment payment) throws OptimisticLockException {
        RetryCallback<Boolean, OptimisticLockException> retryCallback = retryContext -> {
            Wallet wallet = transaction.getWallet();
            if(retryContext.getRetryCount() > 0) {
                wallet = walletService.getWallet(transaction.getWallet().getId());
            }

            transactionService.confirmPayment(wallet, transaction, payment.getId());
            return true;
        };

        retryTemplate.execute(retryCallback);
    }
}
