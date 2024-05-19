package com.playtomic.tests.wallet.domain.wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    Wallet getWallet(UUID id);
    void deposit(Wallet wallet, BigDecimal amount);
}
