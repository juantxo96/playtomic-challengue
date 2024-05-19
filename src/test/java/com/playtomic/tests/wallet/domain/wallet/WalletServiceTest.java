package com.playtomic.tests.wallet.domain.wallet;

import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest
public class WalletServiceTest {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletServiceImp walletService;

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        wallet = new Wallet(userId, currency);

        walletRepository.save(wallet);
    }

    @Test
    @Transactional
    @Rollback
    public void should_return_wallet() {
        Wallet findWallet = walletService.getWallet(wallet.getId());

        Assertions.assertNotNull(findWallet);
        Assertions.assertEquals(wallet.getId(), findWallet.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void should_throw_exception_when_wallet_not_found() {
        UUID walletId = UUID.randomUUID();

        Assertions.assertThrows(WalletNotFoundException.class, () -> walletService.getWallet(walletId));
    }

    @Test
    @Transactional
    @Rollback
    public void should_deposit_amount_in_wallet() {
        walletService.deposit(wallet, BigDecimal.TEN);

        Assertions.assertEquals(wallet.getBalance(), BigDecimal.TEN);

        Wallet savedWallet = walletRepository.findById(wallet.getId()).get();
        Assertions.assertNotNull(savedWallet);
        Assertions.assertEquals(savedWallet.getBalance(), wallet.getBalance());
    }

    @Test
    public void should_return_error_if_wallet_optimistic_locking_fails() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Wallet wallet1 = walletRepository.findAll().get(0);
        Wallet wallet2 = walletRepository.findAll().get(0);

        Future<?> future1 = executor.submit(() -> walletService.deposit(wallet1, new BigDecimal("20")));
        Future<?> future2 = executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            walletService.deposit(wallet2, new BigDecimal("20"));});

        future1.get();
        Assertions.assertThrows(ExecutionException.class, () -> future2.get());

        Wallet finalWallet = walletService.getWallet(wallet.getId());
        Assertions.assertEquals(finalWallet.getBalance(), wallet1.getBalance());

        executor.shutdown();
    }
}
