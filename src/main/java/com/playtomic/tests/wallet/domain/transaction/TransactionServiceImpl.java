package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    public Transaction createPayment(Wallet wallet, BigDecimal amount) {
        Transaction transaction = new Transaction(wallet, amount, TransactionType.PAYMENT, TransactionStatus.PENDING);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void confirmPayment(Wallet wallet, Transaction transaction, String paymentId) {
        transaction.confirm(paymentId);
        walletService.deposit(wallet, transaction.getAmount());

        transactionRepository.save(transaction);
    }

    @Transactional
    public void cancelPayment(Transaction transaction) {
        transaction.cancel();
        transactionRepository.save(transaction);
    }
}
