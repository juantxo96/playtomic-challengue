package com.playtomic.tests.wallet.domain.wallet;

import java.util.UUID;

public interface WalletService {
    Wallet getWallet(UUID id);
}
