package com.playtomic.tests.wallet.domain.transaction;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
    public void confirmPayment(Transaction transaction, String paymentId) {
        transaction.confirm(paymentId);
        walletService.deposit(transaction.getWallet(), transaction.getAmount());

        transactionRepository.save(transaction);
    }
}
