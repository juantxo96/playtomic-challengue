package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.domain.wallet.WalletService;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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
