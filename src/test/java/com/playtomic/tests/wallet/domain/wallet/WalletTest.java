package com.playtomic.tests.wallet.domain.wallet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletTest {
    @Test
    public void should_generate_wallet() {
        UUID userId = UUID.randomUUID();
        String currencyCode = "EUR";

        Wallet wallet = new Wallet(userId, currencyCode);

        Assertions.assertEquals(userId, wallet.getUserId());
        Assertions.assertEquals(currencyCode, wallet.getCurrencyCode());
        Assertions.assertEquals(wallet.getBalance(), BigDecimal.ZERO);
    }
}
