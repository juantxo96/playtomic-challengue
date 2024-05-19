package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.domain.wallet.WalletService;
import com.playtomic.tests.wallet.domain.wallet.Wallet;

import java.util.UUID;

public class GetWalletImp implements GetWallet {
    private final WalletService walletService;

    public GetWalletImp(WalletService walletService) {
        this.walletService = walletService;
    }

    public WalletDto execute(UUID id) {
        Wallet wallet = walletService.getWallet(id);
        return WalletDto.fromEntity(wallet);
    }
}
