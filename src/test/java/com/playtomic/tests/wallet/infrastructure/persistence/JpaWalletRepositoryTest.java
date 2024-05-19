package com.playtomic.tests.wallet.infrastructure.persistence;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.infrastructure.external.persistence.JpaWalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Transactional
@Rollback
public class JpaWalletRepositoryTest {
    @Autowired
    private JpaWalletRepository jpaWalletRepository;

    @Test
    public void should_save_wallet() {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";

        Wallet wallet = new Wallet(userId, currency);
        Wallet savedWallet = jpaWalletRepository.save(wallet);

        Assertions.assertNotNull(savedWallet.getId());
    }

    @Test
    public void should_find_wallet_by_id() {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";

        Wallet wallet = new Wallet(userId, currency);
        Wallet savedWallet = jpaWalletRepository.save(wallet);

        Optional<Wallet> foundWallet = jpaWalletRepository.findById(savedWallet.getId());
        Assertions.assertTrue(foundWallet.isPresent());
        Assertions.assertEquals(wallet.getId(), foundWallet.get().getId());
        Assertions.assertEquals(userId, foundWallet.get().getUserId());
    }
}
