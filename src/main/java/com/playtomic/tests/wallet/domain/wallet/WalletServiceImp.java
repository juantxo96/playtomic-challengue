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
        return walletRepository.findById(id).orElseThrow(WalletNotFoundException::new);
    }

    public void deposit(Wallet wallet, BigDecimal amount) {
        wallet.deposit(amount);
        walletRepository.save(wallet);
    }
}
