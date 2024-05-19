package com.playtomic.tests.wallet.api.wallet;

import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void should_return_wallet() throws Exception {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);

        Wallet persistedWallet = walletRepository.save(wallet);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", persistedWallet.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.id").value(wallet.getId().toString()));
    }

    @Test
    public void should_return_not_found_when_no_wallet() throws Exception {
        UUID walletId = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", walletId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
