package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.application.wallet.getWallet.WalletDto;

import java.util.UUID;

public interface GetWallet {
    WalletDto execute(UUID id);
}
