package com.playtomic.tests.wallet.application.wallet;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.when;

public class GetWalletTest {
    @Mock
    private WalletService walletService;

    @InjectMocks
    private GetWalletImp getWallet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_return_wallet_dto() {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String currency = "EUR";

        Wallet mockWallet = new Wallet(userId, currency);
        when(walletService.getWallet(walletId)).thenReturn(mockWallet);

        WalletDto walletDto = getWallet.execute(walletId);
        Assertions.assertNotNull(walletDto);
        Assertions.assertEquals(userId, walletDto.getUserId());
        Assertions.assertEquals(currency, walletDto.getCurrencyCode());
    }
}
