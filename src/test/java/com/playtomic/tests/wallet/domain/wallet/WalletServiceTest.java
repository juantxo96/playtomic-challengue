package com.playtomic.tests.wallet.domain.wallet;

import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImp walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_return_wallet() {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String currency = "EUR";

        Wallet mockWallet = new Wallet(userId, currency);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));

        Wallet wallet = walletService.getWallet(walletId);

        Assertions.assertEquals(wallet, mockWallet);
    }

    @Test
    public void should_throw_exception_when_wallet_not_found() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> walletService.getWallet(walletId));
    }
}
