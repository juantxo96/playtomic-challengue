package com.playtomic.tests.wallet.domain.wallet;

import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImp implements WalletService {
    private final WalletRepository walletRepository;

    public WalletServiceImp(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWallet(UUID id) {
        /*
         * Implementing a cache using a service like redis might be a good idea to be able to reduce the calls to the database
         * For this we should add a logic to update the redis cache on every wallet update
         */
        return walletRepository.findById(id).orElseThrow(WalletNotFoundException::new);
    }

    public void deposit(Wallet wallet, BigDecimal amount) {
        wallet.deposit(amount);
        walletRepository.save(wallet);
    }
}
