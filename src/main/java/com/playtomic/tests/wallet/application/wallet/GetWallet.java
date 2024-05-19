package com.playtomic.tests.wallet.application.wallet;

import java.util.UUID;

public interface GetWallet {
    WalletDto execute(UUID id);
}
